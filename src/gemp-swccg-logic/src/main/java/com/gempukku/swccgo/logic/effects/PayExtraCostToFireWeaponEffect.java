package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect for paying extra costs for firing a weapon.
 */
public class PayExtraCostToFireWeaponEffect extends AbstractSubActionEffect {
    private PhysicalCard _weaponCard;
    private SwccgBuiltInCardBlueprint _permanentWeapon;

    /**
     * Creates an effect for paying extra costs for firing a weapon.
     * @param action the action performing this effect
     * @param weaponCard the weapon card being fired, or null if permanent weapon
     * @param permanentWeapon the permanent weapon being fired, or null if not a permanent weapon
     */
    public PayExtraCostToFireWeaponEffect(Action action, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon) {
        super(action);
        _weaponCard = weaponCard;
        _permanentWeapon = permanentWeapon;
    }

    /**
     * Checks whether this effect can be played in full. This is required to check
     * for example for cards that give a choice of effects to carry out and one
     * that can be played in full has to be chosen.
     *
     * @param game the game
     * @return true if (based on current info) the effect should be able to be fully carried out, otherwise false
     */
    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        int extraCost = game.getModifiersQuerying().getExtraForceRequiredToFireWeapon(game.getGameState(), _weaponCard);
        if (extraCost == 0)
            return true;

        String playerId = _weaponCard != null ? _weaponCard.getOwner() : _permanentWeapon.getPhysicalCard(game).getOwner();
        int forceAvailableToUse = game.getModifiersQuerying().getForceAvailableToUse(game.getGameState(), playerId);
        if (forceAvailableToUse < extraCost)
            return false;

        return true;
    }

    /**
     * Gets the sub-action to perform.
     * @param game the game
     * @return the sub-action to perform.
     */
    @Override
    protected SubAction getSubAction(SwccgGame game) {
        int extraCost = game.getModifiersQuerying().getExtraForceRequiredToFireWeapon(game.getGameState(), _weaponCard);

        SubAction subAction = new SubAction(_action);
        if (extraCost > 0) {
            String playerId = _weaponCard != null ? _weaponCard.getOwner() : _permanentWeapon.getPhysicalCard(game).getOwner();
            subAction.appendEffect(new UseForceEffect(subAction, playerId, extraCost));
        }
        return subAction;
    }

    /**
     * Determines if the action process was fully carried out.
     * The class that implements the getSubAction method should implement this method to do any additional checking as to
     * if the action was fully carried out.
     * @return true if the action was fully carried out, otherwise false
     */
    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
