using System.Collections.Generic;
using UnityEngine;

public class BattleSystem : MonoBehaviour
{
    public SpawnManager spawner;

    public List<CombatCharacter> allies = new List<CombatCharacter>();
    public List<CombatCharacter> enemies = new List<CombatCharacter>();
    public void LoadFromServer(BattleDTO dto)
    {
        allies.Clear();
        enemies.Clear();

        foreach (var c in dto.allies)
        {
            allies.Add(new CombatCharacter(
                new CharacterData
                {
                    nom = c.nom,
                    hp_base = c.hp,
                    isBoss = false
                }, false));
        }

        foreach (var c in dto.enemies)
        {
            enemies.Add(new CombatCharacter(
                new CharacterData
                {
                    nom = c.nom,
                    hp_base = c.hp,
                    isBoss = c.isBoss
                }, true));
        }

        SpawnAll();
    }
 /*  Testing monousuari client no server
  *  
  *  void Start()
    {
        CreateCharacters();
        SpawnAll();
    }*/

    void SpawnAll()
    {
        // 🟦 ALLIES
        for (int i = 0; i < allies.Count; i++)
        {
            spawner.SpawnCharacter(allies[i], false, i, allies.Count);
        }

        // 🟥 ENEMIES
        int normalIndex = 0;

        for (int i = 0; i < enemies.Count; i++)
        {
            if (enemies[i].data.isBoss)
            {
                spawner.SpawnCharacter(enemies[i], true, 0, enemies.Count);
            }
            else
            {
                spawner.SpawnCharacter(enemies[i], true, normalIndex, enemies.Count);
                normalIndex++;
            }
        }
    }

    void CreateCharacters()
    {
        allies.Add(new CombatCharacter(new CharacterData { nom = "Guerrer", hp_base = 120 }, false));
        allies.Add(new CombatCharacter(new CharacterData { nom = "Mage", hp_base = 80 }, false));
        allies.Add(new CombatCharacter(new CharacterData { nom = "Tank", hp_base = 200 }, false));
        allies.Add(new CombatCharacter(new CharacterData { nom = "Femboy", hp_base = 200 }, false));

        enemies.Add(new CombatCharacter(new CharacterData { nom = "Goblin", hp_base = 60 }, true));
        enemies.Add(new CombatCharacter(new CharacterData { nom = "Esquelet", hp_base = 35 }, true));
        enemies.Add(new CombatCharacter(new CharacterData { nom = "Orc", hp_base = 120 }, true));
        enemies.Add(new CombatCharacter(new CharacterData { nom = "Esquelet", hp_base = 35 }, true));
        enemies.Add(new CombatCharacter(new CharacterData { nom = "Orc", hp_base = 120 }, true));
        enemies.Add(new CombatCharacter(
    new CharacterData { nom = "Orc", hp_base = 300, isBoss = true },
    true
));
    }
}