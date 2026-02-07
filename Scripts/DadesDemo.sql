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
-- 1️⃣ CATÀLEG D'ESTATS DISPONIBLES AL JOC
-- ------------------------------------------------------------
-- Taula de tipus enum "soft"
-- Conté tots els estats que poden aplicar-se als personatges
INSERT INTO ESTAT (nom) VALUES
('Cremat'),
('Enverinat'),
('Atordit'),
('Confusió'),
('Mort'),
('Zombi'),
('Semizombi');


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
(nom, tipus, imatge, icona, usos)
VALUES
(
  'Bola de Foc',
  2, -- 2 = habilitat
  'https://static.vecteezy.com/system/resources/previews/002/694/852/non_2x/fireball-falling-icon-free-vector.jpg',
  'https://icon-icons.com/download-file?file=https%3A%2F%2Fimages.icon-icons.com%2F510%2FPNG%2F512%2Ffireball_icon-icons.com_50394.png&id=50394&pack_or_individual=pack',
  NULL -- NULL = usos il·limitats
);


-- =====================================================
-- ACCIO: Ballesta Enverinada (ARMA)
-- Arma que aplica Enverinat a múltiples enemics
-- =====================================================

INSERT INTO ACCIO
(nom, tipus, imatge, icona, usos)
VALUES
(
  'Ballesta Enverinada',
  1, -- 1 = arma
  'https://example.com/img/ballesta_veri.png',
  'https://example.com/icon/ballesta_veri.ico',
  NULL
);


-- ------------------------------------------------------------
-- 2️⃣ TIPUS_EFECTE
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
-- 3️⃣ EFECTE
-- ------------------------------------------------------------
-- Defineix l'efecte general associat a la habilitat "Bola de Foc"
-- En aquest cas:
-- - Tipus d'efecte: Estat
-- - Dany: Màgic
-- - Rang: Enemic
-- - Duració: 3 torns
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2 LIMIT 1),
  2,
  2,
  3,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Bola de Foc' LIMIT 1),
  'https://example.com/img/efecte_cremat.png',
  'https://example.com/icon/efecte_cremat.ico'
);

-- =====================================================
-- EFECTE: Estat Enverinat (origen físic)
-- =====================================================

INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2), -- Estat
  1,  -- 1 = dany físic
  4,  -- 4 = tots els enemics
  3,  -- 3 rondes
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Ballesta Enverinada' LIMIT 1),
  'https://example.com/img/estat_enverinat.png',
  'https://example.com/icon/estat_enverinat.ico'
);


-- ------------------------------------------------------------
-- 4️⃣ EFECTE_ESTAT
-- ------------------------------------------------------------
-- Vincula l'EFECTE de "Bola de Foc" amb l'estat "Cremat"
INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  (SELECT e.id_efecte
   FROM EFECTE e
   JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
   WHERE a.nom = 'Bola de Foc'
     AND e.id_tipus_efecte = (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2 LIMIT 1)
   LIMIT 1),
  (SELECT id_estat FROM ESTAT WHERE nom = 'Cremat' LIMIT 1)
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
    LIMIT 1
  ),
  (SELECT id_estat FROM ESTAT WHERE nom = 'Enverinat')
);


-- ------------------------------------------------------------
-- 5️⃣ EFECTE_MOD_ESTADISTICA
-- ------------------------------------------------------------
-- Defineix el dany per torn de l'estat "Cremat"
-- - HP
-- - Valor fix (-5)
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  (SELECT e.id_efecte
   FROM EFECTE e
   JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
   WHERE a.nom = 'Bola de Foc'),

  1,   -- HP
  1,   -- suma
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
  (
    SELECT e.id_efecte
    FROM EFECTE e
    JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
    WHERE a.nom = 'Ballesta Enverinada'
    LIMIT 1
  ),
  1,  -- HP
  1,  -- suma
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
(nom, tipus, imatge, icona, usos)
VALUES
(
  'Defensa',
  2, -- habilitat
  'https://cdn-icons-png.flaticon.com/512/8037/8037114.png',
  'https://cdn-icons-png.freepik.com/512/8294/8294515.ico',
  NULL
);


-- EFECTE: Habilitat Defensa (buff temporal)
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 3), -- Mod estadística
  NULL,
  1,   -- self
  1,   -- 1 torn
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Defensa'),
  'https://example.com/img/efecte_defensa.png',
  'https://example.com/icon/efecte_defensa.ico'
);


-- Defensa física +70%
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  (SELECT e.id_efecte
   FROM EFECTE e
   JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
   WHERE a.nom = 'Defensa'),

  3,   -- defensa física
  4,   -- percentatge
  70
);


-- Defensa màgica +70%
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  (SELECT e.id_efecte
   FROM EFECTE e
   JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
   WHERE a.nom = 'Defensa'),

  4,   -- defensa màgica
  4,   -- percentatge
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
  critic_multiplicador_base
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
  1.5  -- multiplicador crític
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
  critic_multiplicador_base
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
  1.3  -- multiplicador crític
);

