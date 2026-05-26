package com.example.demo.api.model.states;

import com.example.demo.api.model.*;
import com.example.demo.api.model.combat.CombatLogDTO;
import com.example.demo.api.model.combat.CombatResolver;
import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.CLIENT_READY;
import com.example.demo.api.model.messages.in.UseActionMessage_IN;
import com.example.demo.api.model.messages.out.*;
import com.example.demo.api.model.messages.out.show_map.ShowMapMessage_OUT;
import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;
import com.example.demo.api.model.combat.CombatResolver;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class StateInGame extends State {

    private final ObjectMapper mapper
            = new ObjectMapper();

    private final Random random
            = new Random();

    public enum CombatPhase {
        WAITING_ACTION,
        RESOLVING,
        WAITING_CLIENT_READY,
        GAME_OVER
    }

    private CombatPhase phase
            = CombatPhase.WAITING_ACTION;

    private int currentTurnIndex = 0;

    private int selectedPis;

    private long turnStartTime;

    private static final long TURN_TIMEOUT
            = 60000;

    private final Set<Long> readyPlayers
            = new HashSet<>();

    private MapNode currentNode;

    // =====================================================
    // INIT
    // =====================================================

    public StateInGame(
            GameInstance game,
            int selectedPis
    ) {
        super(game);

        this.selectedPis = selectedPis;

        currentNode = loadSelectedFloor();

       for (Player p : game.getPlayers()) {

        Personaje pj =
                game.getSeleccionados()
                        .get(p.getId());

        if (pj != null) {

                pj.setIsAlive(true);

                pj.setCurrentHp(
                        pj.getHp()
                );
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

        // =========================================
        // TIMEOUT
        // =========================================

        if (
                phase == CombatPhase.WAITING_ACTION
                &&
                System.currentTimeMillis()
                - turnStartTime >= TURN_TIMEOUT
        ) {
            resolveTurnTimeout();
        }

        // =========================================
        // GG
        // =========================================

        /* instakill enemics  
        if (areAllEnemiesDead()) {
                phase = CombatPhase.GAME_OVER;

                game.broadcast(
                        new JSONMessage(
                        game.getId(),
                        new ShowMapMessage_OUT()
                        )
                );

                game.setState(
                        new StateMap(game)
                );

                return;
        }*/
        // =========================================
        // MESSAGES
        // =========================================

        GameMessage msg =
                game.pollMessage(
                        1,
                        TimeUnit.SECONDS
                );
   
        if (msg == null)
            return;

             System.out.println(
        "[QUEUE MSG] " +
        msg.payload()
        );

        JSONMessage json =
                mapper.readValue(
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

        if (
                phase
                != CombatPhase.WAITING_ACTION
        ) {
            return;
        }

        Player currentPlayer =
                getCurrentPlayer();

        if (currentPlayer == null)
            return;

        if (
                msg.player().getId()
                != currentPlayer.getId()
        ) {
            return;
        }

        UseActionMessage_IN data =
                mapper.treeToValue(
                        json.data,
                        UseActionMessage_IN.class
                );

        Personaje attacker =
                game.getSeleccionados()
                        .get(
                                msg.player().getId()
                        );
        
        if (
                attacker == null
                ||
                !attacker.isIsAlive()
        ) {
            return;
        }

System.out.println(
        "[ATTACKER] "
        + attacker.getNombre()
        + " HP="
        + attacker.getCurrentHp()
        + " ALIVE="
        + attacker.isIsAlive()
        );

        Accio accion =
                attacker.getAcciones()
                        .stream()
                        .filter(a ->
                                a.getId()
                                        ==
                                        data.getActionId()
                        )
                        .findFirst()
                        .orElse(null);

        if (accion == null){
                System.out.println("accio NULL");
                return;
        }

                        System.out.println("accio no NULL");

        EnemyInstance targetEnemy = null;

        List<EnemyInstance> allTargets = null;

        // =====================================
        // SINGLE TARGET
        // =====================================
                System.out.println("datatarget " +data.getTargetId() );

        if (data.getTargetId() != -1)
        {
        targetEnemy =
                currentNode.enemics
                        .stream()
                        .filter(e ->
                                e.getInstanceId() == data.getTargetId()
                        )
                        .findFirst()
                        .orElse(null);


        if (targetEnemy == null){
                System.out.println("targetEnemy null");
                return;
        }
        }
        // =====================================
        // ALL ENEMIES
        // =====================================

        else
        {
        allTargets =
                currentNode.enemics
                        .stream()
                        .filter(e ->
                                e.getBase().isIsAlive()
                        )
                        .toList();
        }
        phase = CombatPhase.RESOLVING;

        System.out.println(
        "[TARGET ENEMY HASH] "
        + targetEnemy.getInstanceId()
        + " HASH="
        + System.identityHashCode(targetEnemy)
        );
        // =========================================
        // DAMAGE
        // =========================================
       /*  for (Efecto efecto : accion.getEfectos())
                {
                if (efecto.getTipo() != 0)
                        continue;

                float damage;

                // =========================
                // MAGIC DAMAGE
                // =========================

                if (
                        efecto.getTipoDanyo() != null
                        &&
                        efecto.getTipoDanyo() == 1
                )
                {
                        damage =
                        attacker.getDanyoMagico()
                        - targetEnemy.getBase()
                                .getDefensaMagica();

                        System.out.println(
                        "[MAGIC DAMAGE]"
                        );
                }

                // =========================
                // PHYSICAL DAMAGE
                // =========================

                else
                {
                        damage =
                        attacker.getDanyoFisico()
                        - targetEnemy.getBase()
                                .getDefensaFisica();

                        System.out.println(
                        "[PHYSICAL DAMAGE]"
                        );
                }

                damage = Math.max(
                        1,
                        damage
                );

                float hp = targetEnemy.getBase().getCurrentHp() - damage;

                targetEnemy.getBase().setCurrentHp(hp);

                if (hp <= 0)
                {
                        targetEnemy.getBase().setCurrentHp(0);
                        
                        targetEnemy.getBase()
                        .setIsAlive(false);

                        game.broadcast(
                        new JSONMessage(
                                game.getId(),
                                new Dead_OUT(
                                targetEnemy.getInstanceId(),
                                targetEnemy
                                        .getBase()
                                        .getNombre()
                                )
                        )
                        );
                }
        }
        */

        System.out.println("Nigga combat actions");
       List<CombatResolver.CombatAction> actions =
        new ArrayList<>();

        // =========================================
        // PLAYER ACTION
        // =========================================

        CombatResolver.CombatAction playerAction =
                new CombatResolver.CombatAction();
        System.out.println(
        "[CREATE ACTION] enemy="
        + playerAction.enemy
        );
        playerAction.enemy = false;

        playerAction.casterId =
                attacker.getId();

        playerAction.casterPlayer =
                attacker;

        playerAction.enemyTarget =
        targetEnemy;

        playerAction.allEnemyTargets =
                allTargets;
       playerAction.action =
        accion;
        if (accion == null)
        {
        System.out.println(
                "[ACTION IS NULL]"
        );

        return;
        }


        playerAction.action =
                accion;
 

        playerAction.speed =
                (int) attacker.getVelocidad();
        System.out.println(
        "[ACTION OBJECT] "
        + playerAction.action
        );
        System.out.println(
        "[PLAYER ACTION DATA] enemy="
        + playerAction.enemy
        + " caster="
        + playerAction.casterId
        + " target="
        + (
                playerAction.enemyTarget != null
                ? playerAction.enemyTarget.getInstanceId()
                : "NULL"
        )
        + " action="
        + (
                playerAction.action != null
                ? playerAction.action.getId()
                : "NULL"
        )
        );
        actions.add(playerAction);

        // =========================================
        // ENEMY ACTIONS
        // =========================================
        System.out.println("[BEFORE ENEMY ACTIONS]");
        actions.addAll(
                CombatResolver.createEnemyActions(
                        currentNode.enemics,
                        game.getPlayers(),
                        game
                )
        );
        System.out.println("[AFTER ENEMY ACTIONS]");
        // =========================================
        // RESOLVE
        // =========================================

        //CombatResolver.resolveTurn(actions);

        List<CombatResolver.CombatEvent> events =
        CombatResolver.resolveTurn(actions);
        for (CombatResolver.CombatEvent event : events)
        {
                System.out.println(
                        "[EVENT] "
                        + event.type
                );
        }
        // =========================================
        // UPDATE CLIENTS
        // =========================================

        broadcastEnemies();

        broadcastPlayers();

        // =========================================
        // ENEMY TURN
        // =========================================

       // enemyTurn();

       phase = CombatPhase.WAITING_ACTION;

        nextTurn();
    }

    // =====================================================
    // READY
    // =====================================================

    private void handleClientReady(
            GameMessage msg
    ) {

        if (
                phase
                != CombatPhase.WAITING_CLIENT_READY
        ) {
            return;
        }

        readyPlayers.add(
                msg.player().getId()
        );

        long alivePlayers =
                game.getPlayers()
                        .stream()
                        .filter(p -> {

                            Personaje pj =
                                    game.getSeleccionados()
                                            .get(p.getId());

                            return pj != null
                                    && pj.isIsAlive();
                        })
                        .count();

        if (
                readyPlayers.size()
                        >= alivePlayers
        ) {

            readyPlayers.clear();

            phase =
                    CombatPhase.WAITING_ACTION;

            nextTurn();
        }
    }

    // =====================================================
    // ENEMIES
    // =====================================================

    private boolean areAllEnemiesDead() {

        return currentNode.enemics
                .stream()
                .noneMatch(e ->
                        e.getBase().isIsAlive()
                );
    }
/* 
    private void enemyTurn() {

        List<EnemyInstance> aliveEnemies =
                currentNode.enemics
                        .stream()
                        .filter(e ->
                                e.getBase().isIsAlive()
                        )
                        .toList();

        if (aliveEnemies.isEmpty())
            return;

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

        if (alivePlayers.isEmpty())
            return;

        EnemyInstance attacker =
                aliveEnemies.get(
                        random.nextInt(
                                aliveEnemies.size()
                        )
                );

        Player targetPlayer =
                alivePlayers.get(
                        random.nextInt(
                                alivePlayers.size()
                        )
                );

        Personaje target =
                game.getSeleccionados()
                        .get(targetPlayer.getId());

        if (target == null)
            return;

        float damage =
                attacker.getBase()
                        .getDanyoFisico();

        float newHp = target.getCurrentHp() - damage;

        target.setCurrentHp(newHp);

        if (newHp <= 0) {

            target.setCurrentHp(0);

            target.setIsAlive(false);

            game.broadcast(
                    new JSONMessage(
                            game.getId(),
                            new Dead_OUT(
                                    targetPlayer.getId(),
                                    target.getNombre()
                            )
                    )
            );
        }

        broadcastPlayers();
    }
*/
    // =====================================================
    // TURNOS
    // =====================================================

    private void startCurrentTurn() {

        Player currentPlayer =
                getCurrentPlayer();

        if (currentPlayer == null)
            return;

        Personaje pj =
                game.getSeleccionados()
                        .get(currentPlayer.getId());

        if (pj == null)
            return;

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

        turnStartTime =
                System.currentTimeMillis();
    }

    private void nextTurn() {

        if (game.getPlayers().isEmpty())
            return;

        int attempts = 0;

        do {

            currentTurnIndex++;

            if (
                    currentTurnIndex
                            >= game.getPlayers().size()
            ) {
                currentTurnIndex = 0;
            }

            Player player =
                    game.getPlayers()
                            .get(currentTurnIndex);

            Personaje pj =
                    game.getSeleccionados()
                            .get(player.getId());

            if (
                    pj != null
                    &&
                    pj.isIsAlive()
            ) {

                startCurrentTurn();

                return;
            }

            attempts++;

        } while (
                attempts
                        < game.getPlayers().size()
        );
    }

    private void resolveTurnTimeout() {

        nextTurn();
    }

    private Player getCurrentPlayer() {

        if (game.getPlayers().isEmpty())
            return null;

        return game.getPlayers()
                .get(currentTurnIndex);
    }

    // =====================================================
    // BROADCAST
    // =====================================================

    private void broadcastPlayers() {

        List<Personaje> players =
                new ArrayList<>();

        for (Player p : game.getPlayers()) {

            Personaje pj =
                    game.getSeleccionados()
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
        for (EnemyInstance e : currentNode.enemics)
        {

                System.out.println(
    "[SEND ENEMY INSTANCE] "
    + e.getInstanceId()
    + " HP="
    + e.getBase().getCurrentHp()
    + " HASH="
    + System.identityHashCode(e)
);
        System.out.println(
                "[SEND ENEMY] "
                + e.getBase().getNombre()
                + " HP="
                + e.getBase().getHp()
                + " CURRENT="
                + e.getBase().getCurrentHp()
                + " ALIVE="
                + e.getBase().isIsAlive()
        );
        }
    }

    // =====================================================
    // MAPA
    // =====================================================

    private MapNode loadSelectedFloor() {

        try (
                java.io.InputStream is =
                        getClass()
                                .getClassLoader()
                                .getResourceAsStream(
                                        "map_fixed.json"
                                )
        ) {

            JsonNode root =
                    mapper.readTree(is);

            JsonNode mapas =
                    root.get("data")
                            .get("mapas");

            JsonNode selectedNode = null;

            outer:
            for (JsonNode mapa : mapas) {

                JsonNode nodes =
                        mapa.get("nodes");

                for (JsonNode node : nodes) {

                    int pis =
                            node.get("pis")
                                    .asInt();

                    if (pis == selectedPis) {

                        selectedNode = node;

                        break outer;
                    }
                }
            }

            if (selectedNode == null) {

                throw new RuntimeException(
                        "Piso no encontrado"
                );
            }

            List<EnemyInstance> enemies =
                    new ArrayList<>();

            JsonNode enemics =
                    selectedNode.get("enemics");

            if (
                    enemics != null
                    &&
                    enemics.isArray()
            ) {

                for (JsonNode e : enemics) {

                    long idPersonatge =
                            e.get("id_personatge")
                                    .asLong();

                    Personaje base =
                            game.getPersonajeById(
                                    idPersonatge
                            );

                    if (base == null)
                        continue;


                    
                System.out.println(
                "BASE HP ORIGINAL: "
                + base.getHp()
                );
                Personaje copia = base.copy();

        copia.setCurrentHp(base.getHp());


                EnemyInstance enemy =
                new EnemyInstance(
                        copia,
                        1f
                );

                /*enemy.getBase().setHp(
                        enemy.getBase().getHp()
                );*/

                enemy.getBase().setIsAlive(true);

                enemies.add(enemy);
                    System.out.println(
                            "[ENEMY LOADED] "
                                    + enemy.getInstanceId()
                                    + " -> "
                                    + base.getNombre()
                    );
                }
            }

            MapNode mapNode =
                    new MapNode(
                            selectedPis,
                            "combat",
                            enemies
                    );

            this.currentNode =
                    mapNode;

            broadcastPlayers();

            broadcastEnemies();

            return mapNode;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}