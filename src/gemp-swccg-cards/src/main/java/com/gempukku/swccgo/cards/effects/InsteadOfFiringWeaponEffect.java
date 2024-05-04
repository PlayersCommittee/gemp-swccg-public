package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

/**
 * An effect that performs another effect "instead of firing weapon".
 */
public class InsteadOfFiringWeaponEffect extends AbstractSubActionEffect {
    private PhysicalCard _weaponOrCardWithPermanentWeapon;
    private StandardEffect _effectToPerform;

    /**
     * Creates an effect that performs another effect "instead of firing a weapon".
     *
     * @param action the action performing this effect
     * @param weapon the weapon (or card with permanent weapon)
     * @param effectToPerform the effect to perform "instead of firing a weapon"
     */
    public InsteadOfFiringWeaponEffect(Action action, PhysicalCard weapon, StandardEffect effectToPerform) {
        super(action);
        _weaponOrCardWithPermanentWeapon = weapon;
        _effectToPerform = effectToPerform;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        PhysicalCard weaponToFire;
                        SwccgBuiltInCardBlueprint permanentWeapon;
                        PhysicalCard cardToFireWeapon;
                        if (Filters.weapon.accepts(game, _weaponOrCardWithPermanentWeapon)) {
                            weaponToFire = _weaponOrCardWithPermanentWeapon;
                            cardToFireWeapon = _weaponOrCardWithPermanentWeapon.getAttachedTo();

                            // Mark weapon as have been used
                            game.getModifiersQuerying().weaponUsedBy(cardToFireWeapon, weaponToFire);
                            // Mark weapon as been "fired"
                            if (game.getGameState().isDuringAttack()) {
                                game.getModifiersQuerying().firedInAttack(weaponToFire, cardToFireWeapon, false);
                            }
                            if (game.getGameState().isDuringBattle()) {
                                game.getModifiersQuerying().firedInBattle(weaponToFire, cardToFireWeapon, false);
                            }
                            if (game.getGameState().isDuringAttackRun()) {
                                game.getModifiersQuerying().firedInAttackRun(weaponToFire, cardToFireWeapon, false);
                            }
                        }
                        else {
                            permanentWeapon = _weaponOrCardWithPermanentWeapon.getBlueprint().getPermanentWeapon(_weaponOrCardWithPermanentWeapon);
                            cardToFireWeapon = _weaponOrCardWithPermanentWeapon;

                            // Mark weapon as have been used
                            game.getModifiersQuerying().weaponUsedBy(cardToFireWeapon, cardToFireWeapon);
                            // Mark weapon as been "fired"
                            if (game.getGameState().isDuringAttack()) {
                                game.getModifiersQuerying().firedInAttack(permanentWeapon, cardToFireWeapon, false);
                            }
                            if (game.getGameState().isDuringBattle()) {
                                game.getModifiersQuerying().firedInBattle(permanentWeapon, cardToFireWeapon, false);
                            }
                            if (game.getGameState().isDuringAttackRun()) {
                                game.getModifiersQuerying().firedInAttackRun(permanentWeapon, cardToFireWeapon, false);
                            }
                        }
                    }
                }
        );
        subAction.appendEffect(_effectToPerform);
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}