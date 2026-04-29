    using System;

    [Serializable]
    public class SkillData
    {
        public string nom;

        public int tipus; // 1 arma, 2 habilidad, 3 objeto
        public int cooldown;

        public string descripcio;

        public float baseDamage;

        public bool isAOE;

        public string statusEffect; // "Cremat", "Enverinat", etc
    }