using System;

[Serializable]
public class NodeDTO
{
    public int pis;
    public string tipus;

    public EnemyDTO[] enemics;
    public EnemyDTO boss;

    public RewardDTO recompensa;
}