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
('Black Flash Mark');

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
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2),
  2,
  2,
  3,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Bola de Foc'),
  'https://example.com/img/efecte_cremat.png',
  'https://example.com/icon/efecte_cremat.ico'
);

-- =====================================================
-- EFECTE: Estat Enverinat (físic)
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
-- EFECTE_ESTAT
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
     AND e.id_tipus_efecte = (
       SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2 
     )	
	),
  (SELECT id_estat FROM ESTAT WHERE nom = 'Cremat')
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
-- EFECTE_MOD_ESTADISTICA
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
  critic_multiplicador_base
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
  1.2
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
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2 LIMIT 1),
  NULL,
  1,
  NULL,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1),
  'estat_zombi.png',
  'estat_zombi.ico'
);

INSERT INTO EFECTE_ESTAT
(id_efecte, id_estat)
VALUES
(
  (
    SELECT e.id_efecte
    FROM EFECTE e
    JOIN TIPUS_EFECTE t ON t.id_tipus_efecte = e.id_tipus_efecte
    WHERE t.tipus_efecte = 2
      AND e.id_obj_arm_hab_actiu =
          (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1)
    ORDER BY e.id_efecte DESC
    LIMIT 1
  ),
  (SELECT id_estat FROM ESTAT WHERE nom = 'Zombi' LIMIT 1)
);


-- ---------- EFECTE ZOMBI (DEBUFF -70%) ----------
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 3 LIMIT 1),
  NULL,
  1,
  NULL,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1),
  'estat_zombi_debuff.png',
  'estat_zombi_debuff.ico'
);


INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  (SELECT e.id_efecte FROM EFECTE e JOIN TIPUS_EFECTE t ON t.id_tipus_efecte = e.id_tipus_efecte WHERE t.tipus_efecte = 3 AND e.id_obj_arm_hab_actiu = (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1) ORDER BY e.id_efecte DESC LIMIT 1),
  2, 4, -70
),
(
  (SELECT e.id_efecte FROM EFECTE e JOIN TIPUS_EFECTE t ON t.id_tipus_efecte = e.id_tipus_efecte WHERE t.tipus_efecte = 3 AND e.id_obj_arm_hab_actiu = (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1) ORDER BY e.id_efecte DESC LIMIT 1),
  3, 4, -70
),
(
  (SELECT e.id_efecte FROM EFECTE e JOIN TIPUS_EFECTE t ON t.id_tipus_efecte = e.id_tipus_efecte WHERE t.tipus_efecte = 3 AND e.id_obj_arm_hab_actiu = (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1) ORDER BY e.id_efecte DESC LIMIT 1),
  4, 4, -70
),
(
  (SELECT e.id_efecte FROM EFECTE e JOIN TIPUS_EFECTE t ON t.id_tipus_efecte = e.id_tipus_efecte WHERE t.tipus_efecte = 3 AND e.id_obj_arm_hab_actiu = (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1) ORDER BY e.id_efecte DESC LIMIT 1),
  5, 4, -70
);


-- =====================================================
-- SEMIZOMBI (-40%)
-- =====================================================

-- ---------- EFECTE SEMIZOMBI (ESTAT) ----------
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio, id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2 LIMIT 1),
  NULL,
  1,
  NULL,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1),
  'estat_semizombi.png',
  'estat_semizombi.ico'
);

-- Vincular EFECTE ↔ ESTAT Semizombi
INSERT INTO EFECTE_ESTAT (id_efecte, id_estat)
VALUES
(
  (SELECT e.id_efecte FROM EFECTE e WHERE e.id_tipus_efecte = (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2 LIMIT 1) AND e.id_obj_arm_hab_actiu = (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1) ORDER BY e.id_efecte DESC LIMIT 1),
  (SELECT id_estat FROM ESTAT WHERE nom = 'Semizombi' LIMIT 1)
);

-- ---------- EFECTE SEMIZOMBI (DEBUFF -40%) ----------
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio, id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 3 LIMIT 1),
  NULL,
  1,
  NULL,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1),
  'estat_semizombi_debuff.png',
  'estat_semizombi_debuff.ico'
);

