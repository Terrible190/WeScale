package com.example.demo.api.model.states;

import com.example.demo.api.model.*;
import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.UseActionMessage_IN;
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

                UseActionMessage_IN data
                        = mapper.treeToValue(json.data, UseActionMessage_IN.class);

                Player player = msg.player();

                Personaje pj = game.getSeleccionados().get(player.getId());

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

                if (!target.isAlive()) {

                    System.out.println("[INFO] enemigo ya muerto");

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

                target.setHp(target.getHp() - damage);

                System.out.println(
                        "[ENEMY] "
                        + target.getBase().getNombre()
                        + " | HP restante: "
                        + target.getHp()
                );

                if (!target.isAlive()) {

                    System.out.println(
                            "💀 Enemigo eliminado -> "
                            + target.getInstanceId()
                    );
                }

                // =========================
                // ATAQUE ENEMIGO
                // =========================
                enemyTurn();

                // =========================
                // SIGUIENTE TURNO
                // =========================
                nextTurn();

                System.out.println(
                        "\n===== TURNO DE "
                        + getCurrentPlayer().getName()
                        + " ====="
                );

                break;
        }
    }

    // =====================================================
    // TURNO ENEMIGO
    // =====================================================
    private void enemyTurn() {

        // enemigos vivos
        List<EnemyInstance> aliveEnemies = currentNode.enemics.stream()
                .filter(EnemyInstance::isAlive)
                .toList();

        if (aliveEnemies.isEmpty()) {

            System.out.println(
                    "\n===== TODOS LOS ENEMIGOS MUERTOS ====="
            );

            return;
        }

        // elegir enemigo aleatorio
        EnemyInstance attacker
                = aliveEnemies.get(random.nextInt(aliveEnemies.size()));

        // elegir jugador aleatorio
        List<Player> players = game.getPlayers();

        if (players.isEmpty()) {
            return;
        }

        Player targetPlayer
                = players.get(random.nextInt(players.size()));

        Personaje playerCharacter
                = game.getSeleccionados().get(targetPlayer.getId());

        if (playerCharacter == null) {
            return;
        }

        float damage = attacker.getDanyoFisico();

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

        if (newHp <= 0) {

            playerCharacter.setHp(0);

            System.out.println(
                    "☠️ "
                    + targetPlayer.getName()
                    + " ha muerto"
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
    }

    // =====================================================
    // TURNOS
    // =====================================================
    private Player getCurrentPlayer() {
        return game.getPlayers().get(currentTurnIndex);
    }

    private void nextTurn() {

        currentTurnIndex++;

        if (currentTurnIndex >= game.getPlayers().size()) {
            currentTurnIndex = 0;
        }
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

            if (data == null) {
                throw new RuntimeException("JSON inválido: falta data");
            }

            JsonNode mapas = data.get("mapas");

            if (mapas == null || !mapas.isArray() || mapas.isEmpty()) {
                throw new RuntimeException("JSON inválido: no hay mapas");
            }

            JsonNode map = mapas.get(0);

            JsonNode nodes = map.get("nodes");

            if (nodes == null || !nodes.isArray() || nodes.isEmpty()) {
                throw new RuntimeException("JSON inválido: no hay nodes");
            }

            JsonNode node = nodes.get(0);

            int pis = node.get("pis").asInt();
            String tipo = node.get("tipus").asText();

            List<EnemyInstance> enemies = new ArrayList<>();

            JsonNode enemics = node.get("enemics");

            if (enemics != null && enemics.isArray()) {

                for (JsonNode e : enemics) {

                    long idPersonaje = e.get("id_personatge").asLong();

                    JsonNode escala = e.get("escala");

                    float scale = (float) escala.get("hp").asDouble();

                    Personaje base = game.getPersonajeById(idPersonaje);

                    if (base == null) {

                        System.out.println(
                                "[WARN] Personaje no encontrado: "
                                + idPersonaje
                        );

                        continue;
                    }

                    EnemyInstance enemy
                            = new EnemyInstance(base, scale);

                    enemies.add(enemy);

                    System.out.println(
                            "[ENEMY LOADED] "
                            + enemy.getInstanceId()
                            + " -> "
                            + base.getNombre()
                    );
                }
            }

            System.out.println(
                    "\nMapa cargado -> Piso "
                    + pis
                    + " | Tipo "
                    + tipo
            );

            game.broadcast(
                    new JSONMessage(
                            game.getId(),
                            new Enemy_OUT(enemies, pis)
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