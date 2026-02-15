-- =========================================
-- ELIMINAR TAULES 
-- =========================================

DROP TABLE EFECTE_MOD_ESTADISTICA;

DROP TABLE EFECTE_INVOCACIO;

DROP TABLE EFECTE_ESTAT;

DROP TABLE ESTAT;

DROP TABLE EFECTE;

DROP TABLE TIPUS_EFECTE;

DROP TABLE PERSONATGE_ACCIO;

DROP TABLE ACCIO;

DROP TABLE JUGADOR;

DROP TABLE PERSONATGE;

CREATE TABLE PERSONATGE (
    id_personatge INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    seleccionable BOOLEAN NOT NULL,
    imatge VARCHAR(500) NOT NULL,
    icona VARCHAR(500) NOT NULL,

    velocitat FLOAT NOT NULL CHECK (velocitat >= 0),
    hp_base FLOAT NOT NULL CHECK (hp_base >= 0),
    dany_fisic_base FLOAT NOT NULL CHECK (dany_fisic_base >= 0),
    dany_magic_base FLOAT NOT NULL CHECK (dany_magic_base >= 0),
    defensa_fisica_base FLOAT NOT NULL CHECK (defensa_fisica_base >= 0),
    defensa_magica_base FLOAT NOT NULL CHECK (defensa_magica_base >= 0),
    critic_base FLOAT NOT NULL CHECK (critic_base >= 0),
    critic_multiplicador_base FLOAT NOT NULL CHECK (critic_multiplicador_base >= 1)
);

CREATE TABLE JUGADOR (
    id_jugador INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    id_personatge INT NOT NULL,

    FOREIGN KEY (id_personatge)
        REFERENCES PERSONATGE(id_personatge)
);

CREATE TABLE ACCIO (
    id_obj_actiu INT AUTO_INCREMENT PRIMARY KEY,

    nom VARCHAR(100) NOT NULL UNIQUE,

    tipus INT NOT NULL,
    -- 1 = Arma 
    -- 2 = Habilitat 
    -- 3 = Objecte

    cooldown INT DEFAULT 0 CHECK (cooldown >= 0),
    -- Nombre de torns que han de passar abans de poder reutilitzar l'acció.
    -- 0 = sense temps de reutilització

    descripcio VARCHAR(1000),

    imatge VARCHAR(500),
	
    icona VARCHAR(500),

    usos INT DEFAULT NULL CHECK (usos IS NULL OR usos >= -1),

    estadistica INT DEFAULT NULL,
    -- 1 = HP
    -- 2 = Dany Físic
    -- 3 = Defensa Física
    -- 4 = Defensa Màgica
    -- 5 = Dany Màgic
    -- 6 = Velocitat
    -- 7 = Defensa Total (Defensa Física + Defensa Màgica)
    -- 8 = Crític
    -- 9 = Multiplicador Crític
    -- NULL = sense requisit

    nivell_minim INT DEFAULT NULL CHECK (nivell_minim IS NULL OR nivell_minim >= 0),

    tier INT DEFAULT NULL CHECK (tier IS NULL OR tier >= 0),

    CHECK (tipus IN (1,2,3))
);


CREATE TABLE PERSONATGE_ACCIO (
    id_personatge_accio INT AUTO_INCREMENT PRIMARY KEY,
    id_personatge INT NOT NULL,
    id_objhabarm_actiu INT NOT NULL,
    equipada BOOLEAN NOT NULL,

    FOREIGN KEY (id_personatge)
        REFERENCES PERSONATGE(id_personatge),

    FOREIGN KEY (id_objhabarm_actiu)
        REFERENCES ACCIO(id_obj_actiu)
);

CREATE TABLE TIPUS_EFECTE (
    id_tipus_efecte INT AUTO_INCREMENT PRIMARY KEY,
    tipus_efecte INT NOT NULL UNIQUE, -- 1 = Invocacio | 2 = Estat | 3 = Mod Estadistica 
    imatge VARCHAR(500),
    icona VARCHAR(500),

    CHECK (tipus_efecte IN (1, 2, 3))
);

CREATE TABLE EFECTE (
    id_efecte INT AUTO_INCREMENT PRIMARY KEY,
    id_tipus_efecte INT NOT NULL,

    tipus_dany INT DEFAULT NULL, -- 1=fisic 2=magic
    rang INT DEFAULT NULL,       -- 1=self 2=enemy 3=seleccionat 4=all_enemies 5=all_allies

    duracio INT DEFAULT NULL CHECK (duracio IS NULL OR duracio >= 0),

    id_obj_arm_hab_actiu INT NOT NULL,

    descripcio VARCHAR(1000),

    FOREIGN KEY (id_tipus_efecte)
        REFERENCES TIPUS_EFECTE(id_tipus_efecte),

    FOREIGN KEY (id_obj_arm_hab_actiu)
        REFERENCES ACCIO(id_obj_actiu),

    CHECK (tipus_dany IS NULL OR tipus_dany IN (1, 2)),
    CHECK (rang IS NULL OR rang IN (1, 2, 3, 4, 5))
);

CREATE TABLE ESTAT (
    id_estat INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    imatge VARCHAR(500),
    icona VARCHAR(500)
);


CREATE TABLE EFECTE_ESTAT (
    id_efecte_estat INT AUTO_INCREMENT PRIMARY KEY,
    id_efecte INT NOT NULL,
    id_estat INT NOT NULL,

    FOREIGN KEY (id_efecte)
        REFERENCES EFECTE(id_efecte),

    FOREIGN KEY (id_estat)
        REFERENCES ESTAT(id_estat)
);

CREATE TABLE EFECTE_INVOCACIO (
    id_efecte_invo INT AUTO_INCREMENT PRIMARY KEY,
    id_efecte INT NOT NULL,
    id_personatge INT NOT NULL,

    FOREIGN KEY (id_efecte)
        REFERENCES EFECTE(id_efecte),

    FOREIGN KEY (id_personatge)
        REFERENCES PERSONATGE(id_personatge)
);

CREATE TABLE EFECTE_MOD_ESTADISTICA (
    id_efecte_mod INT AUTO_INCREMENT PRIMARY KEY,

    id_efecte INT NOT NULL,

    nom_stat INT NOT NULL,
    -- 1=HP
    -- 2=Dany Físic
    -- 3=Defensa Física
    -- 4=Defensa Màgica
    -- 5=Dany Màgic
    -- 6=Velocitat
 

    operacio INT NOT NULL,
    -- 1=ADD                 → Sumar valor directe
    -- 2=MULTIPLY            → Multiplicar valor
    -- 3=SET                 → Fixar valor exacte
    -- 4=PERCENT_CURRENT     → Percentatge sobre valor actual
    -- 5=PERCENT_BASE        → Percentatge sobre valor base
    -- 6=PERCENT_DAMAGE_DONE → Percentatge sobre dany causat
    -- 7=PERCENT_DAMAGE_TAKEN→ Percentatge sobre dany rebut
    -- 8=DIVIDE              → Dividir valor
    -- 9=CLAMP_MIN           → Forçar valor mínim
    -- 10=CLAMP_MAX          → Forçar valor màxim
	-- 11=PERCENT_MAX_HP	 → Percentatge sobre vida màxima
    valor FLOAT NOT NULL,
    -- Valor utilitzat en l'operació (pot ser positiu o negatiu segons el cas)

    FOREIGN KEY (id_efecte)
        REFERENCES EFECTE(id_efecte),

    CHECK (operacio IN (1,2,3,4,5,6,7,8,9,10,11))
);
