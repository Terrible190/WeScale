-- =====================================================
-- DADES DEMO · ZOMBI I SEMIZOMBI
-- =====================================================


-- -----------------------------------------------------
-- ACCIO PLACEHOLDER (Defensa)
-- Necessària perquè EFECTE.id_obj_arm_hab_actiu NO pot ser NULL
-- -----------------------------------------------------

INSERT INTO ACCIO (nom, tipus, imatge, icona, usos)
SELECT 'Defensa', 2,
       'https://example.com/img/defensa.png',
       'https://example.com/icon/defensa.ico',
       NULL
WHERE NOT EXISTS (
    SELECT 1 FROM ACCIO WHERE nom = 'Defensa'
);

-- Guardem l'id de l'acció placeholder
SET @id_accio_placeholder :=
(
  SELECT id_obj_actiu
  FROM ACCIO
  WHERE nom = 'Defensa'
  LIMIT 1
);


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
 id_obj_arm_hab_actiu)
VALUES
(
  @id_tipus_efecte_estat,
  NULL,
  1,      -- self
  NULL,   -- indefinit
  @id_accio_placeholder,
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
 id_obj_arm_hab_actiu)
VALUES
(
  @id_tipus_efecte_mod,
  NULL,
  1,
  NULL,
  @id_accio_placeholder,
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
 id_obj_arm_hab_actiu)
VALUES
(
  @id_tipus_efecte_estat,
  NULL,
  1,
  NULL,
  @id_accio_placeholder,
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
 id_obj_arm_hab_actiu)
VALUES
(
  @id_tipus_efecte_mod,
  NULL,
  1,
  NULL,
  @id_accio_placeholder,
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
