package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A destiny modifier for destiny drawn for an action with a specified card as the action source.
 */
public class DestinyDrawForActionSourceModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a destiny modifier for destiny drawn for either player for an action with a card accepted by the filter
     * as the action source.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param modifierAmount the amount of the modifier
     */
    public DestinyDrawForActionSourceModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount), null);
    }

    /**
     * Creates a destiny modifier for destiny drawn for either player for an action with a card accepted by the filter
     * as the action source.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose destiny draws are affected
     */
    public DestinyDrawForActionSourceModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, String playerId) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount), playerId);
    }


    /**
     * Creates a destiny modifier for destiny drawn for either player for an action with a card accepted by the filter
     * as the action source.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition in order for this to trigger
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose destiny draws are affected
     */
    public DestinyDrawForActionSourceModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, String playerId) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a destiny modifier for destiny drawn for the specified player for an action with a card accepted by the
     * filter as the action source.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose destiny draws are affected
     */
    private DestinyDrawForActionSourceModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, affectFilter, condition, ModifierType.EACH_DESTINY_DRAW_FOR_ACTION_SOURCE, false);
        _playerId = playerId;
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String sideText;
        if (_playerId==null)
            sideText = "Each";
        else if (gameState.getDarkPlayer().equals(_playerId))
            sideText = "Each dark side";
        else
            sideText = "Each light side";

        if (value >= 0)
            return sideText + " destiny draw +" + GuiUtils.formatAsString(value);
        else
            return sideText + " destiny draw " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getDestinyDrawFromSourceCardModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard destinyDrawActionSource) {
        if (isForPlayer(playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, destinyDrawActionSource);
        else
            return 0;
    }
}