-- =====================================================
-- Defensa per defecte: Guerrer
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
-- Defensa per defecte: Goblin
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
  3,     -- id_obj_actiu de la Ballesta Enverinada
  TRUE   -- equipada
);


-- =====================================================
-- ZOMBI AND SEMIZOMBI
-- =====================================================

-- -----------------------------------------------------
-- VARIABLES DE TIPUS D'EFECTE
-- -----------------------------------------------------

SET @id_tipus_efecte_estat :=
(
  SELECT id_tipus_efecte
  FROM TIPUS_EFECTE
  WHERE tipus_efecte = 2
  LIMIT 1
);

SET @id_tipus_efecte_mod :=
(
  SELECT id_tipus_efecte
  FROM TIPUS_EFECTE
  WHERE tipus_efecte = 3
  LIMIT 1
);


-- =====================================================
-- ZOMBI (-70%)
-- =====================================================

-- Estat Zombi
SET @id_estat_zombi :=
(
  SELECT id_estat
  FROM ESTAT
  WHERE nom = 'Zombi'
  LIMIT 1
);

-- ---------- EFECTE ZOMBI (ESTAT) ----------
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  @id_tipus_efecte_estat,
  NULL,
  1,      -- self
  NULL,   -- indefinit
  @id_accio_placeholder,
  'https://example.com/img/estat_zombi.png',
  'https://example.com/icon/estat_zombi.ico'
);

SET @id_efecte_zombi_estat :=
(
  SELECT id_efecte
  FROM EFECTE
  WHERE id_tipus_efecte = @id_tipus_efecte_estat
    AND id_obj_arm_hab_actiu = @id_accio_placeholder
  ORDER BY id_efecte DESC
  LIMIT 1
);

INSERT INTO EFECTE_ESTAT (id_efecte, id_estat)
VALUES (@id_efecte_zombi_estat, @id_estat_zombi);


-- ---------- EFECTE ZOMBI (DEBUFF -70%) ----------
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  @id_tipus_efecte_mod,
  NULL,
  1,
  NULL,
  @id_accio_placeholder,
  'https://example.com/img/estat_zombi_debuff.png',
  'https://example.com/icon/estat_zombi_debuff.ico'
);

SET @id_efecte_zombi_mod :=
(
  SELECT id_efecte
  FROM EFECTE
  WHERE id_tipus_efecte = @id_tipus_efecte_mod
    AND id_obj_arm_hab_actiu = @id_accio_placeholder
  ORDER BY id_efecte DESC
  LIMIT 1
);

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(@id_efecte_zombi_mod, 2, 4, -70),
(@id_efecte_zombi_mod, 3, 4, -70),
(@id_efecte_zombi_mod, 4, 4, -70),
(@id_efecte_zombi_mod, 5, 4, -70);


-- =====================================================
-- SEMIZOMBI (-40%)
-- =====================================================

SET @id_estat_semizombi :=
(
  SELECT id_estat
  FROM ESTAT
  WHERE nom = 'Semizombi'
  LIMIT 1
);

-- ---------- EFECTE SEMIZOMBI (ESTAT) ----------
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  @id_tipus_efecte_estat,
  NULL,
  1,
  NULL,
  @id_accio_placeholder,
  'https://example.com/img/estat_semizombi.png',
  'https://example.com/icon/estat_semizombi.ico'
);

SET @id_efecte_semizombi_estat :=
(
  SELECT id_efecte
  FROM EFECTE
  WHERE id_tipus_efecte = @id_tipus_efecte_estat
    AND id_obj_arm_hab_actiu = @id_accio_placeholder
  ORDER BY id_efecte DESC
  LIMIT 1
);

INSERT INTO EFECTE_ESTAT (id_efecte, id_estat)
VALUES (@id_efecte_semizombi_estat, @id_estat_semizombi);


-- ---------- EFECTE SEMIZOMBI (DEBUFF -40%) ----------
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  @id_tipus_efecte_mod,
  NULL,
  1,
  NULL,
  @id_accio_placeholder,
  'https://example.com/img/estat_semizombi_debuff.png',
  'https://example.com/icon/estat_semizombi_debuff.ico'
);

SET @id_efecte_semizombi_mod :=
(
  SELECT id_efecte
  FROM EFECTE
  WHERE id_tipus_efecte = @id_tipus_efecte_mod
    AND id_obj_arm_hab_actiu = @id_accio_placeholder
  ORDER BY id_efecte DESC
  LIMIT 1
);

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(@id_efecte_semizombi_mod, 2, 4, -40),
(@id_efecte_semizombi_mod, 3, 4, -40),
(@id_efecte_semizombi_mod, 4, 4, -40),
(@id_efecte_semizombi_mod, 5, 4, -40);
