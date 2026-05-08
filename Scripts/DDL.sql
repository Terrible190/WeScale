-- =========================================
-- ELIMINAR TAULES 
-- =========================================

DROP TABLE IF EXISTS EFECTE_MOD_ESTADISTICA;
DROP TABLE IF EXISTS ACCIO_EFECTE;
DROP TABLE IF EXISTS EFECTE_INVOCACIO;
DROP TABLE IF EXISTS EFECTE_ESTAT;
DROP TABLE IF EXISTS ESTAT;
DROP TABLE IF EXISTS EFECTE;
DROP TABLE IF EXISTS TIPUS_EFECTE;
DROP TABLE IF EXISTS PERSONATGE_ACCIO;
DROP TABLE IF EXISTS ACCIO;
DROP TABLE IF EXISTS JUGADOR;
DROP TABLE IF EXISTS PERSONATGE;

-- =========================================
-- PERSONATGE
-- =========================================

CREATE TABLE PERSONATGE (
    id_personatge INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    seleccionable BOOLEAN NOT NULL,
    imatge VARCHAR(500) NOT NULL,
    icona VARCHAR(500) NOT NULL,

    velocitat FLOAT NOT NULL,
    hp_base FLOAT NOT NULL,
    dany_fisic_base FLOAT NOT NULL,
    dany_magic_base FLOAT NOT NULL,
    defensa_fisica_base FLOAT NOT NULL,
    defensa_magica_base FLOAT NOT NULL,
    critic_base FLOAT NOT NULL,
    critic_multiplicador_base FLOAT NOT NULL
);

-- =========================================
-- JUGADOR
-- =========================================

CREATE TABLE JUGADOR (
    id_jugador INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    id_personatge INT NOT NULL,

    FOREIGN KEY (id_personatge)
        REFERENCES PERSONATGE(id_personatge)
);

-- =========================================
-- ACCIO
-- =========================================

CREATE TABLE ACCIO (
    id_obj_actiu INT AUTO_INCREMENT PRIMARY KEY,

    nom VARCHAR(100) NOT NULL UNIQUE,

    tipus INT NOT NULL,

    cooldown INT DEFAULT 0,

    descripcio VARCHAR(1000),

    imatge VARCHAR(500),

    icona VARCHAR(500),

    usos INT DEFAULT NULL,

    estadistica INT DEFAULT NULL,

    nivell_minim INT DEFAULT NULL,

    tier INT DEFAULT NULL
);

-- =========================================
-- PERSONATGE_ACCIO
-- =========================================

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

-- =========================================
-- TIPUS_EFECTE
-- =========================================

CREATE TABLE TIPUS_EFECTE (
    id_tipus_efecte INT AUTO_INCREMENT PRIMARY KEY,

    tipus_efecte INT NOT NULL UNIQUE,

    imatge VARCHAR(500),

    icona VARCHAR(500)
);

-- =========================================
-- EFECTE
-- =========================================

CREATE TABLE EFECTE (
    id_efecte INT AUTO_INCREMENT PRIMARY KEY,

    id_tipus_efecte INT NOT NULL,

    tipus_dany INT DEFAULT NULL,

    rang INT DEFAULT NULL,

    duracio INT DEFAULT NULL,

    descripcio VARCHAR(1000),

    FOREIGN KEY (id_tipus_efecte)
        REFERENCES TIPUS_EFECTE(id_tipus_efecte)
);

-- =========================================
-- ESTAT
-- =========================================

CREATE TABLE ESTAT (
    id_estat INT AUTO_INCREMENT PRIMARY KEY,

    nom VARCHAR(100) NOT NULL UNIQUE,

    imatge VARCHAR(500),

    icona VARCHAR(500)
);

-- =========================================
-- EFECTE_ESTAT
-- =========================================

CREATE TABLE EFECTE_ESTAT (
    id_efecte_estat INT AUTO_INCREMENT PRIMARY KEY,

    id_efecte INT NOT NULL,

    id_estat INT NOT NULL,

    FOREIGN KEY (id_efecte)
        REFERENCES EFECTE(id_efecte),

    FOREIGN KEY (id_estat)
        REFERENCES ESTAT(id_estat)
);

-- =========================================
-- EFECTE_INVOCACIO
-- =========================================

CREATE TABLE EFECTE_INVOCACIO (
    id_efecte_invo INT AUTO_INCREMENT PRIMARY KEY,

    id_efecte INT NOT NULL,

    id_personatge INT NOT NULL,

    FOREIGN KEY (id_efecte)
        REFERENCES EFECTE(id_efecte),

    FOREIGN KEY (id_personatge)
        REFERENCES PERSONATGE(id_personatge)
);

-- =========================================
-- ACCIO_EFECTE (MANY TO MANY)
-- =========================================

CREATE TABLE ACCIO_EFECTE (
    id_accio_efecte INT AUTO_INCREMENT PRIMARY KEY,

    id_accio INT NOT NULL,

    id_efecte INT NOT NULL,

    FOREIGN KEY (id_accio)
        REFERENCES ACCIO(id_obj_actiu),

    FOREIGN KEY (id_efecte)
        REFERENCES EFECTE(id_efecte)
);

-- =========================================
-- EFECTE_MOD_ESTADISTICA
-- =========================================

CREATE TABLE EFECTE_MOD_ESTADISTICA (
    id_efecte_mod INT AUTO_INCREMENT PRIMARY KEY,

    id_efecte INT NOT NULL,

    nom_stat INT NOT NULL,

    operacio INT NOT NULL,

    valor FLOAT NOT NULL,

    FOREIGN KEY (id_efecte)
        REFERENCES EFECTE(id_efecte)
);