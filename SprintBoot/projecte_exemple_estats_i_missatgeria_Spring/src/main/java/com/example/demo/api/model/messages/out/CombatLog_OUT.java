package com.example.demo.api.model.messages.out;

import com.example.demo.api.model.combat.CombatLogDTO;
import com.example.demo.api.model.messages.MessageBody;

import java.util.List;

public class CombatLog_OUT extends MessageBody{

    public static final String TYPE =
            "COMBAT_LOG";

    public List<CombatLogDTO> logs;

    public CombatLog_OUT(List<CombatLogDTO> logs ) {
        this.logs = logs;
    }

    @Override
    public String getMessageType() {
        return TYPE;
    }
}