-- Debuff global -40%
INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  (SELECT e.id_efecte FROM EFECTE e WHERE e.id_tipus_efecte = (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 3 LIMIT 1) AND e.id_obj_arm_hab_actiu = (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1) ORDER BY e.id_efecte DESC LIMIT 1),
  2, 4, -40
),
(
  (SELECT e.id_efecte FROM EFECTE e WHERE e.id_tipus_efecte = (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 3 LIMIT 1) AND e.id_obj_arm_hab_actiu = (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1) ORDER BY e.id_efecte DESC LIMIT 1),
  3, 4, -40
),
(
  (SELECT e.id_efecte FROM EFECTE e WHERE e.id_tipus_efecte = (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 3 LIMIT 1) AND e.id_obj_arm_hab_actiu = (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1) ORDER BY e.id_efecte DESC LIMIT 1),
  4, 4, -40
),
(
  (SELECT e.id_efecte FROM EFECTE e WHERE e.id_tipus_efecte = (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 3 LIMIT 1) AND e.id_obj_arm_hab_actiu = (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'ESTAT_INTERNAL' LIMIT 1) ORDER BY e.id_efecte DESC LIMIT 1),
  5, 4, -40
);


-- =====================================================
-- ACCIO Angel Custodi - Objecte
-- =====================================================


INSERT INTO ACCIO
(nom, tipus, imatge, icona, usos)
VALUES
(
  'Àngel Custodi',
  3, -- 3 = objecte actiu
  'https://example.com/img/angel_custodi.png',
  'https://example.com/icon/angel_custodi.ico',
  1  -- ús únic
);


INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2 LIMIT 1), -- Estat
  NULL,
  3,      -- self
  NULL,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Àngel Custodi' LIMIT 1),
  'angel_custodi_effect.png',
  'angel_custodi_effect.ico'
);

INSERT INTO EFECTE_INTERACCIO
(id_efecte_origen, id_estat_objectiu, accio)
VALUES
(
  (SELECT id_efecte
   FROM EFECTE e
   JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
   WHERE a.nom = 'Àngel Custodi'
   LIMIT 1),

  (SELECT id_estat FROM ESTAT WHERE nom = 'Mort' LIMIT 1),

  1 -- eliminar estat
);

INSERT INTO EFECTE_INTERACCIO
(
  id_efecte_origen,
  id_estat_objectiu,
  accio,
  id_estat_resultat,
  delay_torns
)
VALUES
(
  -- EFECTE que aplica l'estat Zombi
  (
    SELECT e.id_efecte
    FROM EFECTE e
    JOIN EFECTE_ESTAT ee ON ee.id_efecte = e.id_efecte
    JOIN ESTAT s ON s.id_estat = ee.id_estat
    WHERE s.nom = 'Zombi'
    LIMIT 1
  ),

  NULL, -- no actua sobre un estat concret, sinó sobre el temps

  2,    -- 2 = afegir estat

  -- Estat que s’afegirà
  (
    SELECT id_estat
    FROM ESTAT
    WHERE nom = 'Semizombi'
    LIMIT 1
  ),

  3     -- després de 3 torns
);


-- =====================================================
-- ACCIO Resurrecio - Habilitat
-- =====================================================
INSERT INTO ACCIO
(nom, tipus, imatge, icona, usos)
VALUES
(
  'Resurrecció',
  2, -- habilitat
  'https://example.com/img/resurreccio.png',
  'https://example.com/icon/resurreccio.ico',
  NULL
);
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2 LIMIT 1), -- Estat
  NULL,
  3,      -- seleccionat (aliat mort)
  NULL,
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Resurrecció' LIMIT 1),
  'resurreccio_effect.png',
  'resurreccio_effect.ico'
);

INSERT INTO EFECTE_INTERACCIO
(id_efecte_origen, id_estat_objectiu, accio)
VALUES
(
  (SELECT id_efecte
   FROM EFECTE e
   JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
   WHERE a.nom = 'Resurrecció'
   LIMIT 1),

  (SELECT id_estat FROM ESTAT WHERE nom = 'Mort' LIMIT 1),

  1 -- eliminar
);
INSERT INTO EFECTE_INTERACCIO
(id_efecte_origen, id_estat_objectiu, accio, id_estat_resultat)
VALUES
(
  (SELECT id_efecte
   FROM EFECTE e
   JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
   WHERE a.nom = 'Resurrecció'
   LIMIT 1),

  (SELECT id_estat FROM ESTAT WHERE nom = 'Mort' LIMIT 1),

  2, -- afegir
  (SELECT id_estat FROM ESTAT WHERE nom = 'Zombi' LIMIT 1)
);

