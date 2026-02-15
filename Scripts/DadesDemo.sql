-- =====================================================
-- ACCIO INTERNA DEL SISTEMA
-- Serveix com a placeholder per a EFECTES d'estat
-- =====================================================

INSERT INTO ACCIO (nom, tipus, imatge, icona, usos)
VALUES ('ESTAT_INTERNAL', 2, 'internal.png', 'internal.ico', NULL);

-- ============================================================
-- DEFINICIÓ DE L'EFECTE "CREMAT" ASSOCIAT A LA HABILITAT
-- ============================================================
--
-- Resum conceptual:
--
-- ESTAT        → Cremat
-- TIPUS_EFECTE → Estat
-- EFECTE       → Dany màgic, rang enemic, duració 3 torns
-- EFECTE_ESTAT → L'efecte aplica l'estat Cremat
-- EFECTE_MOD   → L'estat Cremat redueix 5 HP cada torn
--
-- Aquest conjunt defineix COMPLETAMENT el comportament
-- de l'estat "Cremat" dins del joc.
-- ============================================================

-- ------------------------------------------------------------
-- CATÀLEG D'ESTATS DISPONIBLES AL JOC
-- ------------------------------------------------------------
-- Taula de tipus enum
-- Conté tots els estats que poden aplicar-se als personatges
INSERT INTO ESTAT (nom) VALUES
('Cremat'),
('Enverinat'),
('Atordit'),
('Confusió'),
('Mort'),
('Zombi'),
('Semizombi'),
('T''has encantat'),
('Black Flash Mark'),
('Sobrecàrrega Obscura'),
('Congelat'),
('Espines'),
('Àngel Custodi'),
('Frenesí'),
('Vampirisme');


-- =====================================================
-- ACCIO: Habilitat màgica "Bola de Foc"
-- =====================================================
-- Tipus:
-- 1 = arma
-- 2 = habilitat
-- 3 = objecte actiu
--
-- Aquesta ACCIO serà utilitzada pels personatges
-- i està associada als EFECTES definits més endavant
-- =====================================================

INSERT INTO ACCIO
(nom, tipus, imatge, icona, usos, descripcio)
VALUES
(
  'Bola de Foc',
  2,
  'https://static.vecteezy.com/system/resources/previews/002/694/852/non_2x/fireball-falling-icon-free-vector.jpg',
  'https://icon-icons.com/download-file?file=https%3A%2F%2Fimages.icon-icons.com%2F510%2FPNG%2F512%2Ffireball_icon-icons.com_50394.png&id=50394&pack_or_individual=pack',
  NULL,
  'Llança una bola de foc que aplica Cremat durant 3 torns.'
);


-- =====================================================
-- ACCIO: Ballesta Enverinada (ARMA)
-- Arma que aplica Enverinat a múltiples enemics
-- =====================================================

INSERT INTO ACCIO
(nom, tipus, imatge, icona, usos, descripcio)
VALUES
(
  'Ballesta Enverinada',
  1,
  'https://example.com/img/ballesta_veri.png',
  'https://example.com/icon/ballesta_veri.ico',
  NULL,
  'Dispara un projectil físic i enverinat que afecta tots els enemics durant 3 torns.'
);



-- =====================================================
-- ACCIO: Bastó Glacial (ARMA)
-- Arma que aplica Congelament a un enemic
-- =====================================================
INSERT INTO ACCIO
(nom, tipus, imatge, icona, usos, descripcio)
VALUES
(
  'Bastó Glacial',
  1,
  'https://example.com/img/basto_glacial.png',
  'https://example.com/icon/basto_glacial.ico',
  NULL,
  'Ataca amb energia glacial aplicant Congelat durant 3 torns i reduint la velocitat un 30%.'
);

