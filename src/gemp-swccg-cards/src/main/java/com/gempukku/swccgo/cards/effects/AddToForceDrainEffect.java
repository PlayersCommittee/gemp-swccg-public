package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForceDrainState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfForceDrainModifierEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.EnhanceForceDrainResult;

/**
 * This effect is used to "Add X to Force drain".
 */
public class AddToForceDrainEffect extends AbstractSubActionEffect {
    private PhysicalCard _source;
    private Evaluator _evaluator;
    private SwccgBuiltInCardBlueprint _permanentWeapon;

    /**
     * Creates an effect to "Add X to Force drain".
     * @param action the action performing this effect
     * @param amount the amount to add
     */
    public AddToForceDrainEffect(Action action, int amount) {
        this(action, new ConstantEvaluator(amount), null);
    }

    /**
     * Creates an effect to "Add X to Force drain" using a permanent weapon.
     * @param action the action performing this effect
     * @param amount the amount to add
     * @param permanentWeapon the permanent weapon adding to the Force drain
     */
    public AddToForceDrainEffect(Action action, int amount, SwccgBuiltInCardBlueprint permanentWeapon) {
        this(action, new ConstantEvaluator(amount), permanentWeapon);
    }

    /**
     * Creates an effect to "Add X to Force drain".
     * @param action the action performing this effect
     * @param evaluator the amount to add
     */
    protected AddToForceDrainEffect(Action action, Evaluator evaluator) {
        this(action, evaluator, null);
    }

    private AddToForceDrainEffect(Action action, Evaluator evaluator, SwccgBuiltInCardBlueprint permanentWeapon) {
        super(action);
        _source = action.getActionSource();
        _evaluator = evaluator;
        _permanentWeapon = permanentWeapon;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        ForceDrainState forceDrainState = game.getGameState().getForceDrainState();
        if (forceDrainState != null) {

            // Amount added is calculated and added as constant modifier
            float amount = _evaluator.evaluateExpression(game.getGameState(), game.getModifiersQuerying(), forceDrainState.getLocation());
            String actionMsg = "Adds " + GuiUtils.formatAsString(amount) + " to Force drain";
            subAction.appendEffect(
                    new AddUntilEndOfForceDrainModifierEffect(subAction,
                            new ForceDrainModifier(_source, Filters.forceDrainLocation, amount, forceDrainState.getPlayerId()), actionMsg));

            // Emit effect result if a weapon card or permanent weapon is enhancing Force drain
            if (Filters.weapon.accepts(game.getGameState(), game.getModifiersQuerying(), _source)) {
                subAction.appendEffect(
                        new UseWeaponEffect(subAction, _source.getAttachedTo(), _source));
                subAction.appendEffect(
                        new TriggeringResultEffect(subAction,
                                new EnhanceForceDrainResult(_action.getPerformingPlayer(), _source)));
            }
            else if (_permanentWeapon != null) {
                subAction.appendEffect(
                        new TriggeringResultEffect(subAction,
                                new EnhanceForceDrainResult(_action.getPerformingPlayer(), _permanentWeapon)));
            }
        }
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
