package com.example.demo.components;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.example.demo.api.model.Player;

/**
 * El gestor de joc es responsabilitza de gestionar les noves connexions WS i crear les partides 
 * quan hi ha prous jugadors. Un cop establert el WS amb cada jugador, també  
 * s'encarrega de recepcionar els missatges dels WS ja connectats, redirigint-los a les
 * partides pertinents.
 */
@Component
public class GameManager {

    private long lastPlayerId = 0; 

    /** Executor de fils */
    private ExecutorService executor;
    
    /** Cua de jugadors pendents d'assignar a partida */
    private final Queue<WebSocketSession> waitingPlayers = new ConcurrentLinkedQueue<>();


    /** mapa que relaciona Ids de sessió de websocket(String) amb partides actuals (GameInstance).
     * Un websocket session id pertany a un únic player connectat.*/
    private final Map<WebSocketSession, GameInstance> sessionToGame = new ConcurrentHashMap<>();

    private final Map<WebSocketSession, Player> sessionToPlayer = new ConcurrentHashMap<>();

    /** Índex de GameInstance per game_id */
    private final Map<String, GameInstance> games = new ConcurrentHashMap<>();


    public GameManager(){
        executor = Executors.newFixedThreadPool(10);
    }

    public void onConnect(WebSocketSession session) {
        waitingPlayers.add(session);
        tryStartGame();
    }

    public void handleIncoming(WebSocketSession session, String message) {
        // Mirem si hi ha un joc associat a aquesta sessió, i si és el cas
        // encuem la petició al joc trobat.
        GameInstance game = sessionToGame.get(session);
        if (game != null) {
            Player p = sessionToPlayer.get(session);
            game.enqueue(new GameMessage(p, message));
        }
    }

    private void tryStartGame() {
        System.out.println("Number of waiting players:"+waitingPlayers.size()   );
        if (waitingPlayers.size() >= 3) {
            List<Player> players = List.of(
                new Player( lastPlayerId++, waitingPlayers.poll()),
                new Player( lastPlayerId++, waitingPlayers.poll()),
                new Player( lastPlayerId++, waitingPlayers.poll())
            );

            GameInstance game = new GameInstance(players, executor);
            String gameId = game.getId();

            // Registrem els jugadors al mapa de sessions-->joc i sessions-->Player
            players.forEach(p -> sessionToGame.put(p.getSession(), game));
            players.forEach(p -> sessionToPlayer.put(p.getSession(),p));

            // Indexem el joc pel seu Id
            games.put(gameId, game);

            // Engeguem el fil de joc
            game.start();
        }
    }
}