-- ------------------------------------------------------------
-- TIPUS_EFECTE
-- ------------------------------------------------------------
-- Taula enum "dur" que defineix el tipus d'un EFECTE
-- 1 = Invocació
-- 2 = Estat
-- 3 = Modificació d'estadística
INSERT INTO TIPUS_EFECTE (tipus_efecte, imatge, icona)
VALUES
(1, 'https://example.com/img/invocacio.png', 'https://example.com/icon/invocacio.ico'),
(2, 'https://example.com/img/estat.png',     'https://example.com/icon/estat.ico'),
(3, 'https://example.com/img/mod.png',       'https://example.com/icon/mod.ico');

-- ------------------------------------------------------------
-- EFECTE
-- ------------------------------------------------------------
-- Defineix l'efecte general associat a la habilitat "Bola de Foc"
-- En aquest cas:
-- - Tipus d'efecte: Estat
-- - Dany: Màgic
-- - Rang: Enemic
-- - Duració: 3 torns

INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2),
  2,   -- dany màgic
  2,   -- enemic
  3,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Bola de Foc'),
  'Aplica l estat Cremat durant 3 torns.'
);

SET @id_efecte_bola := LAST_INSERT_ID();



-- =====================================================
-- EFECTE: Bastó Glacial (magic)
-- =====================================================
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2),
  2,   -- dany màgic
  2,   -- enemic
  3,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Bastó Glacial'),
  'Aplica l estat Congelat durant 3 torns.'
);

SET @id_efecte_glacial := LAST_INSERT_ID();



-- =====================================================
-- EFECTE: Ballesta Enverinada (físic)
-- =====================================================

INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2),
  1,   -- dany físic
  4,   -- tots els enemics
  3,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Ballesta Enverinada'),
  'Aplica l estat Enverinat a tots els enemics durant 3 torns.'
);

SET @id_efecte_ballesta := LAST_INSERT_ID();



-- ------------------------------------------------------------
-- EFECTE_ESTAT
-- ------------------------------------------------------------
-- Vincula l'EFECTE de "Bola de Foc" amb l'estat "Cremat"
INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  @id_efecte_bola,
  (SELECT id_estat FROM ESTAT WHERE nom = 'Cremat')
);


-- =====================================================
-- EFECTE_ESTAT: l'arma aplica l'estat Glacial
-- =====================================================
INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  @id_efecte_glacial,
  (SELECT id_estat FROM ESTAT WHERE nom = 'Congelat')
);




-- =====================================================
-- EFECTE_ESTAT: l'arma aplica l'estat Enverinat
-- =====================================================

INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  (
	SELECT e.id_efecte
	FROM EFECTE e
	JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
	WHERE a.nom = 'Ballesta Enverinada'
  ),
  (SELECT id_estat FROM ESTAT WHERE nom = 'Enverinat')
);

-- ------------------------------------------------------------
-- EFECTE_MOD_ESTADISTICA Arma - Basto Glacial
-- ------------------------------------------------------------
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_glacial,
  6,   -- Velocitat
  4,   -- Percentatge
  -30
);


-- ------------------------------------------------------------
-- EFECTE_MOD_ESTADISTICA
-- ------------------------------------------------------------
-- Defineix el dany per torn de l'estat "Cremat"
-- - HP
-- - Valor fix (-5)
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_bola,
  1,   -- HP
  1,   -- ADD
  -5
);

-- =====================================================
-- EFECTE_MOD_ESTADISTICA
-- Enverinat: -4 HP per torn (NO percentatge)
-- =====================================================

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_ballesta,
  1,   -- HP
  1,   -- ADD
  -4
);

-- =====================================================
-- ACCIO: Habilitat base "Defensa"
-- =====================================================
-- Tipus:
-- 1 = arma
-- 2 = habilitat
-- 3 = objecte actiu
--
-- Defensa és una habilitat genèrica
-- =====================================================

INSERT INTO ACCIO
(nom, tipus, imatge, icona, usos, descripcio)
VALUES
(
  'Defensa',
  2,
  'https://cdn-icons-png.flaticon.com/512/8037/8037114.png',
  'https://cdn-icons-png.freepik.com/512/8294/8294515.ico',
  NULL,
  'Augmenta la defensa física i màgica un 70% durant 1 torn.'
);



