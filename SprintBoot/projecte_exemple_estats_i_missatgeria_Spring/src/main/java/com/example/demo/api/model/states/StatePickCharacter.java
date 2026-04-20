package com.example.demo.api.model.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.example.demo.api.model.Player;
import com.example.demo.api.model.messages.JSONMessage;
import com.example.demo.api.model.messages.in.pick_characters.PickCharacterMessage_IN;
import com.example.demo.api.model.messages.out.characters_to_pick.CharacterInfo;
import com.example.demo.api.model.messages.out.characters_to_pick.PlayerInfo;
import com.example.demo.api.model.messages.out.characters_to_pick.Players2SelectMessage_OUT;
import com.example.demo.api.model.messages.out.generic.ActionResult_OUT;
import com.example.demo.components.GameInstance;
import com.example.demo.components.GameMessage;

import tools.jackson.databind.ObjectMapper;

public class StatePickCharacter extends State
{
    private ObjectMapper mapper;

    Players2SelectMessage_OUT m;

    public StatePickCharacter(GameInstance game) {
        super(game);
        mapper = new ObjectMapper();
        

       List<PlayerInfo> players = new ArrayList<>();
       for (Player p:game.getPlayers()) {
        PlayerInfo p1 = new PlayerInfo(p.getId(),"Player "+p.getId());
        players.add(p1);
       }
       
    
        //Aquí  inventem les dades...però haurien de sortir de game, que al seu temps les hauria de llegir de la 
        // base de dades i/o d'objectes en memòria.
       CharacterInfo c1 = new CharacterInfo(1, "Ork", "http://", -1, false);
       CharacterInfo c2 = new CharacterInfo(2, "Elf", "http://", -1, false);
       CharacterInfo c3 = new CharacterInfo(3, "Wizard", "http://", -1, false);
       
       List<CharacterInfo> characters = new ArrayList<>();
       characters.add(c1);
       characters.add(c2);
       characters.add(c3);  

       m = new Players2SelectMessage_OUT();
       m.characters = characters;
       m.players = players;

       JSONMessage gm = new JSONMessage(game.getId(), m);
       game.broadcast(gm);
    }

    @Override
    public void tick() {
        
        GameMessage message = game.pollMessage(5, TimeUnit.SECONDS);
        if(message!=null){
            JSONMessage gm = mapper.readValue(message.payload(), JSONMessage.class);
            System.out.println("TICK:"+gm.messageType);

            switch (gm.messageType) {
                case PickCharacterMessage_IN.TYPE:
                    pickCharacter(message.player(), gm);
                    break;
            
                default:
                    break;
            }
        }
    }

    private void pickCharacter(Player p, JSONMessage jsonMsg) {

        PickCharacterMessage_IN message = mapper.treeToValue(jsonMsg.data, PickCharacterMessage_IN.class);
        System.out.println("MESSAGE: Pick character:"+message.characterId);
        
        // Verifiquem l'id del jugador
        boolean idPlayerValid = p.getId() == message.playerId;

        if(!idPlayerValid){ 
            System.out.println("Missatge erroni, el jugador amb id :"+message.playerId+" no existeix.");
            // Missatge individual
            ActionResult_OUT result = new ActionResult_OUT(false, 1);
            game.send(p.getSession(), new JSONMessage(game.getId(), result) );
            return;
        }
        
        // Busquem personatge amb l'ID que volem ocupar 
        Optional<CharacterInfo> oc = m.characters.stream().filter(x -> x.characterId == message.characterId).findFirst();
        if(!oc.isPresent())
        {
            System.out.println("Missatge erroni, el personatge amb id :"+message.characterId+" no existeix.");
            // Missatge individual
            ActionResult_OUT result = new ActionResult_OUT(false, 2);
            game.send(p.getSession(), new JSONMessage(game.getId(), result) );
            return;

        } else {

            // Si el trobem, mirem si ja està seleccionat.
            CharacterInfo ci = oc.get();

            if(!ci.isSelected){

                // si no està seleccionat, assignem l'id del jugador al personatge 
                ci.selectedPlayerId = message.playerId;
                ci.isSelected = true;

                // Missatge individual
                ActionResult_OUT result = new ActionResult_OUT(true, 0);
                game.send(p.getSession(), new JSONMessage(game.getId(), result) );

                // Missatge a tothom amb l'actualització de les assignacions.
                JSONMessage ogm = new JSONMessage(game.getId(),m);
                game.broadcast(ogm);

                // Si tots els jugadors estan assignats, passem a l'estat següent !
                if(m.characters.stream().filter(x -> !x.isSelected).count()==0){                    
                    game.setState(new StateMap(game));
                }
            }
        }     
    }
}