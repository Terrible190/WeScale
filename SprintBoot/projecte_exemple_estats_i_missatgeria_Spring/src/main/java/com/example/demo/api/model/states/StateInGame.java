package com.example.demo.api.model.states;

import com.example.demo.api.model.*;
import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.UseActionMessage_IN;
import com.example.demo.api.model.messages.out.CharactersList_OUT;
import com.example.demo.api.model.messages.out.Dead_OUT;
import com.example.demo.api.model.messages.out.Enemy_OUT;
import com.example.demo.api.model.messages.out.GameStarted_OUT;
import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class StateInGame extends State {

    private final ObjectMapper mapper = new ObjectMapper();

    // =========================
    // TURNOS
    // =========================
    private int currentTurnIndex = 0;

    // =========================
    // MAPA
    // =========================
    private int currentFloor = 0;
    private MapNode currentNode;

    // =========================
    // RANDOM
    // =========================
    private final Random random = new Random();

    // =========================
    // INIT
    // =========================
    public StateInGame(GameInstance game) {
        super(game);

        this.currentFloor = 1;
        this.currentNode = loadFirstFloor();

        game.broadcast(new JSONMessage(
                game.getId(),
                new GameStarted_OUT()
        ));

        System.out.println("===== PARTIDA INICIADA =====");

        System.out.println("Piso: " + currentNode.pis);
        System.out.println("Tipo: " + currentNode.tipus);
        System.out.println("Enemigos: " + currentNode.enemics.size());

        System.out.println(
                "Empieza el jugador: "
                + getCurrentPlayer().getName()
        );
    }

    // =====================================================
    // LOOP
    // =====================================================
    @Override
    public void tick() {

        GameMessage msg = game.pollMessage(1, TimeUnit.SECONDS);

        if (msg == null) {
            return;
        }

        JSONMessage json = mapper.readValue(msg.payload(), JSONMessage.class);

        switch (json.messageType) {

            case UseActionMessage_IN.TYPE:

                Player currentPlayer = getCurrentPlayer();

                if (currentPlayer == null) {
                    System.out.println("[GAME] No quedan jugadores vivos");
                    return;
                }

                // =========================
                // CHECK TURNO
                // =========================
                if (msg.player().getId() != currentPlayer.getId()) {

                    System.out.println(
                            "[TURNO] No es turno de "
                            + msg.player().getName()
                    );

                    break;
                }

                Personaje currentCharacter =
                        game.getSeleccionados().get(msg.player().getId());

                // =========================
                // JUGADOR MUERTO
                // =========================
                if (currentCharacter == null || !currentCharacter.isIsAlive()) {

                    System.out.println(
                            "[INFO] jugador muerto: "
                            + msg.player().getName()
                    );

                    nextTurn();

                    break;
                }

                UseActionMessage_IN data =
                        mapper.treeToValue(json.data, UseActionMessage_IN.class);

                Player player = msg.player();

                Personaje pj =
                        game.getSeleccionados().get(player.getId());

                if (pj == null) {

                    System.out.println("[ERROR] Sin personaje");

                    break;
                }

                float damage = pj.getDanyoFisico();

                // =========================
                // BUSCAR ENEMIGO
                // =========================
                EnemyInstance target = currentNode.enemics.stream()
                        .filter(e -> e.getInstanceId() == data.getTargetId())
                        .findFirst()
                        .orElse(null);

                if (target == null) {

                    System.out.println("[ERROR] enemigo no encontrado");

                    break;
                }

                // =========================
                // ENEMIGO MUERTO
                // =========================
                if (!target.getBase().isIsAlive()) {

                    System.out.println(
                            "[INFO] enemigo ya muerto"
                    );

                    break;
                }

                // =========================
                // ATAQUE JUGADOR
                // =========================
                System.out.println(
                        "\n[ACTION] "
                        + player.getName()
                        + " usa acción "
                        + data.getActionId()
                        + " contra enemigo "
                        + target.getInstanceId()
                );

                System.out.println(
                        "[DAMAGE] "
                        + damage
                        + " daño"
                );

                float enemyHp = target.getBase().getHp() - damage;

                target.getBase().setHp(enemyHp);

                // =========================
                // MUERTE ENEMIGO
                // =========================
                if (enemyHp <= 0) {

                    target.getBase().setHp(0);
                    target.getBase().setIsAlive(false);

                    System.out.println(
                            "💀 Enemigo eliminado -> "
                            + target.getInstanceId()
                    );

                    game.broadcast(
                            new JSONMessage(
                                    game.getId(),
                                    new Dead_OUT(
                                            target.getInstanceId(),
                                            target.getBase().getNombre()
                                    )
                            )
                    );
                }

                System.out.println(
                        "[ENEMY] "
                        + target.getBase().getNombre()
                        + " | HP restante: "
                        + target.getBase().getHp()
                );

                // =========================
                // UPDATE ENEMIES
                // =========================
                game.broadcast(
                        new JSONMessage(
                                game.getId(),
                                new Enemy_OUT(currentNode.enemics)
                        )
                );

                // =========================
                // ATAQUE ENEMIGO
                // =========================
                enemyTurn();

                // =========================
                // SIGUIENTE TURNO
                // =========================
                nextTurn();

                Player nextPlayer = getCurrentPlayer();

                if (nextPlayer != null) {

                    System.out.println(
                            "\n===== TURNO DE "
                            + nextPlayer.getName()
                            + " ====="
                    );
                }

                break;
        }
    }

    // =====================================================
    // TURNO ENEMIGO
    // =====================================================
    private void enemyTurn() {

        // enemigos vivos
        List<EnemyInstance> aliveEnemies = currentNode.enemics.stream()
                .filter(e -> e.getBase().isIsAlive())
                .toList();

        if (aliveEnemies.isEmpty()) {

            System.out.println(
                    "\n===== TODOS LOS ENEMIGOS MUERTOS ====="
            );

            return;
        }

        // jugadores vivos
        List<Player> alivePlayers = game.getPlayers().stream()
                .filter(p -> {
                    Personaje pj =
                            game.getSeleccionados().get(p.getId());

                    return pj != null && pj.isIsAlive();
                })
                .toList();

        if (alivePlayers.isEmpty()) {

            System.out.println(
                    "\n===== TODOS LOS JUGADORES MUERTOS ====="
            );

            return;
        }

        // enemigo random
        EnemyInstance attacker =
                aliveEnemies.get(random.nextInt(aliveEnemies.size()));

        // jugador random vivo
        Player targetPlayer =
                alivePlayers.get(random.nextInt(alivePlayers.size()));

        Personaje playerCharacter =
                game.getSeleccionados().get(targetPlayer.getId());

        if (playerCharacter == null) {
            return;
        }

        float damage = attacker.getBase().getDanyoFisico();

        System.out.println(
                "\n[ENEMY TURN] "
                + attacker.getBase().getNombre()
                + " (Enemy ID "
                + attacker.getInstanceId()
                + ") ataca a "
                + targetPlayer.getName()
        );

        System.out.println(
                "[ENEMY DAMAGE] "
                + damage
                + " daño"
        );

        float newHp = playerCharacter.getHp() - damage;

        // =========================
        // MUERTE JUGADOR
        // =========================
        if (newHp <= 0) {

            playerCharacter.setHp(0);
            playerCharacter.setIsAlive(false);

            System.out.println(
                    "☠️ "
                    + targetPlayer.getName()
                    + " ha muerto"
            );

            game.broadcast(
                    new JSONMessage(
                            game.getId(),
                            new Dead_OUT(
                                    targetPlayer.getId(),
                                    targetPlayer.getName()
                            )
                    )
            );

        } else {

            playerCharacter.setHp(newHp);

            System.out.println(
                    "[PLAYER HP] "
                    + targetPlayer.getName()
                    + " -> "
                    + playerCharacter.getHp()
                    + " HP"
            );
        }

        // =========================
        // UPDATE PLAYERS
        // =========================
        List<Personaje> jugadors = new ArrayList<>();

        for (Player a : game.getPlayers()) {

            Personaje p =
                    game.getSeleccionados().get(a.getId());

            jugadors.add(p);
        }

        game.broadcast(
                new JSONMessage(
                        game.getId(),
                        new CharactersList_OUT(jugadors)
                )
        );
    }

    // =====================================================
    // TURNOS
    // =====================================================
    private Player getCurrentPlayer() {

        if (game.getPlayers().isEmpty()) {
            return null;
        }

        int attempts = 0;

        while (attempts < game.getPlayers().size()) {

            Player player =
                    game.getPlayers().get(currentTurnIndex);

            Personaje pj =
                    game.getSeleccionados().get(player.getId());

            if (pj != null && pj.isIsAlive()) {
                return player;
            }

            currentTurnIndex++;

            if (currentTurnIndex >= game.getPlayers().size()) {
                currentTurnIndex = 0;
            }

            attempts++;
        }

        return null;
    }

    private void nextTurn() {

        if (game.getPlayers().isEmpty()) {
            return;
        }

        int attempts = 0;

        do {

            currentTurnIndex++;

            if (currentTurnIndex >= game.getPlayers().size()) {
                currentTurnIndex = 0;
            }

            Player player =
                    game.getPlayers().get(currentTurnIndex);

            Personaje pj =
                    game.getSeleccionados().get(player.getId());

            if (pj != null && pj.isIsAlive()) {
                return;
            }

            attempts++;

        } while (attempts < game.getPlayers().size());

        System.out.println("[GAME] No quedan jugadores vivos");
    }

    // =====================================================
    // MAPA
    // =====================================================
    private MapNode loadFirstFloor() {

        try (java.io.InputStream is
                     = getClass().getClassLoader().getResourceAsStream("map_fixed.json")) {

            if (is == null) {
                throw new RuntimeException("map_fixed.json no encontrado");
            }

            JsonNode root = mapper.readTree(is);

            JsonNode data = root.get("data");

            JsonNode mapas = data.get("mapas");

            JsonNode map = mapas.get(0);

            JsonNode nodes = map.get("nodes");

            JsonNode node = nodes.get(0);

            int pis = node.get("pis").asInt();

            String tipo = node.get("tipus").asText();

            List<EnemyInstance> enemies = new ArrayList<>();

            JsonNode enemics = node.get("enemics");

            if (enemics != null && enemics.isArray()) {

                for (JsonNode e : enemics) {

                    long idPersonaje =
                            e.get("id_personatge").asLong();

                    JsonNode escala =
                            e.get("escala");

                    float scale =
                            (float) escala.get("hp").asDouble();

                    Personaje base =
                            game.getPersonajeById(idPersonaje);

                    if (base == null) {
                        continue;
                    }

                    EnemyInstance enemy =
                            new EnemyInstance(base.copy(), scale);

                    enemies.add(enemy);

                    System.out.println(
                            "[ENEMY LOADED] "
                            + enemy.getInstanceId()
                            + " -> "
                            + base.getNombre()
                    );
                }
            }

            game.broadcast(
                    new JSONMessage(
                            game.getId(),
                            new Enemy_OUT(enemies)
                    )
            );

            List<Personaje> jugadors = new ArrayList<>();

            for (Player a : game.getPlayers()) {

                Personaje p =
                        game.getSeleccionados().get(a.getId());

                jugadors.add(p);
            }

            game.broadcast(
                    new JSONMessage(
                            game.getId(),
                            new CharactersList_OUT(jugadors)
                    )
            );

            return new MapNode(pis, tipo, enemies);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error cargando mapa: "
                    + e.getMessage(),
                    e
            );
        }
    }
}