-- EFECTE: Habilitat Defensa (buff temporal)
INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 3),
  1,   -- self
  1,   -- 1 torn
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Defensa'),
  'Augmenta la defensa física i màgica.'
);

SET @id_efecte_defensa := LAST_INSERT_ID();



-- Defensa física +70%
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_defensa,
  3,   -- Defensa Física
  4,   -- Percentatge
  70
);


-- Defensa màgica +70%
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_defensa,
  4,   -- Defensa Màgica
  4,   -- Percentatge
  70
);

-- =====================================================
-- PERSONATGE: Guerrer (jugable)
-- =====================================================

INSERT INTO PERSONATGE
(
  nom,
  seleccionable,
  imatge,
  icona,
  hp_base,
  dany_fisic_base,
  dany_magic_base,
  defensa_fisica_base,
  defensa_magica_base,
  critic_base,
  critic_multiplicador_base,
  velocitat
)
VALUES
(
  'Guerrer',
  TRUE,
  'https://example.com/img/personatge_guerrer.png',
  'https://example.com/icon/personatge_guerrer.ico',
  120, -- HP
  18,  -- dany físic
  4,   -- dany màgic
  12,  -- defensa física
  8,   -- defensa màgica
  0.1, -- crític base
  1.5, -- multiplicador crític
  3.5  -- velocitat
);

-- =====================================================
-- PERSONATGE: Goblin (enemic)
-- =====================================================
-- No seleccionable pel jugador
-- =====================================================

INSERT INTO PERSONATGE
(
  nom,
  seleccionable,
  imatge,
  icona,
  hp_base,
  dany_fisic_base,
  dany_magic_base,
  defensa_fisica_base,
  defensa_magica_base,
  critic_base,
  critic_multiplicador_base,
  velocitat
)
VALUES
(
  'Goblin',
  FALSE,
  'https://example.com/img/personatge_goblin.png',
  'https://example.com/icon/personatge_goblin.ico',
  60,  -- HP
  10,  -- dany físic
  0,   -- dany màgic
  5,   -- defensa física
  2,   -- defensa màgica
  0.05,-- crític base
  1.3,  -- multiplicador crític
  3.5  -- velocitat
);

-- =====================================================
-- PERSONATGE: Esquelet (invocació)
-- =====================================================

INSERT INTO PERSONATGE
(
  nom,
  seleccionable,
  imatge,
  icona,
  hp_base,
  dany_fisic_base,
  dany_magic_base,
  defensa_fisica_base,
  defensa_magica_base,
  critic_base,
  critic_multiplicador_base,
  velocitat
)
VALUES
(
  'Esquelet',
  FALSE,
  'https://example.com/img/personatge_esquelet.png',
  'https://example.com/icon/personatge_esquelet.ico',
  35,   -- HP
  8,    -- dany físic
  0,    -- dany màgic
  3,    -- defensa física
  1,    -- defensa màgica
  0.05, -- crític
  1.2, 
  3.5  -- velocitat
);



-- =====================================================
-- Defensa: Guerrer
-- =====================================================

INSERT INTO PERSONATGE_ACCIO
(id_personatge, id_objhabarm_actiu, equipada)
VALUES
(
  (SELECT id_personatge FROM PERSONATGE WHERE nom = 'Guerrer'),
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Defensa'),
  TRUE
);

-- =====================================================
-- Defensa: Goblin
-- =====================================================

INSERT INTO PERSONATGE_ACCIO
(id_personatge, id_objhabarm_actiu, equipada)
VALUES
(
  (SELECT id_personatge FROM PERSONATGE WHERE nom = 'Goblin'),
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Defensa'),
  TRUE
);
-- =====================================================
-- Arma Ballesta Enverinada: Goblin
-- =====================================================
INSERT INTO PERSONATGE_ACCIO
(id_personatge, id_objhabarm_actiu, equipada)
VALUES
(
  (SELECT id_personatge FROM PERSONATGE WHERE nom = 'Goblin'),
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Ballesta Enverinada' LIMIT 1),   -- id_obj_actiu de la Ballesta Enverinada
  TRUE   -- equipada
);


