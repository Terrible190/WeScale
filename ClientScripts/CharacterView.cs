using UnityEngine;

public class CharacterView : MonoBehaviour
{
    public CombatCharacter data;

    public void Init(CombatCharacter character)
    {
        data = character;
    }
}