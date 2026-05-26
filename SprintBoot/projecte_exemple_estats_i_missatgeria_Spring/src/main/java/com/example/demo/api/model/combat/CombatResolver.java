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

        public List<EnemyInstance> allEnemyTargets;

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

        System.out.println(
            "[TOTAL ACTIONS] "
            + actions.size()
        );

        actions.sort(
            Comparator.comparingInt(
                a -> -a.speed
            )
        );

        for (CombatAction action : actions)
        {
            System.out.println(
                "[ACTION LOOP]"
            );

            System.out.println(
                "[RESOLVE TURN] enemy="
                + action.enemy
                + " caster="
                + action.casterId
            );

            try
            {
                resolveAction(action, events);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
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
        System.out.println(
            "[ENTER resolveAction]"
        );
        if (action.action == null)
        {
            return;
        }

        // =========================
        // PLAYER ACTION
        // =========================
        System.out.println(
            "[ACTION ENEMY FLAG] "
            + action.enemy
        );
        if (!action.enemy)
        {
            // PLAYER ACTION

            if (action.casterPlayer == null)
                return;

            if (!action.casterPlayer.isIsAlive())
                return;

            System.out.println(
                "[TARGET TYPE] "
                + action.action.getTargetType()
            );

            // =========================
            // ALL ENEMIES (AOE)
            // =========================

            if (action.enemyTarget == null)
            {
                if (action.allEnemyTargets == null)
                    return;

                for (EnemyInstance enemy : action.allEnemyTargets)
                {
                    if (
                        enemy == null
                        ||
                        !enemy.getBase().isIsAlive()
                    )
                    {
                        continue;
                    }

                    CombatAction copy =
                        new CombatAction();

                    copy.enemy = false;
                    copy.casterId = action.casterId;
                    copy.casterPlayer = action.casterPlayer;
                    copy.enemyTarget = enemy;
                    copy.action = action.action;

                    events.add(
                        new ActionEvent(
                            copy.casterId,
                            enemy.getInstanceId(),
                            copy.action.getId()
                        )
                    );

                    System.out.println(
                        "[APPLY EFFECTS ENEMY] "
                        + enemy.getInstanceId()
                    );

                    applyEffectsToEnemy(
                        copy,
                        events
                    );
                }
                if (action.action == null)
                {
                    System.out.println(
                        "[ACTION NULL]"
                    );

                    return;
                }
                return;
            }

            // =========================
            // SINGLE TARGET
            // =========================

            if (
                !action.enemyTarget
                    .getBase()
                    .isIsAlive()
            )
            {
                return;
            }

            System.out.println(
                "[TARGET ENEMY] "
                + action.enemyTarget.getInstanceId()
            );

            events.add(
                new ActionEvent(
                    action.casterId,
                    action.enemyTarget.getInstanceId(),
                    action.action.getId()
                )
            );

            System.out.println(
                "[APPLY EFFECTS ENEMY]"
            );

            applyEffectsToEnemy(
                action,
                events
            );
        }
        else
        {
            // ENEMY ACTION

            if (action.casterEnemy == null)
                return;

            if (
                !action.casterEnemy
                    .getBase()
                    .isIsAlive()
            )
            {
                return;
            }

            if (action.playerTarget == null)
                return;

            if (!action.playerTarget.isIsAlive())
                return;

            events.add(
                new ActionEvent(
                    action.casterEnemy.getInstanceId(),
                    action.playerTarget.getId(),
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

        for (Efecto efecto :  action.action.getEfectos() )
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
            action.playerTarget.getCurrentHp()
            - damage;

        action.playerTarget.setCurrentHp(hp);

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
            action.playerTarget.getCurrentHp()
            <= 0
        )
        {
           action.playerTarget.setCurrentHp(0);

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
                .getCurrentHp()
            - damage;
 System.out.println(
            "[PLAYER HIT] target="
            + action.enemyTarget.getInstanceId()
            + " hpBefore="
            + action.enemyTarget.getBase().getCurrentHp()
        );
        action.enemyTarget
            .getBase()
            .setCurrentHp(hp);

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
System.out.println(
    "[PLAYER HIT DONE] target="
    + action.enemyTarget.getInstanceId()
    + " hpAfter="
    + action.enemyTarget.getBase().getCurrentHp()
);       
        // =========================
        // DEATH
        // =========================

        if (
            action.enemyTarget
                .getBase()
                .getCurrentHp()
            <= 0
        )
        {
            action.enemyTarget
                .getBase()
                .setCurrentHp(0);

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
// DAMAGE CALC PLAYER
// =====================================

    private static float calculatePlayerDamage(
        Personaje attacker,
        EnemyInstance target,
        Efecto efecto
    )
    {
        float damage;

        // =========================
        // MAGIC DAMAGE
        // =========================

        if (
            efecto.getTipoDanyo() != null
            &&
            efecto.getTipoDanyo() == 1
        )
        {
            damage =
                attacker.getDanyoMagico()
                - target.getBase()
                    .getDefensaMagica();

            System.out.println(
                "[MAGIC DAMAGE] "
                + attacker.getNombre()
                + " -> "
                + damage
            );
        }

        // =========================
        // PHYSICAL DAMAGE
        // =========================

        else
        {
            damage =
                attacker.getDanyoFisico()
                - target.getBase()
                    .getDefensaFisica();

            System.out.println(
                "[PHYSICAL DAMAGE] "
                + attacker.getNombre()
                + " -> "
                + damage
            );
        }

        return Math.max(
            1,
            damage
        );
    }

    // =====================================
    // DAMAGE CALC ENEMY
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

        System.out.println(
            "[ENEMY DAMAGE] "
            + attacker.getBase().getNombre()
            + " -> "
            + damage
        );

        return Math.max(
            1,
            damage
        );
    }
    // =====================================

   
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

            CombatAction action = new CombatAction();
             
            action.enemy = true;

            action.casterId =
                enemy.getInstanceId();

            action.casterEnemy =
                enemy;

            action.playerTarget =
                target;

            action.speed =
                (int) enemy.getBase().getVelocidad();

            if (enemy.getBase().getAcciones() == null || enemy.getBase().getAcciones().isEmpty()  )
            {
                continue;
            }

            action.action =
                enemy.getBase()
                    .getAcciones()
                    .get(0);

            actions.add(action);
        }

        return actions;
    }
}