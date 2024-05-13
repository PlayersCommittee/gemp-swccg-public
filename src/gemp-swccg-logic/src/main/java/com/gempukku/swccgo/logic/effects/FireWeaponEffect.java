package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.List;

/**
 * An effect that fires the specified weapon (or permanent weapon).
 */
public class FireWeaponEffect extends AbstractSubActionEffect {
    private PhysicalCard _weapon;
    private boolean _forFree;
    private Filter _targetedAsCharacter;
    private Float _defenseValueAsCharacter;
    private Filter _fireAtTargetFilter;
    private FireWeaponEffect _that;

    /**
     * Creates an effect to fire the specified weapon (or permanent weapon).
     * @param action the action performing this effect
     * @param weapon the weapon to fire
     */
    public FireWeaponEffect(Action action, PhysicalCard weapon) {
        this(action, weapon, false, Filters.none, null, Filters.any);
    }

    /**
     * Creates an effect to fire the specified weapon (or permanent weapon).
     * @param action the action performing this effect
     * @param weapon the weapon to fire
     * @param forFree true if weapon is fired for free, otherwise false
     * @param fireAtTargetFilter the filter for cards that may be targeted by the weapon
     */
    public FireWeaponEffect(Action action, PhysicalCard weapon, boolean forFree, Filter fireAtTargetFilter) {
        this(action, weapon, forFree, Filters.none, null, fireAtTargetFilter);
    }

    /**
     * Creates an effect to fire the specified weapon (or permanent weapon).
     * @param action the action performing this effect
     * @param weapon the weapon to fire
     * @param forFree true if weapon is fired for free, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for cards that may be targeted by the weapon
     */
    public FireWeaponEffect(Action action, PhysicalCard weapon, boolean forFree, Filter targetedAsCharacter, float defenseValueAsCharacter, Filter fireAtTargetFilter) {
        this(action, weapon, forFree, targetedAsCharacter, (Float) defenseValueAsCharacter, fireAtTargetFilter);
    }

    /**
     * Creates an effect to fire the specified weapon (or permanent weapon).
     * @param action the action performing this effect
     * @param weapon the weapon to fire
     * @param forFree true if weapon is fired for free, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for cards that may be targeted by the weapon
     */
    private FireWeaponEffect(Action action, PhysicalCard weapon, boolean forFree, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter) {
        super(action);
        _weapon = weapon;
        _forFree = forFree;
        _targetedAsCharacter = targetedAsCharacter;
        _defenseValueAsCharacter = defenseValueAsCharacter;
        _fireAtTargetFilter = fireAtTargetFilter;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    /**
     * Determines if firing per battle limit is ignored for this weapon firing.
     * @return true or false
     */
    protected boolean isignorePerAttackOrBattleLimit() {
        return false;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        FireWeaponAction fireWeaponAction = _weapon.getBlueprint().getFireWeaponAction(_weapon.getOwner(), game, _weapon, _forFree, 0, _action.getActionSource(), false, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, isignorePerAttackOrBattleLimit());
                        if (fireWeaponAction != null) {
                            List<Modifier> modifiers = _that.getWeaponFiringModifiers(_weapon.getOwner(), game, _weapon);
                            if (modifiers != null) {
                                for (Modifier modifier : modifiers) {
                                    fireWeaponAction.appendBeforeCost(
                                            new AddUntilEndOfWeaponFiringModifierEffect(fireWeaponAction, modifier, null));
                                }
                            }
                            List<ActionProxy> actionProxies = _that.getWeaponFiringActionProxies(_weapon.getOwner(), game, _weapon);
                            if (actionProxies != null) {
                                for (ActionProxy actionProxy : actionProxies) {
                                    fireWeaponAction.appendBeforeCost(
                                            new AddUntilEndOfWeaponFiringActionProxyEffect(fireWeaponAction, actionProxy));
                                }
                            }
                            subAction.appendEffect(
                                    new StackActionEffect(subAction, fireWeaponAction));
                        }
                    }
                });
        return subAction;
    }

    /**
     * This method is called before weapon is fired in order to get any modifiers that will last until
     * the end of weapon firing process.
     * @param playerId the player firing the weapon
     * @param game the game
     * @param weapon the weapon (or card with permanent weapon)
     */
    protected List<Modifier> getWeaponFiringModifiers(String playerId, SwccgGame game, PhysicalCard weapon) {
        return null;
    }

    /**
     * This method is called before weapon is fired in order to get any proxy actions that will last until
     * the end of weapon firing process.
     * @param playerId the player firing the weapon
     * @param game the game
     * @param weapon the weapon (or card with permanent weapon)
     */
    protected List<ActionProxy> getWeaponFiringActionProxies(String playerId, SwccgGame game, PhysicalCard weapon) {
        return null;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
