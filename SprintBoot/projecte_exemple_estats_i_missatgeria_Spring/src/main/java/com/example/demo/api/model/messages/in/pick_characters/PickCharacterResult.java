/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.api.model.messages.in.pick_characters;

/**
 *
 * @author Usuari
 */
public class PickCharacterResult {

    public PickCharacterResultType type;

    public Long playerId;
    public Long characterId;

    public PickCharacterResult(PickCharacterResultType type) {
        this.type = type;
    }

    public PickCharacterResult(PickCharacterResultType type, Long playerId, Long characterId) {
        this.type = type;
        this.playerId = playerId;
        this.characterId = characterId;
    }

    public static PickCharacterResult ok(long playerId, long characterId) {
        return new PickCharacterResult(PickCharacterResultType.OK, playerId, characterId);
    }

    public static PickCharacterResult error(PickCharacterResultType type) {
        return new PickCharacterResult(type);
    }
}