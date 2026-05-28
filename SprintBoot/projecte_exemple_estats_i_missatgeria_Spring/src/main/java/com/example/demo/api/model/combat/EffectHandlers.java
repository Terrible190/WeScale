package com.example.demo.api.model.combat;

import com.example.demo.api.model.*;
import com.example.demo.components.GameInstance;

class DamageEffectHandler implements EffectHandler {

    @Override
    public void apply(
            Efecto efecto,
            Personaje attacker,
            EnemyInstance targetEnemy,
            Personaje targetAlly,
            GameInstance game) {

        float damage =
                attacker.getDanyoFisico();

        // DAÑO A ENEMIGO
        if(targetEnemy != null)
        {
            targetEnemy.getBase().setHp(
                targetEnemy.getBase().getHp()
                - damage
            );

            System.out.println(
                "[DAMAGE ENEMY] "
                + damage
            );

            return;
        }

        // DAÑO A ALIADO / SELF
        if(targetAlly != null)
        {
            targetAlly.setHp(
                targetAlly.getHp()
                - damage
            );

            System.out.println(
                "[DAMAGE ALLY] "
                + damage
            );
        }
    }
}

class StatusEffectHandler implements EffectHandler {

    @Override
    public void apply(
            Efecto efecto,
            Personaje attacker,
            EnemyInstance targetEnemy,
            Personaje targetAlly,
            GameInstance game) {

        System.out.println(
            "[STATUS ADDED] "
            + efecto.getId()
        );

        if(targetAlly == null)
        {
            targetAlly = attacker;
        }

        targetAlly.getActiveEffects().add(
            new ActiveEffect(
                efecto,
                1
            )
        );

        System.out.println(
            "[ACTIVE EFFECTS] "
            + targetAlly.getActiveEffects().size()
        );
    }
}

class BuffEffectHandler implements EffectHandler {

    @Override
    public void apply(
            Efecto efecto,
            Personaje attacker,
            EnemyInstance targetEnemy,
            Personaje targetAlly,
            GameInstance game) {

        if (!(efecto instanceof EfectoModEstadistica mod))
            return;

        Personaje target =
            targetAlly != null
                ? targetAlly
                : attacker;

        switch (mod.getStat()) {

            case 1: // danyo fisico

                target.setDanyoFisico(
                    aplicarOperacion(
                        target.getDanyoFisico(),
                        mod.getOperacion(),
                        mod.getValor()
                    )
                );

                break;

            case 2: // danyo magico

                target.setDanyoMagico(
                    aplicarOperacion(
                        target.getDanyoMagico(),
                        mod.getOperacion(),
                        mod.getValor()
                    )
                );

                break;

            case 3: // defensa fisica

                target.setDefensaFisica(
                    aplicarOperacion(
                        target.getDefensaFisica(),
                        mod.getOperacion(),
                        mod.getValor()
                    )
                );

                break;

            case 4: // defensa magica

                target.setDefensaMagica(
                    aplicarOperacion(
                        target.getDefensaMagica(),
                        mod.getOperacion(),
                        mod.getValor()
                    )
                );

                break;

            case 5: // velocidad

                target.setVelocidad(
                    aplicarOperacion(
                        target.getVelocidad(),
                        mod.getOperacion(),
                        mod.getValor()
                    )
                );

                break;

            case 6: // critico

                target.setMultiplicadorCritico(
                    aplicarOperacion(
                        target.getMultiplicadorCritico(),
                        mod.getOperacion(),
                        mod.getValor()
                    )
                );

                break;
        }

        System.out.println(
            "[BUFF] stat="
            + mod.getStat()
            + " valor="
            + mod.getValor()
        );
    }

    private float aplicarOperacion(
            float actual,
            int operacion,
            float valor) {

        switch (operacion) {

            case 1: // suma
                return actual + valor;

            case 2: // resta
                return actual - valor;

            case 3: // multiplicar
                return actual * valor;

            default:
                return actual;
        }
    }
private float operar(
        float actual,
        int op,
        float valor)
{
    switch(op)
    {
        case 1:
            return actual + valor;

        case 2:
            return actual * valor;

        case 3:
            return valor;

        case 4:
            return actual * (1 + valor / 100f);

        case 8:
            return actual / valor;

        default:
            return actual;
    }
}
private void aplicarModificador(
        Personaje target,
        EfectoModEstadistica mod)
{
    int stat = mod.getStat();
    int op = mod.getOperacion();
    float value = mod.getValor();

    switch (stat)
    {
        case 1: // HP

            target.setHp(
                operar(
                    target.getHp(),
                    op,
                    value
                )
            );

            break;

        case 2: // Danyo fisico

            target.setDanyoFisico(
                operar(
                    target.getDanyoFisico(),
                    op,
                    value
                )
            );

            break;

        case 3: // Defensa fisica

            target.setDefensaFisica(
                operar(
                    target.getDefensaFisica(),
                    op,
                    value
                )
            );

            break;

        case 4: // Defensa magica

            target.setDefensaMagica(
                operar(
                    target.getDefensaMagica(),
                    op,
                    value
                )
            );

            break;

        case 5: // Danyo magico

            target.setDanyoMagico(
                operar(
                    target.getDanyoMagico(),
                    op,
                    value
                )
            );

            break;

        case 6: // Velocidad

            target.setVelocidad(
                operar(
                    target.getVelocidad(),
                    op,
                    value
                )
            );

            break;
    }
}
}

/*class SummonEffectHandler implements EffectHandler {

    @Override
    public void apply(
            Efecto efecto,
            Personaje attacker,
            EnemyInstance targetEnemy,
            Personaje targetAlly,
            GameInstance game) {

        if (!(efecto instanceof EfectoInvocacion summon))
            return;

        Personaje invocat =
            summon.getInvocado();

        EnemyInstance enemy =
            new EnemyInstance(invocat);

        game.getEnemies().add(enemy);

        System.out.println(
            "[SUMMON] "
            + invocat.getNombre()
        );
    }*/


    