using UnityEngine;
using System.Collections.Generic;

public class SpawnManager : MonoBehaviour
{

    [Header("Scale Settings")]
    public float characterScale = 5f;

    [Header("Default Prefab (override total)")]
    public bool useDefaultPrefab = true;
    public GameObject defaultPrefab; // ← Vampire A Lusth

    [Header("Prefabs (fallback si no uses default)")]
    public GameObject warriorPrefab;
    public GameObject magePrefab;
    public GameObject tankPrefab;

    public GameObject goblinPrefab;
    public GameObject skeletonPrefab;
    public GameObject orcPrefab;

    [Header("Positions")]
    public Transform[] allyPositions;
    public Transform[] enemyPositions;
    
    [Header("Boss Position")]
    public Transform enemyBossPosition;
    private Dictionary<string, GameObject> prefabMap;


    void Awake()
    {
        prefabMap = new Dictionary<string, GameObject>();

        // 🟦 Allies
        prefabMap["Guerrer"] = warriorPrefab;
        prefabMap["Mage"] = magePrefab;
        prefabMap["Tank"] = tankPrefab;
        prefabMap["Femboy"] = magePrefab;

        // 🟥 Enemies
        prefabMap["Goblin"] = goblinPrefab;
        prefabMap["Esquelet"] = skeletonPrefab;
        prefabMap["Orc"] = orcPrefab;
        prefabMap["Esquelet"] = skeletonPrefab;
        prefabMap["Orc"] = orcPrefab;
    }
    int GetSlotIndex(int index, int totalCharacters, int totalSlots)
    {
        int[] order = { 2, 1, 3, 0, 4 };

        // 🔥 evitar crash
        if (index >= order.Length)
            index = order.Length - 1;

        int slot = order[index];

        // 🔥 evitar salirte de slots reales
        if (slot >= totalSlots)
            slot = totalSlots - 1;

        return slot;
    }
    public GameObject SpawnCharacter(CombatCharacter character, bool isEnemy, int index, int totalCharacters)
    {
        Transform[] positions = isEnemy ? enemyPositions : allyPositions;

        if (positions == null || positions.Length == 0)
        {
            Debug.LogError("No positions assigned!");
            return null;
        }

        int totalSlots = positions.Length;

        Transform spawnPoint;

        // 🔥 PRIORIDAD: BOSS
        if (isEnemy && character.data.isBoss && enemyBossPosition != null)
        {
            spawnPoint = enemyBossPosition;
        }
        else
        {
            int slotIndex = GetSlotIndex(index, totalCharacters, totalSlots);
            spawnPoint = positions[slotIndex];
        }

        GameObject prefab;

        if (useDefaultPrefab)
        {
            prefab = defaultPrefab;

            if (prefab == null)
            {
                Debug.LogError("Default prefab is NULL");
                return null;
            }
        }
        else
        {
            if (!prefabMap.TryGetValue(character.data.nom, out prefab))
            {
                Debug.LogError("No prefab for: " + character.data.nom);
                return null;
            }

            if (prefab == null)
            {
                Debug.LogError("Prefab NULL: " + character.data.nom);
                return null;
            }
        }

        GameObject obj = Instantiate(prefab, spawnPoint.position, Quaternion.identity);

        obj.transform.localScale = Vector3.one * characterScale * 2f;

        float angle = 70f;

        if (isEnemy)
            obj.transform.rotation = Quaternion.Euler(0, -angle, 0);
        else
            obj.transform.rotation = Quaternion.Euler(0, angle, 0);

        // 🔥 ESCALA EXTRA PARA BOSS
        if (character.data.isBoss)
        {
            obj.transform.localScale *= 1.5f;
        }

        if (!obj.TryGetComponent(out CharacterView view))
        {
            Debug.LogError("Prefab sin CharacterView");
            return obj;
        }

        view.Init(character);

        return obj;
    }
}