-- =====================================================
-- ZOMBI AND SEMIZOMBI
-- =====================================================

-- =====================================================
-- ZOMBI (-70%)
-- =====================================================


-- ---------- EFECTE ZOMBI (ESTAT) ----------
INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  2,   -- Estat
  1,   -- self
  NULL, -- permanent mentre estat actiu
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL'),
  'Aplica l estat Zombi.'
);

SET @id_efecte_zombi_estat := LAST_INSERT_ID();


INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  @id_efecte_zombi_estat,
  (SELECT id_estat FROM ESTAT WHERE nom = 'Zombi')
);



-- ---------- EFECTE ZOMBI (DEBUFF -70%) ----------
INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  3,   -- Modificació estadística
  1,
  NULL,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL'),
  'Redueix totes les estadístiques un 70%.'
);

SET @id_efecte_zombi_mod := LAST_INSERT_ID();


INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(@id_efecte_zombi_mod, 2, 4, -70),  -- Dany Físic
(@id_efecte_zombi_mod, 3, 4, -70),  -- Defensa Física
(@id_efecte_zombi_mod, 4, 4, -70),  -- Defensa Màgica
(@id_efecte_zombi_mod, 5, 4, -70);  -- Dany Màgic



-- =====================================================
-- SEMIZOMBI (-40%)
-- =====================================================

-- ---------- EFECTE SEMIZOMBI (ESTAT) ----------
INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  2,   -- Estat
  1,   -- self
  NULL,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL'),
  'Aplica l estat Semizombi.'
);

SET @id_efecte_semizombi_estat := LAST_INSERT_ID();


INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  @id_efecte_semizombi_estat,
  (SELECT id_estat FROM ESTAT WHERE nom = 'Semizombi')
);


-- ---------- EFECTE SEMIZOMBI (DEBUFF -40%) ----------
INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  3,   -- Modificació estadística
  1,
  NULL,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL'),
  'Redueix totes les estadístiques un 40%.'
);

SET @id_efecte_semizombi_mod := LAST_INSERT_ID();

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(@id_efecte_semizombi_mod, 2, 4, -40),  -- Dany Físic
(@id_efecte_semizombi_mod, 3, 4, -40),  -- Defensa Física
(@id_efecte_semizombi_mod, 4, 4, -40),  -- Defensa Màgica
(@id_efecte_semizombi_mod, 5, 4, -40);  -- Dany Màgic
 
-- =====================================================
-- ACCIO Angel Custodi - Objecte
-- =====================================================
INSERT INTO ACCIO
(nom, tipus, imatge, icona, usos, descripcio)
VALUES
(
  'Àngel Custodi',
  3,
  'https://example.com/img/angel_custodi.png',
  'https://example.com/icon/angel_custodi.ico',
  1,
  'Evita la mort i restaura el 50% de la vida màxima.'
);

SET @id_accio_angel := LAST_INSERT_ID();

INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  2,   -- Estat
  1,   -- self
  NULL,
  @id_accio_angel,
  'Protegeix de la mort i restaura vida.'
);

SET @id_efecte_angel := LAST_INSERT_ID();

INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  @id_efecte_angel,
  (SELECT id_estat FROM ESTAT WHERE nom = 'Àngel Custodi')
);

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_angel,
  1,  
  9, 
  1
);

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_angel,
  1,  
  11, 
  50
);


-- =====================================================
-- ACCIO Resurrecio - Habilitat
-- =====================================================
INSERT INTO ACCIO
(nom, tipus, imatge, icona, usos, descripcio)
VALUES
(
  'Resurrecció',
  2,
  'https://example.com/img/resurreccio.png',
  'https://example.com/icon/resurreccio.ico',
  NULL,
  'Revifa un aliat caigut retornant-lo al combat.'
);

INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2 LIMIT 1), -- Estat
  NULL,
  3,      -- seleccionat (aliat mort)
  NULL,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Resurrecció' LIMIT 1)
);


-- =====================================================
-- ACCIO: Habilitat d'invocació "Invocar Esquelet"
-- =====================================================
INSERT INTO ACCIO
(nom, tipus, imatge, icona, usos, descripcio)
VALUES
(
  'Invocar Esquelet',
  2,
  'https://example.com/img/invocar_esquelet.png',
  'https://example.com/icon/invocar_esquelet.ico',
  1,
  'Invoca un esquelet aliat per ajudar en combat.'
);


-- =====================================================
-- EFECTE: Invocació d'Esquelet
-- =====================================================

INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  1,   -- Invocació
  1,   -- self
  NULL,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Invocar Esquelet'),
  'Invoca un esquelet aliat.'
);

SET @id_efecte_invocar := LAST_INSERT_ID();


-- =====================================================
-- EFECTE_INVOCACIO: invoca un Esquelet
-- =====================================================
INSERT INTO EFECTE_INVOCACIO
(id_efecte, id_personatge)
VALUES
(
  @id_efecte_invocar,
  (SELECT id_personatge FROM PERSONATGE WHERE nom = 'Esquelet')
);


-- =====================================================
-- ACCIO: Black Flash
-- =====================================================
INSERT INTO ACCIO
(nom, tipus, usos, descripcio)
VALUES
(
  'Black Flash',
  2,
  NULL,
  'Marca l enemic amb Black Flash i infligeix dany físic extra.'
);
INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  2,   -- Estat
  3,   -- seleccionat
  1,   -- 1 torn
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Black Flash'),
  'Aplica l estat Black Flash Mark.'
);

SET @id_efecte_blackflash_estat := LAST_INSERT_ID();
INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  @id_efecte_blackflash_estat,
  (SELECT id_estat FROM ESTAT WHERE nom = 'Black Flash Mark')
);

INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  3,   -- Mod estadística
  1,   -- físic
  3,   -- seleccionat
  0,   -- immediat
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Black Flash'),
  'Infligeix dany físic addicional.'
);

SET @id_efecte_blackflash_damage := LAST_INSERT_ID();


INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_blackflash_damage,
  2,   -- Dany Físic
  1,   -- ADD
  10
);

INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  3,   -- Mod estadística
  1,   -- físic
  3,   -- seleccionat
  0,   -- immediat
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Black Flash'),
  'Si l enemic té Black Flash Mark, el dany es duplica.'
);

SET @id_efecte_blackflash_bonus := LAST_INSERT_ID();

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_blackflash_bonus,
  2,   -- Dany Físic
  8,   -- SCALE_PERCENT (multiplica)
  200  -- 200% = doble dany
);


-- =====================================================
-- ACCIO: Sobrecàrrega Obscura
-- =====================================================
INSERT INTO ACCIO
(nom, tipus, usos, descripcio)
VALUES
(
  'Sobrecàrrega Obscura',
  2,
  NULL,
  'Augmenta el dany físic i màgic un 40% durant 1 torn.'
);

SET @id_accio_sobrecarga := LAST_INSERT_ID();

INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  2,   -- Estat
  1,   -- self
  1,   -- 1 torn
  @id_accio_sobrecarga,
  'Aplica l estat Sobrecàrrega Obscura.'
);

SET @id_efecte_sobrecarga_estat := LAST_INSERT_ID();

INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  @id_efecte_sobrecarga_estat,
  (SELECT id_estat FROM ESTAT WHERE nom = 'Sobrecàrrega Obscura')
);

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_sobrecarga_estat,
  2,   -- Dany Físic
  4,   -- Percentatge
  40
);
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_sobrecarga_estat,
  5,   -- Dany Màgic
  4,   -- Percentatge
  40
);

