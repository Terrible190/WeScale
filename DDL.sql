CREATE TABLE PERSONATGE (
    id_personatge INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    seleccionable BOOLEAN NOT NULL,
    imatge VARCHAR(500) NOT NULL,
    icona VARCHAR(500) NOT NULL,

    hp_base FLOAT NOT NULL CHECK (hp_base >= 0),
    dany_fisic_base FLOAT NOT NULL CHECK (dany_fisic_ base >= 0),
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
    nom VARCHAR(100) NOT NULL,
    tipus INT NOT NULL, -- 1 = arma | 2 = habilitat | 3 = objecte_actiu
    imatge VARCHAR(500),
    icona VARCHAR(500),
    usos INT CHECK (usos IS NULL OR usos >= 0),

    CHECK (tipus IN (1, 2, 3))
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
    tipus_efecte INT NOT NULL, -- 1 = Invocacio | 2 = Estat | 3 = Mod Estadistica
    imatge VARCHAR(500),
    icona VARCHAR(500),

    CHECK (tipus_efecte IN (1, 2, 3))
);

CREATE TABLE EFECTE (
    id_efecte INT AUTO_INCREMENT PRIMARY KEY,
    id_tipus_efecte INT NOT NULL,
    tipus_dany INT, -- 1 = fisic | 2 = magic
    rang INT,       -- 1 = jo | 2 = enemic | 3 = seleccionat | 4 = all_enemies | 5 = all_allies
    duracio INT CHECK (duracio IS NULL OR duracio >= 0),
    id_obj_arm_hab_actiu INT NOT NULL,
	imatge varchar(500) NOT NULL,
	icona varchar(500) NOT NULL,

    FOREIGN KEY (id_tipus_efecte)
        REFERENCES TIPUS_EFECTE(id_tipus_efecte),

    FOREIGN KEY (id_obj_arm_hab_actiu)
        REFERENCES ACCIO(id_obj_actiu),

    CHECK (tipus_dany IS NULL OR tipus_dany IN (1, 2)),
    CHECK (rang IS NULL OR rang IN (1, 2, 3, 4, 5))
);

CREATE TABLE ESTAT (
    id_estat INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
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
    id_invocacio INT NOT NULL,
    id_personatge INT NOT NULL,

    FOREIGN KEY (id_efecte)
        REFERENCES EFECTE(id_efecte),

    FOREIGN KEY (id_personatge)
        REFERENCES PERSONATGE(id_personatge)
);

CREATE TABLE EFECTE_MOD_ESTADISTICA (
    id_efecte_mod INT AUTO_INCREMENT PRIMARY KEY,
    id_efecte INT NOT NULL,
    nom_stat INT NOT NULL,   -- identificador de l’estadística
    operacio INT NOT NULL,  -- 1 = add | 2 = multiply | 3 = set
    valor FLOAT NOT NULL,

    FOREIGN KEY (id_efecte)
        REFERENCES EFECTE(id_efecte),

    CHECK (operacio IN (1, 2, 3))
);
