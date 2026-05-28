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

            game.getCompletedFloors().add(game.getSelectedPis());

            game.broadcast(
                new JSONMessage(
                        game.getId(),
                        new GAME_WIN_OUT()
                )
                );
            game.resetCharacterSelection();
                game.setState(
                new StatePickCharacter(game)
                );           
                 return;
        }
        if (areAllPlayersDead()) {

        System.out.println(
                "\n===== GAME OVER ====="
        );

        game.broadcast(
                new JSONMessage(
                        game.getId(),
                        new GAME_LOSE_OUT()
                )
        );

        game.resetCharacterSelection();

        game.setState(
                new StatePickCharacter(game)
        );

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
private boolean areAllPlayersDead() {

    return game.getPlayers()
            .stream()
            .noneMatch(p -> {

                Personaje pj =
                        game.getSeleccionados()
                                .get(p.getId());

                return pj != null
                        && pj.isIsAlive();
            });
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
System.out.println(
    "[ACCION] "
    + accion.getNombre()
);

System.out.println(
    "[EFECTOS NULL] "
    + (accion.getEfectos() == null)
);

if (accion.getEfectos() != null) {

    System.out.println(
        "[EFECTOS SIZE] "
        + accion.getEfectos().size()
    );
}
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
        System.out.println(
        "[TARGET TYPE] "
        + accion.getTargetType()
        );
        switch (accion.getTargetType()) {
            // self
            case 0:

                targetAlly
                        = attacker;

                break;

            // enemy
           // enemy
case 1:

    System.out.println(
        "[TARGET ID RECIBIDO] "
        + data.getTargetId()
    );

    for (EnemyInstance e : currentNode.enemics) {

        System.out.println(
            "[ENEMY INSTANCE] "
            + e.getInstanceId()
        );
    }

        targetEnemy
            = currentNode.enemics
                    .stream()
                    .filter(e
                            -> e.getInstanceId()
                    == data.getTargetId()
                    )
                    .findFirst()
                    .orElse(null);

/*targetEnemy =
    currentNode.enemics.get(0);*/
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

        System.out.println("enemic dins applyeffect " + targetEnemy);

        System.out.println(
        "[EFFECT] id="
        + efecto.getId()
        + " tipo="
        + efecto.getTipo()
        );
        EffectHandler handler = registry.get(efecto.getTipo());
        
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
                System.out.println("Daño " + damage );
                System.out.println("Vida " + target.getBase().getHp() );
                System.out.println("Perosnaje herido " + target.getBase().getNombre());
                System.out.println("Daño desde" + attacker.getNombre());
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


    private float calculateDamage(
        Personaje attacker,
        EnemyInstance target,
        Efecto efecto
        ) {
                return attacker.getDanyoFisico();
        }
    // =====================================================
    // ENEMY TURN 
    // =====================================================
    private void enemyTurn() {

    List<EnemyInstance> aliveEnemies =
            currentNode.enemics
                    .stream()
                    .filter(e -> e.getBase().isIsAlive())
                    .toList();

    if (aliveEnemies.isEmpty()) {

        System.out.println(
                "\n===== TODOS LOS ENEMIGOS MUERTOS ====="
        );

        return;
    }

    List<Player> alivePlayers =
            game.getPlayers()
                    .stream()
                    .filter(p -> {

                        Personaje pj =
                                game.getSeleccionados()
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

    for (EnemyInstance attacker : aliveEnemies) {

        Player targetPlayer =
                alivePlayers.get(
                        random.nextInt(
                                alivePlayers.size()
                        )
                );

        Personaje playerCharacter =
                game.getSeleccionados()
                        .get(targetPlayer.getId());

        if (playerCharacter == null
                || !playerCharacter.isIsAlive()) {
            continue;
        }

        float damage =
                attacker.getBase()
                        .getDanyoFisico();

        System.out.println(
                "[ENEMY ATTACK] "
                + attacker.getBase().getNombre()
                + " -> "
                + playerCharacter.getNombre()
        );

        System.out.println(
                "[HP BEFORE] "
                + playerCharacter.getHp()
        );

        float newHp =
                playerCharacter.getHp()
                - damage;

        System.out.println(
                "[DAMAGE] "
                + damage
        );

        System.out.println(
                "[HP AFTER] "
                + newHp
        );

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

            System.out.println(
                    "[PLAYER DEAD] "
                    + playerCharacter.getNombre()
            );

        } else {

            playerCharacter.setHp(newHp);
        }
    }

    System.out.println(
            "[BROADCAST PLAYERS]"
    );

    for (Player p : game.getPlayers()) {

        Personaje pj =
                game.getSeleccionados()
                        .get(p.getId());

        if (pj == null)
            continue;

        System.out.println(
                pj.getNombre()
                + " HP="
                + pj.getHp()
        );
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
        processEffects(pj);
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


    private void processEffects(
        Personaje pj)
{
    Iterator<ActiveEffect> it =
        pj.getActiveEffects().iterator();

    while(it.hasNext())
    {
        ActiveEffect active =
            it.next();

        Efecto efecto =
            active.getEfecto();
        System.out.println(
        efecto.getClass().getName()
        );
       
       if(
        efecto.getStat() != null
        )
        {
        aplicarModificador(
                pj,
                efecto
        );
        }
        active.reduceTurn();

        if(active.getTurns() <= 0)
        {
            it.remove();
        }
    }
}
private void aplicarModificador(
    Personaje pj,
    Efecto efecto
)
{
    System.out.println(
        "[MOD] stat="
        + efecto.getStat()
        + " valor="
        + efecto.getValor()
    );

    if(efecto.getStat() == null)
        return;

    switch(efecto.getStat())
    {
        case 1:

            pj.setHp(
                pj.getHp()
                + efecto.getValor()
            );

            break;

        case 2:

            pj.setDanyoFisico(
                pj.getDanyoFisico()
                + efecto.getValor()
            );

            break;

        case 3:

            pj.setDanyoMagico(
                pj.getDanyoMagico()
                + efecto.getValor()
            );

            break;

        case 4:

            pj.setDefensaFisica(
                pj.getDefensaFisica()
                + efecto.getValor()
            );

            break;

        case 5:

            pj.setDefensaMagica(
                pj.getDefensaMagica()
                + efecto.getValor()
            );

            break;

        case 6:

            pj.setVelocidad(
                pj.getVelocidad()
                + efecto.getValor()
            );

            break;
    }
} 
}