-- =====================================================
-- ACCIO: Habilitat d'invocació "Invocar Esquelet"
-- =====================================================

INSERT INTO ACCIO
(nom, tipus, imatge, icona, usos)
VALUES
(
  'Invocar Esquelet',
  2, -- 2 = habilitat
  'https://example.com/img/invocar_esquelet.png',
  'https://example.com/icon/invocar_esquelet.ico',
  1 -- usos
);
-- =====================================================
-- EFECTE: Invocació d'Esquelet
-- =====================================================

INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio,
 id_obj_arm_hab_actiu, imatge, icona)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 1), -- Invocació
  NULL,
  1,    -- self
  NULL, -- instantani
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Invocar Esquelet'),
  'invocar_esquelet_effect.png',
  'invocar_esquelet_effect.ico'
);

-- =====================================================
-- EFECTE_INVOCACIO: invoca un Esquelet
-- =====================================================

INSERT INTO EFECTE_INVOCACIO
(id_efecte, id_invocacio, id_personatge)
VALUES
(
  (
    SELECT e.id_efecte
    FROM EFECTE e
    JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
    WHERE a.nom = 'Invocar Esquelet'
      AND e.id_tipus_efecte = (
        SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 1
      )
  ),
  (SELECT id_personatge FROM PERSONATGE WHERE nom = 'Esquelet'),
  (SELECT id_personatge FROM PERSONATGE WHERE nom = 'Guerrer')
);



-- =====================================================
-- ACCIO: Black Flash
-- =====================================================
INSERT INTO ACCIO (nom, tipus, usos)
VALUES ('Black Flash', 2, NULL);
INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio, id_obj_arm_hab_actiu)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2),
  NULL,
  3, -- seleccionat
  1, -- dura 1 torn
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Black Flash')
);
INSERT INTO EFECTE_ESTAT (id_efecte, id_estat)
VALUES
(
  (
    SELECT e.id_efecte
    FROM EFECTE e
    JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
    WHERE a.nom = 'Black Flash'
      AND e.id_tipus_efecte =
          (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 2)
    ORDER BY e.id_efecte DESC
    LIMIT 1
  ),
  (SELECT id_estat FROM ESTAT WHERE nom = 'Black Flash Mark')
);

INSERT INTO EFECTE
(id_tipus_efecte, tipus_dany, rang, duracio, id_obj_arm_hab_actiu)
VALUES
(
  (SELECT id_tipus_efecte FROM TIPUS_EFECTE WHERE tipus_efecte = 3),
  1, -- dany físic
  2, -- enemic
  0, -- immediat
  (SELECT id_obj_actiu FROM ACCIO WHERE nom = 'Black Flash')
);

INSERT INTO EFECTE_MOD_ESTADISTICA
(id_efecte, nom_stat, operacio, valor)
VALUES
(
  (
    SELECT e.id_efecte
    FROM EFECTE e
    JOIN TIPUS_EFECTE t ON t.id_tipus_efecte = e.id_tipus_efecte
    JOIN ACCIO a ON a.id_obj_actiu = e.id_obj_arm_hab_actiu
    WHERE a.nom = 'Black Flash'
      AND t.tipus_efecte = 3
    ORDER BY e.id_efecte DESC
    LIMIT 1
  ),
  2, -- dany físic
  1, -- suma
  10 -- EXTRA de dany (ajusta’l com vulguis)
);

INSERT INTO EFECTE_INTERACCIO
(id_efecte_origen, id_estat_objectiu, accio, id_estat_resultat, delay_torns)
VALUES
(
  (
    SELECT e.id_efecte
    FROM EFECTE e
    JOIN EFECTE_ESTAT ee ON ee.id_efecte = e.id_efecte
    JOIN ESTAT s ON s.id_estat = ee.id_estat
    WHERE s.nom = 'Black Flash Mark'
    LIMIT 1
  ),
  (SELECT id_estat FROM ESTAT WHERE nom = 'Black Flash Mark'),
  3, -- reemplaçar / trigger especial
  (SELECT id_estat FROM ESTAT WHERE nom = 'Black Flash Mark'),
  1  -- al següent torn
);
