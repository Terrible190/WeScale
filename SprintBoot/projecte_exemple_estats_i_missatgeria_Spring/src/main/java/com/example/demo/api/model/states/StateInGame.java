package com.example.demo.api.model.states;

import com.example.demo.api.model.Personaje;
import com.example.demo.api.model.Player;
import java.util.concurrent.TimeUnit;

import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.UseActionMessage_IN;
import com.example.demo.api.model.messages.out.GameStarted_OUT;

import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;

import tools.jackson.databind.ObjectMapper;

public class StateInGame extends State {

    private final ObjectMapper mapper = new ObjectMapper();

    private int currentTurnIndex = 0;

    private Personaje enemy;

    public StateInGame(GameInstance game) {
        super(game);

        // 🔥 enemigo temporal de prueba
        this.enemy = new Personaje(
                999,
                "Enemigo de prueba",
                200f,
                15f,
                5f,
                10f,
                8f,
                10f,
                1.5f,
                false
        );

        game.broadcast(new JSONMessage(
                game.getId(),
                new GameStarted_OUT()
        ));

        System.out.println("===== PARTIDA INICIADA =====");

        System.out.println(
                "Empieza el jugador: "
                + getCurrentPlayer().getName()
        );
    }

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

                // 🔥 comprobar turno
                if (msg.player().getId() != currentPlayer.getId()) {

                    System.out.println(
                            "[TURNO] No es el turno de "
                            + msg.player().getId()
                    );

                    break;
                }

                UseActionMessage_IN data
                        = mapper.treeToValue(json.data, UseActionMessage_IN.class);

                Player player = msg.player();

                Personaje personajeJugador
                        = game.getSeleccionados().get(player.getId());

                // seguridad
                if (personajeJugador == null) {

                    System.out.println(
                            "[ERROR] "
                            + player.getName()
                            + " no tiene personaje seleccionado"
                    );

                    break;
                }

                float damage = personajeJugador.getDanyoFisico();

                System.out.println(
                        "\n[ACTION DEBUG] Player: "
                        + player.getName()
                        + " (ID: " + player.getId() + ") "
                        + " ha usado acción: "
                        + data.getActionId()
                        + " contra "
                        + data.getTargetId()
                        + " haciendo "
                        + damage
                        + " daño"
                );

                addDaño(damage, data.getTargetId());

                System.out.println(
                        "[ENEMY] "
                        + enemy.getNombre()
                        + " | HP: "
                        + enemy.getHp()
                        + " | vivo? "
                        + enemy.isIsAlive()
                );

                // 🔥 pasar turno
                nextTurn();

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

        Player current = getCurrentPlayer();

        System.out.println(
                "\n===== TURNO DE "
                + current.getId()
                + " ====="
        );
    }

    // =====================================================
    // DAÑO
    // =====================================================

    private void addDaño(float daño, long target) {

        if (!enemy.isIsAlive()) {

            System.out.println("[ENEMY] Está muerto");

            return;
        }

        if (enemy.getId() == target) {

            float newHp = enemy.getHp() - daño;

            if (newHp <= 0) {

                enemy.setHp(0);

                enemy.setIsAlive(false);

                System.out.println(
                        "\n===== ENEMIGO ELIMINADO ====="
                );

            } else {

                enemy.setHp(newHp);
            }
        }
    }
}