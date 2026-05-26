package com.example.demo.api.model.states;

import com.example.demo.api.model.*;
import com.example.demo.api.model.combat.CombatLogDTO;
import com.example.demo.api.model.combat.EffectHandler;
import com.example.demo.api.model.combat.EffectHandlerRegistry;
import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.CLIENT_READY;
import com.example.demo.api.model.messages.in.UseActionMessage_IN;
import com.example.demo.api.model.messages.out.*;
import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class StateInGame extends State {

    private final ObjectMapper mapper
            = new ObjectMapper();

    private final Random random
            = new Random();

    // =========================
    // COMBAT
    // =========================
    public enum CombatPhase {
        WAITING_ACTION,
        RESOLVING,
        WAITING_CLIENT_READY,
        GAME_OVER
    }

    private CombatPhase phase
            = CombatPhase.WAITING_ACTION;

    // =========================
    // TURNOS
    // =========================
    private int currentTurnIndex = 0;
    private int selectedPis;
    private long turnStartTime;

    private static final long TURN_TIMEOUT
            = 60000;
    private final EffectHandlerRegistry registry = new EffectHandlerRegistry();
    private final Set<Long> readyPlayers
            = new HashSet<>();
    private final Map<Integer, EffectHandler> handlers = new HashMap<>();
    // =========================
    // MAPA
    // =========================
    private int currentFloor = 1;

    private MapNode currentNode;

    // =====================================================
    // INIT
    // =====================================================
    public StateInGame(GameInstance game, int selectedPis) {
        super(game);
        this.selectedPis = selectedPis;
        currentNode
                = loadSelectedFloor();

        for (Player p : game.getPlayers()) {
            Personaje pj
                    = game.getSeleccionados()
                            .get(p.getId());

            if (pj != null) {
                pj.setIsAlive(true);
            }
        }

        game.broadcast(
                new JSONMessage(
                        game.getId(),
                        new GameStarted_OUT()
                )
        );

        System.out.println(
                "\n===== PARTIDA INICIADA ====="
        );
        startCurrentTurn();
    }

    // =====================================================
    // LOOP
    // =====================================================
    @Override
    public void tick() {
        if (phase == CombatPhase.WAITING_ACTION
                && System.currentTimeMillis()
                - turnStartTime >= TURN_TIMEOUT) {
            resolveTurnTimeout();
        }
        if (areAllEnemiesDead()) {

            System.out.println(
                    "\n===== GG ====="
            );
            game.resetCharacterSelection();
            game.setState(new StatePickCharacter(game));
            return;
        }
        GameMessage msg
                = game.pollMessage(
                        1,
                        TimeUnit.SECONDS
                );

        if (msg == null) {
            return;
        }

        JSONMessage json
                = mapper.readValue(
                        msg.payload(),
                        JSONMessage.class
                );

        switch (json.messageType) {
            case UseActionMessage_IN.TYPE:

                handleUseAction(
                        msg,
                        json
                );

                break;

            case CLIENT_READY.TYPE:

                handleClientReady(msg);

                break;
        }
    }

    // =====================================================
    // ACTIONS
    // =====================================================
    private void handleUseAction(
            GameMessage msg,
            JSONMessage json
    ) {
        if (phase != CombatPhase.WAITING_ACTION) {
            return;
        }

        Player currentPlayer
                = getCurrentPlayer();

        if (currentPlayer == null) {
            return;
        }

        if (msg.player().getId()
                != currentPlayer.getId()) {
            System.out.println(
                    "[TURN] no es su turno"
            );

            return;
        }

        UseActionMessage_IN data
                = mapper.treeToValue(
                        json.data,
                        UseActionMessage_IN.class
                );

        Personaje attacker
                = game.getSeleccionados()
                        .get(msg.player().getId());

        if (attacker == null
                || !attacker.isIsAlive()) {
            return;
        }

        Accio accion
                = attacker.getAcciones()
                        .stream()
                        .filter(a
                                -> a.getId()
                        == data.getActionId()
                        )
                        .findFirst()
                        .orElse(null);

        if (accion == null) {
            System.out.println(
                    "[ERROR] accion null"
            );

            return;
        }

        EnemyInstance targetEnemy
                = null;

        Personaje targetAlly
                = null;

        switch (accion.getTargetType()) {
            // self
            case 0:

                targetAlly
                        = attacker;

                break;

            // enemy
            case 1:

                targetEnemy
                        = currentNode.enemics
                                .stream()
                                .filter(e
                                        -> e.getInstanceId()
                                == data.getTargetId()
                                )
                                .findFirst()
                                .orElse(null);

                if (targetEnemy == null) {
                    System.out.println(
                            "[ERROR] enemigo no encontrado"
                    );

                    return;
                }

                break;

            // ally
            case 2:

                Player allyPlayer
                        = game.getPlayers()
                                .stream()
                                .filter(p
                                        -> p.getId()
                                == data.getTargetId()
                                )
                                .findFirst()
                                .orElse(null);

                if (allyPlayer != null) {
                    targetAlly
                            = game.getSeleccionados()
                                    .get(allyPlayer.getId());
                }

                if (targetAlly == null) {
                    System.out.println(
                            "[ERROR] aliado no encontrado"
                    );

                    return;
                }

                break;

            // no target
            case 4:

                break;
        }

        System.out.println(
                "\n[ACTION] "
                + attacker.getNombre()
                + " usa "
                + accion.getNombre() + accion.getTipo()
        );

        phase
                = CombatPhase.RESOLVING;

        applyEffects(
                accion,
                attacker,
                targetEnemy,
                targetAlly
        );

        checkEnemyDeath(targetEnemy);

        broadcastEnemies();

        broadcastPlayers();

        game.broadcast(
                new JSONMessage(
                        game.getId(),
                        new CombatLog_OUT(
                                game.consumeCombatLogs()
                        )
                )
        );

        enemyTurn();

        phase
                = CombatPhase.WAITING_CLIENT_READY;

        readyPlayers.clear();

        game.broadcast(
                new JSONMessage(
                        game.getId(),
                        new WAITING_CLIENT_READY()
                )
        );
    }

    private void killAllEnemies() {

        for (EnemyInstance enemy : currentNode.enemics) {

            if (enemy == null
                    || enemy.getBase() == null) {
                continue;
            }

            enemy.getBase().setHp(0);

            enemy.getBase().setIsAlive(false);

            game.broadcast(
                    new JSONMessage(
                            game.getId(),
                            new Dead_OUT(
                                    enemy.getInstanceId(),
                                    enemy.getBase().getNombre()
                            )
                    )
            );

            System.out.println(
                    "[ENEMY DEAD] "
                    + enemy.getBase().getNombre()
            );
        }

        broadcastEnemies();

        System.out.println(
                "\n===== TODOS LOS ENEMIGOS MUERTOS kae ====="
        );
    }

    // =====================================================
    // READY
    // =====================================================
    private void handleClientReady(
            GameMessage msg
    ) {
        if (phase
                != CombatPhase.WAITING_CLIENT_READY) {
            return;
        }

        readyPlayers.add(
                msg.player().getId()
        );

        long alivePlayers
                = game.getPlayers()
                        .stream()
                        .filter(p -> {
                            Personaje pj
                                    = game.getSeleccionados()
                                            .get(p.getId());

                            return pj != null
                                    && pj.isIsAlive();
                        })
                        .count();

        if (readyPlayers.size()
                >= alivePlayers) {
            readyPlayers.clear();

            phase
                    = CombatPhase.WAITING_ACTION;

            nextTurn();
        }
    }

    private boolean areAllEnemiesDead() {

        return currentNode.enemics
                .stream()
                .noneMatch(e
                        -> e.getBase().isIsAlive()
                );
    }

    // =====================================================
    // EFFECTS
    // =====================================================
    private void applyEffects(
            Accio accion,
            Personaje attacker,
            EnemyInstance targetEnemy,
            Personaje targetAlly
    ) {
        if (accion.getEfectos() == null) {
            return;
        }

        for (Efecto efecto
                : accion.getEfectos()) {
            applyEffect(
                    efecto,
                    attacker,
                    targetEnemy,
                    targetAlly
            );
        }
    }

    private void applyEffect(
            Efecto efecto,
            Personaje attacker,
            EnemyInstance targetEnemy,
            Personaje targetAlly
    ) {

        EffectHandler handler = registry.get(efecto.getId());

        if (handler == null) {
            System.out.println("[WARN] no handler for type " + efecto.getTipo());
            return;
        }

        handler.apply(
                efecto,
                attacker,
                targetEnemy,
                targetAlly,
                game
        );
    }

    private void applyDamageEffect(Efecto efecto, Personaje attacker, EnemyInstance target) {
        float damage
                = calculateDamage(
                        attacker,
                        target,
                        efecto
                );

        float hp
                = target.getBase().getHp()
                - damage;

        target.getBase().setHp(hp);
        CombatLogDTO log
                = new CombatLogDTO();

        log.type = "damage";

        log.source
                = attacker.getNombre();

        log.target
                = target.getBase().getNombre();

        log.value
                = damage;

        log.text
                = attacker.getNombre()
                + " fa "
                + damage
                + " de mal a "
                + target.getBase().getNombre();

        game.addCombatLog(log);

        System.out.println(
                "[DAMAGE] "
                + damage
        );
    }

    private void applyStatusEffect(
            Efecto efecto,
            EnemyInstance target
    ) {
        System.out.println(
                "[STATUS EFFECT]"
        );
    }

    private void applyBuffEffect(
            Efecto efecto,
            EnemyInstance targetEnemy,
            Personaje targetAlly
    ) {
        switch (efecto.getId()) {
            case 9:
                if (targetAlly == null) {
                    return;
                }
                float multiplier = 1.5f;
                float newVida
                        = targetAlly.getHp() * multiplier;
                targetAlly.setHp(newVida);

                System.out.println(
                        "[BUFF] " + targetAlly.getNombre()
                        + " aumenta un 50% de la vida"
                );
                break;
            case 2:
                //Ataca amb energia glacial aplicant Congelat durant 3 torns i reduint la velocitat un 30%.
                break;
            case 12:
                //"Marca l enemic amb Black Flash i infligeix dany físic extra."
                break;
        }
    }

    private void applyDefensaStats(
            Efecto efecto,
            Personaje targetAlly
    ) {
        switch (efecto.getId()) {
            case 4:
                if (targetAlly == null) {
                    return;
                }

                float multiplier = 1.7f;

                float newDefFisica
                        = targetAlly.getDefensaFisica() * multiplier;

                float newDefMagica
                        = targetAlly.getDefensaMagica() * multiplier;

                targetAlly.setDefensaFisica(newDefFisica);
                targetAlly.setDefensaMagica(newDefMagica);

                System.out.println(
                        "[BUFF] " + targetAlly.getNombre()
                        + " aumenta defensa 70% por 1 turno"
                );
                break;
            case 18:
                if (targetAlly == null) {
                    return;
                }

                float multiplier2 = 2.0f;
                float newDefMagica2
                        = targetAlly.getDefensaMagica() * multiplier2;
                targetAlly.setDefensaMagica(newDefMagica2);
                System.out.println(
                        "[BUFF] " + targetAlly.getNombre()
                        + " aumenta defensa magina x2"
                );
                break;
        }

    }

    private float calculateDamage(
            Personaje attacker,
            EnemyInstance target,
            Efecto efecto
    ) {
        float damage;

        if (efecto.getTipoDanyo() == 1) {
            damage
                    = attacker.getDanyoMagico()
                    - target.getBase()
                            .getDefensaMagica();
        } else {
            damage
                    = attacker.getDanyoFisico()
                    - target.getBase()
                            .getDefensaFisica();
        }

        return Math.max(1, damage);
    }

    // =====================================================
    // ENEMY TURN
    // =====================================================
    private void enemyTurn() {
        List<EnemyInstance> aliveEnemies
                = currentNode.enemics
                        .stream()
                        .filter(e
                                -> e.getBase().isIsAlive()
                        )
                        .toList();

        if (aliveEnemies.isEmpty()) {
            System.out.println(
                    "\n===== TODOS LOS ENEMIGOS MUERTOS ====="
            );

            return;
        }

        List<Player> alivePlayers
                = game.getPlayers()
                        .stream()
                        .filter(p -> {
                            Personaje pj
                                    = game.getSeleccionados()
                                            .get(p.getId());

                            return pj != null
                                    && pj.isIsAlive();
                        })
                        .toList();

        if (alivePlayers.isEmpty()) {
            System.out.println(
                    "\n===== TODOS LOS JUGADORES MUERTOS ====="
            );

            return;
        }

        EnemyInstance attacker
                = aliveEnemies.get(
                        random.nextInt(
                                aliveEnemies.size()
                        )
                );

        Player targetPlayer
                = alivePlayers.get(
                        random.nextInt(
                                alivePlayers.size()
                        )
                );

        Personaje playerCharacter
                = game.getSeleccionados()
                        .get(targetPlayer.getId());

        if (playerCharacter == null) {
            return;
        }

        float damage
                = attacker.getBase()
                        .getDanyoFisico();

        float newHp
                = playerCharacter.getHp()
                - damage;

        if (newHp <= 0) {
            playerCharacter.setHp(0);

            playerCharacter.setIsAlive(false);

            game.broadcast(
                    new JSONMessage(
                            game.getId(),
                            new Dead_OUT(
                                    targetPlayer.getId(),
                                    playerCharacter.getNombre()
                            )
                    )
            );
        } else {
            playerCharacter.setHp(newHp);
        }

        broadcastPlayers();
    }

    // =====================================================
    // TURNOS
    // =====================================================
    private void startCurrentTurn() {
        Player currentPlayer
                = getCurrentPlayer();

        if (currentPlayer == null) {
            return;
        }

        Personaje pj
                = game.getSeleccionados()
                        .get(currentPlayer.getId());

        if (pj == null) {
            return;
        }

        System.out.println(
                "\n===== TURNO DE "
                + pj.getNombre()
                + " ====="
        );

        game.send(
                currentPlayer.getSession(),
                new JSONMessage(
                        game.getId(),
                        new TURN_START_OUT()
                )
        );
        turnStartTime
                = System.currentTimeMillis();
    }

    private void nextTurn() {
        if (game.getPlayers().isEmpty()) {
            return;
        }

        int attempts = 0;

        do {
            currentTurnIndex++;

            if (currentTurnIndex
                    >= game.getPlayers().size()) {
                currentTurnIndex = 0;
            }

            Player player
                    = game.getPlayers()
                            .get(currentTurnIndex);

            Personaje pj
                    = game.getSeleccionados()
                            .get(player.getId());

            if (pj != null
                    && pj.isIsAlive()) {
                startCurrentTurn();

                return;
            }

            attempts++;

        } while (attempts
                < game.getPlayers().size());
    }

    private void resolveTurnTimeout() {
        Player player
                = getCurrentPlayer();

        if (player == null) {
            return;
        }

        System.out.println(
                "[TIMEOUT]"
        );

        nextTurn();
    }

    private Player getCurrentPlayer() {
        if (game.getPlayers().isEmpty()) {
            return null;
        }

        int attempts = 0;

        while (attempts
                < game.getPlayers().size()) {
            Player player
                    = game.getPlayers()
                            .get(currentTurnIndex);

            Personaje pj
                    = game.getSeleccionados()
                            .get(player.getId());

            if (pj != null
                    && pj.isIsAlive()) {
                return player;
            }

            currentTurnIndex++;

            if (currentTurnIndex
                    >= game.getPlayers().size()) {
                currentTurnIndex = 0;
            }

            attempts++;
        }

        return null;
    }

    // =====================================================
    // BROADCAST
    // =====================================================
    private void broadcastPlayers() {
        List<Personaje> players
                = new ArrayList<>();

        for (Player p : game.getPlayers()) {
            Personaje pj
                    = game.getSeleccionados()
                            .get(p.getId());

            players.add(pj);
        }

        game.broadcast(
                new JSONMessage(
                        game.getId(),
                        new CharactersList_OUT(players)
                )
        );
    }

    private void broadcastEnemies() {
        game.broadcast(
                new JSONMessage(
                        game.getId(),
                        new Enemy_OUT(
                                currentNode.enemics
                        )
                )
        );
    }

    private void checkEnemyDeath(
            EnemyInstance enemy
    ) {
        if (enemy == null) {
            return;
        }

        if (enemy.getBase().getHp() > 0) {
            return;
        }

        enemy.getBase().setHp(0);

        enemy.getBase().setIsAlive(false);

        game.broadcast(
                new JSONMessage(
                        game.getId(),
                        new Dead_OUT(
                                enemy.getInstanceId(),
                                enemy.getBase()
                                        .getNombre()
                        )
                )
        );
    }

    // =====================================================
    // MAPA
    // =====================================================
    private MapNode loadSelectedFloor() {

        try (
                java.io.InputStream is
                = getClass()
                        .getClassLoader()
                        .getResourceAsStream(
                                "map_fixed.json"
                        )) {

                    if (is == null) {
                        throw new RuntimeException(
                                "map_fixed.json no encontrado"
                        );
                    }

                    JsonNode root
                            = mapper.readTree(is);

                    JsonNode mapas
                            = root.get("data")
                                    .get("mapas");

                    if (mapas == null
                            || !mapas.isArray()) {

                        throw new RuntimeException(
                                "No hay mapas"
                        );
                    }

                    JsonNode selectedNode = null;

                    // =========================================
                    // BUSCAR EL PISO
                    // =========================================
                    outer:
                    for (JsonNode mapa : mapas) {

                        JsonNode nodes
                                = mapa.get("nodes");

                        if (nodes == null
                                || !nodes.isArray()) {
                            continue;
                        }

                        for (JsonNode node : nodes) {

                            int pis
                                    = node.get("pis")
                                            .asInt();

                            if (pis == selectedPis) {

                                selectedNode = node;

                                break outer;
                            }
                        }
                    }

                    if (selectedNode == null) {

                        throw new RuntimeException(
                                "Piso no encontrado: "
                                + selectedPis
                        );
                    }

                    // =========================================
                    // DATOS DEL NODE
                    // =========================================
                    int pis
                            = selectedNode.get("pis")
                                    .asInt();

                    String tipo
                            = selectedNode.get("tipus")
                                    .asText();

                    List<EnemyInstance> enemies
                            = new ArrayList<>();

                    JsonNode enemics
                            = selectedNode.get("enemics");

                    // =========================================
                    // ENEMIGOS
                    // =========================================
                    if (enemics != null
                            && enemics.isArray()) {

                        for (JsonNode e : enemics) {

                            long idPersonatge
                                    = e.get("id_personatge")
                                            .asLong();

                            JsonNode escala
                                    = e.get("escala");

                            float hpScale = 1f;

                            if (escala != null) {

                                hpScale
                                        = (float) escala
                                                .get("hp")
                                                .asDouble();
                            }

                            Personaje base
                                    = game.getPersonajeById(
                                            idPersonatge
                                    );

                            if (base == null) {
                                continue;
                            }

                            EnemyInstance enemy
                                    = new EnemyInstance(
                                            base.copy(),
                                            hpScale
                                    );

                            enemies.add(enemy);

                            System.out.println(
                                    "[ENEMY LOADED] "
                                    + enemy.getInstanceId()
                                    + " -> "
                                    + base.getNombre()
                            );
                        }
                    }

                    // =========================================
                    // CREAR MAPNODE
                    // =========================================
                    MapNode mapNode
                            = new MapNode(
                                    pis,
                                    tipo,
                                    enemies
                            );

                    this.currentNode
                            = mapNode;

                    broadcastPlayers();

                    broadcastEnemies();

                    System.out.println(
                            "[MAP] piso cargado -> "
                            + selectedPis
                    );

                    return mapNode;

                } catch (Exception e) {

                    throw new RuntimeException(
                            "Error cargando mapa",
                            e
                    );
                }
    }
}