-- =====================================================
-- ACCIO: Espines
-- =====================================================

INSERT INTO ACCIO (nom, tipus, descripcio)
VALUES (
  'Espines',
  2,
  'Retorna un 50% del dany rebut.'
);

INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES (
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 3),
  1,
  2,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Espines'),
  'Retorna dany rebut.'
);
SET @id_efecte_espines := LAST_INSERT_ID();

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES (
  @id_efecte_espines,
  1,
  7,
  -50
);


-- =====================================================
-- ACCIO: ARMADURA D’ESPINES
-- =====================================================
INSERT INTO ACCIO
(nom, tipus, usos, descripcio, estadistica, nivell_minim)
VALUES
(
  'Armadura d''Espines',
  1,
  -1,
  'Retorna un 30% del dany rebut si la Defensa Total és almenys 50.',
  7,
  50
);

SET @id_accio := LAST_INSERT_ID();

INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(2, NULL, 1, NULL, @id_accio,
 'Aplica l''estat Espines permanentment.');

SET @id_efecte := LAST_INSERT_ID();

INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  @id_efecte,
  (SELECT id_estat FROM ESTAT WHERE nom = 'Espines')
);

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte,
  1,
  7,
  -30
);


-- =====================================================
-- ACCIO: ESCUT ARCÀ (ARMA / EQUIPAMENT)
-- =====================================================

INSERT INTO ACCIO
(nom, tipus, usos, descripcio)
VALUES
(
  'Escut Arcà',
  1,      -- arma / equipament
  -1,     -- permanent
  'Augmenta la defensa màgica un 50% mentre estigui equipat.'
);

INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 3), -- Mod estadística
  1,      -- self
  NULL,   -- permanent
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Escut Arcà'),
  'Augmenta la defensa màgica.'
);

SET @id_efecte_escut := LAST_INSERT_ID();

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_escut,
  4,   -- Defensa Màgica
  4,   -- PERCENT_CURRENT
  50
);

-- =====================================================
-- ACCIO: FRENESÍ (HABILITAT + ESTAT)
-- =====================================================

INSERT INTO ACCIO
(nom, tipus, cooldown, descripcio)
VALUES
(
  'Frenesí',
  2,
  2,
  'Augmenta el dany físic però redueix la defensa durant 2 torns.'
);

SET @id_accio_frenesi := LAST_INSERT_ID();


INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2),
  1,   -- self
  2,   -- 2 torns
  @id_accio_frenesi,
  'Aplica l estat Frenesí.'
);

SET @id_efecte_frenesi := LAST_INSERT_ID();

INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  @id_efecte_frenesi,
  (SELECT id_estat FROM ESTAT WHERE nom = 'Frenesí')
);

-- +50% dany físic
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_frenesi,
  2,   -- Dany Físic
  4,   -- Percentatge
  50
);

-- -20% defensa física
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_frenesi,
  3,   -- Defensa Física
  4,   -- Percentatge
  -20
);

-- =====================================================
--  EFECTE: VAMPIRISME 
-- 	Escala amb crític 
-- =====================================================
INSERT INTO EFECTE
(id_tipus_efecte, rang, duracio, id_obj_arm_hab_actiu, descripcio)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2),
  1,        -- self
  NULL,     -- permanent mentre estat actiu
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL'),
  'Recupera un 25% del dany causat. Si és crític, la curació escala amb el multiplicador crític.'
);

SET @id_efecte_vampirisme_pro := LAST_INSERT_ID();

-- Vinculem amb l'estat Vampirisme
INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  @id_efecte_vampirisme_pro,
  (SELECT id_estat FROM ESTAT WHERE nom = 'Vampirisme')
);

-- Curació basada en dany causat
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  @id_efecte_vampirisme_pro,
  1,   -- HP
  6,   -- PERCENT_DAMAGE_DONE
  25   -- 25% del dany causat
);