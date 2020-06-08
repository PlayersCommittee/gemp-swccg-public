package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForceDrainState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfForceDrainModifierEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * This effect is used to "Reduce Force drain by X".
 */
public class ReduceForceDrainEffect extends AbstractSubActionEffect {
    private PhysicalCard _source;
    private Evaluator _evaluator;

    /**
     * Creates an effect to "Reduce Force drain by X".
     * @param action the action performing this effect
     * @param amount the amount to reduce
     */
    public ReduceForceDrainEffect(Action action, int amount) {
        this(action, new ConstantEvaluator(amount));
    }

    /**
     * Creates an effect to "Reduce Force drain by X".
     * @param action the action performing this effect
     * @param evaluator the amount to reduce
     */
    protected ReduceForceDrainEffect(Action action, Evaluator evaluator) {
        super(action);
        _source = action.getActionSource();
        _evaluator = evaluator;
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

            // Amount reduced is calculated and added as constant modifier
            float amount = _evaluator.evaluateExpression(game.getGameState(), game.getModifiersQuerying(), forceDrainState.getLocation());
            String actionMsg = "Reduces Force drain by " + GuiUtils.formatAsString(amount);
            subAction.appendEffect(
                    new AddUntilEndOfForceDrainModifierEffect(subAction,
                            new ForceDrainModifier(_source, Filters.forceDrainLocation, -amount, forceDrainState.getPlayerId()), actionMsg));
        }
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
