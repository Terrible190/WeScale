package com.example.demo.api.model.combat;

import com.example.demo.api.model.*;
import com.example.demo.components.GameInstance;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CombatResolver
{
    // =====================================
    // TYPES
    // =====================================

    public static class CombatAction
    {
        public long casterId;

        public boolean enemy;

        public int speed;

        public Accio action;

        public EnemyInstance enemyTarget;

        public Personaje playerTarget;

        public Personaje casterPlayer;

        public EnemyInstance casterEnemy;
    }

    // =====================================

    public static class CombatEvent
    {
        public String type;
    }

    // =====================================

    public static class ActionEvent
    extends CombatEvent
    {
        public long casterId;

        public long targetId;

        public int actionId;

        public ActionEvent(
            long casterId,
            long targetId,
            int actionId
        )
        {
            this.type = "ACTION";

            this.casterId = casterId;

            this.targetId = targetId;

            this.actionId = actionId;
        }
    }

    // =====================================

    public static class DamageEvent
    extends CombatEvent
    {
        public long casterId;

        public long targetId;

        public float damage;

        public boolean critical;

        public int damageType;

        public DamageEvent(
            long casterId,
            long targetId,
            float damage,
            boolean critical,
            int damageType
        )
        {
            this.type = "DAMAGE";

            this.casterId = casterId;

            this.targetId = targetId;

            this.damage = damage;

            this.critical = critical;

            this.damageType = damageType;
        }
    }

    // =====================================

    public static class StatusEvent
    extends CombatEvent
    {
        public long targetId;

        public int statusId;

        public StatusEvent(
            long targetId,
            int statusId
        )
        {
            this.type = "STATUS";

            this.targetId = targetId;

            this.statusId = statusId;
        }
    }

    // =====================================

    public static class DeathEvent
    extends CombatEvent
    {
        public long targetId;

        public DeathEvent(long targetId)
        {
            this.type = "DEATH";

            this.targetId = targetId;
        }
    }

    // =====================================
    // RESOLVE TURN
    // =====================================

    public static List<CombatEvent> resolveTurn(
        List<CombatAction> actions
    )
    {
        List<CombatEvent> events =
            new ArrayList<>();

        // =========================
        // SPEED ORDER
        // =========================

        actions.sort(
            Comparator.comparingInt(
                a -> -a.speed
            )
        );

        // =========================
        // RESOLVE
        // =========================

        for (CombatAction action : actions)
        {
            resolveAction(
                action,
                events
            );
        }

        return events;
    }

    // =====================================
    // RESOLVE ACTION
    // =====================================

    private static void resolveAction(
        CombatAction action,
        List<CombatEvent> events
    )
    {
        // =========================
        // PLAYER ACTION
        // =========================

        if (!action.enemy)
        {
            if (
                action.casterPlayer == null
            )
            {
                return;
            }

            if (
                !action.casterPlayer.isIsAlive()
            )
            {
                return;
            }

            if (
                action.enemyTarget == null
            )
            {
                return;
            }

            if (
                !action.enemyTarget
                    .getBase()
                    .isIsAlive()
            )
            {
                return;
            }

            events.add(
                new ActionEvent(
                    action.casterId,
                    action.enemyTarget
                        .getInstanceId(),
                    action.action.getId()
                )
            );

            applyEffectsToEnemy(
                action,
                events
            );
        }

        // =========================
        // ENEMY ACTION
        // =========================

        else
        {
            if (
                action.casterEnemy == null
            )
            {
                return;
            }

            if (
                !action.casterEnemy
                    .getBase()
                    .isIsAlive()
            )
            {
                return;
            }

            if (
                action.playerTarget == null
            )
            {
                return;
            }

            if (
                !action.playerTarget
                    .isIsAlive()
            )
            {
                return;
            }

            events.add(
                new ActionEvent(
                    action.casterEnemy
                        .getInstanceId(),

                    action.playerTarget
                        .getId(),

                    action.action.getId()
                )
            );

            applyEffectsToPlayer(
                action,
                events
            );
        }
    }

    // =====================================
    // PLAYER -> ENEMY
    // =====================================

    private static void applyEffectsToEnemy(
        CombatAction action,
        List<CombatEvent> events
    )
    {
        if (
            action.action.getEfectos()
            == null
        )
        {
            return;
        }

        for (
            Efecto efecto :
            action.action.getEfectos()
        )
        {
            switch (efecto.getTipo())
            {
                // =====================
                // DAMAGE
                // =====================

                case 0:

                    applyDamageToEnemy(
                        action,
                        efecto,
                        events
                    );

                    break;

                // =====================
                // STATUS
                // =====================

                case 1:

                    applyStatusToEnemy(
                        action,
                        efecto,
                        events
                    );

                    break;

                // =====================
                // BUFF
                // =====================

                case 2:

                    applyBuffToEnemy(
                        action,
                        efecto,
                        events
                    );

                    break;
            }
        }
    }

    // =====================================
    // ENEMY -> PLAYER
    // =====================================

    private static void applyEffectsToPlayer(
        CombatAction action,
        List<CombatEvent> events
    )
    {
        float damage =
            calculateEnemyDamage(
                action.casterEnemy,
                action.playerTarget
            );

        float hp =
            action.playerTarget.getHp()
            - damage;

        action.playerTarget.setHp(hp);

        events.add(
            new DamageEvent(
                action.casterEnemy
                    .getInstanceId(),

                action.playerTarget
                    .getId(),

                damage,

                false,

                0
            )
        );

        // =========================
        // DEATH
        // =========================

        if (
            action.playerTarget.getHp()
            <= 0
        )
        {
            action.playerTarget
                .setHp(0);

            action.playerTarget
                .setIsAlive(false);

            events.add(
                new DeathEvent(
                    action.playerTarget
                        .getId()
                )
            );
        }
    }

    // =====================================
    // DAMAGE ENEMY
    // =====================================

    private static void applyDamageToEnemy(
        CombatAction action,
        Efecto efecto,
        List<CombatEvent> events
    )
    {
        float damage =
            calculatePlayerDamage(
                action.casterPlayer,
                action.enemyTarget,
                efecto
            );

        float hp =
            action.enemyTarget
                .getBase()
                .getHp()
            - damage;

        action.enemyTarget
            .getBase()
            .setHp(hp);

        events.add(
            new DamageEvent(
                action.casterId,

                action.enemyTarget
                    .getInstanceId(),

                damage,

                false,

                efecto.getTipoDanyo()
            )
        );

        // =========================
        // DEATH
        // =========================

        if (
            action.enemyTarget
                .getBase()
                .getHp()
            <= 0
        )
        {
            action.enemyTarget
                .getBase()
                .setHp(0);

            action.enemyTarget
                .getBase()
                .setIsAlive(false);

            events.add(
                new DeathEvent(
                    action.enemyTarget
                        .getInstanceId()
                )
            );
        }
    }

    // =====================================
    // STATUS
    // =====================================

    private static void applyStatusToEnemy(
        CombatAction action,
        Efecto efecto,
        List<CombatEvent> events
    )
    {
        events.add(
            new StatusEvent(
                action.enemyTarget
                    .getInstanceId(),

                efecto.getId()
            )
        );
    }

    // =====================================
    // BUFF
    // =====================================

    private static void applyBuffToEnemy(
        CombatAction action,
        Efecto efecto,
        List<CombatEvent> events
    )
    {
        System.out.println(
            "[BUFF EFFECT]"
        );
    }

    // =====================================
    // DAMAGE CALC
    // =====================================

    private static float calculatePlayerDamage(
        Personaje attacker,
        EnemyInstance target,
        Efecto efecto
    )
    {
        float damage;

        if (
            efecto.getTipoDanyo()
            != null
            &&
            efecto.getTipoDanyo() == 1
        )
        {
            damage =
                attacker.getDanyoMagico()
                - target.getBase()
                    .getDefensaMagica();
        }
        else
        {
            damage =
                attacker.getDanyoFisico()
                - target.getBase()
                    .getDefensaFisica();
        }

        return Math.max(
            1,
            damage
        );
    }

    // =====================================

    private static float calculateEnemyDamage(
        EnemyInstance attacker,
        Personaje target
    )
    {
        float damage =
            attacker.getBase()
                .getDanyoFisico()
            - target.getDefensaFisica();

        return Math.max(
            1,
            damage
        );
    }

    // =====================================
    // ENEMY AI
    // =====================================

    public static List<CombatAction> createEnemyActions(
        List<EnemyInstance> enemies,
        List<Player> players,
        GameInstance game
    )
    {
        List<CombatAction> actions =
            new ArrayList<>();

        Random random =
            new Random();

        List<Player> alivePlayers =
            players.stream()
            .filter(p -> {
                Personaje pj =
                    game.getSeleccionados()
                    .get(p.getId());

                return
                    pj != null
                    &&
                    pj.isIsAlive();
            })
            .toList();

        for (
            EnemyInstance enemy :
            enemies
        )
        {
            if (
                !enemy.getBase()
                    .isIsAlive()
            )
            {
                continue;
            }

            if (
                alivePlayers.isEmpty()
            )
            {
                break;
            }

            Player targetPlayer =
                alivePlayers.get(
                    random.nextInt(
                        alivePlayers.size()
                    )
                );

            Personaje target =
                game.getSeleccionados()
                    .get(
                        targetPlayer.getId()
                    );

            CombatAction action =
                new CombatAction();

            action.enemy = true;

            action.casterId =
                enemy.getInstanceId();

            action.casterEnemy =
                enemy;

            action.playerTarget =
                target;

            action.speed =  (int)  enemy.getBase().getVelocidad();

            actions.add(action);
        }

        return actions;
    }
}