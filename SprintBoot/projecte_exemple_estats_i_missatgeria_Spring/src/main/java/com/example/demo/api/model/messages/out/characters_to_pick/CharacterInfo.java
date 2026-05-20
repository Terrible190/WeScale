package com.example.demo.api.model.messages.out.characters_to_pick;

public class CharacterInfo {
    public int characterId;
    public String name;
    public String imageURL;
    public int selectedPlayerId;
    public boolean isSelected;

    public CharacterInfo(){}

    public CharacterInfo(int id, String name, String imageURL, int selectedPlayerId, boolean isSelected) {
        this.characterId = id;
        this.name = name;
        this.imageURL = imageURL;
        this.selectedPlayerId = selectedPlayerId;
        this.isSelected = isSelected;
    }
    
}
