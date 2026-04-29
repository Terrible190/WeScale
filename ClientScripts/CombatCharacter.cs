using System.Collections.Generic;

public class CombatCharacter
{
    public CharacterData data;

    public float currentHP;

    public List<string> activeStates = new List<string>();

    public bool isEnemy;

    public CombatCharacter(CharacterData data, bool isEnemy)
    {
        this.data = data;
        this.isEnemy = isEnemy;
        this.currentHP = data.hp_base;
    }

    public bool IsDead()
    {
        return currentHP <= 0;
    }

    public bool HasState(string state)
    {
        return activeStates.Contains(state);
    }
}