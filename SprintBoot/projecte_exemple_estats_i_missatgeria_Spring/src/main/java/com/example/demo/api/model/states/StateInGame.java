package com.example.demo.api.model.states;

import com.example.demo.api.model.*;
import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.UseActionMessage_IN;
import com.example.demo.api.model.messages.out.Enemy_OUT;
import com.example.demo.api.model.messages.out.GameStarted_OUT;
import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import tools.jackson.databind.JsonNode;

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
                "Empieza el jugador: " + getCurrentPlayer().getName()
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

                // 🔥 check turno
                if (msg.player().getId() != currentPlayer.getId()) {
                    System.out.println("[TURNO] No es turno de " + msg.player().getName());
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

                System.out.println(
                        "\n[ACTION] " + player.getName()
                        + " hace " + damage
                        + " daño a enemigo " + target.getInstanceId()
                );

                target.setHp(target.getHp() - damage);

                System.out.println(
                        "[ENEMY] HP restante: " + target.getHp()
                );

                if (!target.isAlive()) {
                    System.out.println("💀 enemigo eliminado");
                }

                nextTurn();

                System.out.println(
                        "Turno actual: " + getCurrentPlayer().getName()
                );

                break;
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
    // MAPA (solo primer piso)
    // =====================================================
    private MapNode loadFirstFloor() {

        try (java.io.InputStream is
                = getClass().getClassLoader().getResourceAsStream("map_fixed.json")) {

            if (is == null) {
                throw new RuntimeException("map_fixed.json no encontrado");
            }

            JsonNode root = mapper.readTree(is);

            // =========================
            // 1. ENTRAR EN DATA
            // =========================
            JsonNode data = root.get("data");
            if (data == null) {
                throw new RuntimeException("JSON inválido: falta 'data'");
            }

            // =========================
            // 2. MAPAS
            // =========================
            JsonNode mapas = data.get("mapas");
            if (mapas == null || !mapas.isArray() || mapas.isEmpty()) {
                throw new RuntimeException("JSON inválido: no hay mapas");
            }

            JsonNode map = mapas.get(0);

            int idMapa = map.get("id_mapa").asInt();
            int pisos = map.get("pisos").asInt();
            int nivelBase = map.get("nivell_base").asInt();

            // =========================
            // 3. NODES
            // =========================
            JsonNode nodes = map.get("nodes");
            if (nodes == null || !nodes.isArray() || nodes.isEmpty()) {
                throw new RuntimeException("JSON inválido: no hay nodes");
            }

            // SOLO PRIMER PISO
            JsonNode node = nodes.get(0);

            int pis = node.get("pis").asInt();
            String tipo = node.get("tipus").asText();

            List<EnemyInstance> enemies = new ArrayList<>();

            JsonNode enemics = node.get("enemics");

            if (enemics != null && enemics.isArray()) {

                for (JsonNode e : enemics) {

                    long idPersonaje = e.get("id_personatge").asLong();

                    JsonNode escala = e.get("escala");

                    // 🔥 Opción A: scale único (simple)
                    float scale = (float) escala.get("hp").asDouble();

                    Personaje base = game.getEnemyById(idPersonaje);

                    if (base == null) {
                        System.out.println("[WARN] Personaje no encontrado: " + idPersonaje);
                        continue;
                    }

                    EnemyInstance enemy = new EnemyInstance(base, scale);

                    enemies.add(enemy);
                }
            }

            System.out.println("Mapa cargado -> Piso " + pis + " | Tipo " + tipo);
            game.broadcast(new JSONMessage(game.getId(), new Enemy_OUT(enemies)));
            return new MapNode(pis, tipo, enemies);

        } catch (Exception e) {
            throw new RuntimeException("Error cargando mapa: " + e.getMessage(), e);
        }
    }
}
