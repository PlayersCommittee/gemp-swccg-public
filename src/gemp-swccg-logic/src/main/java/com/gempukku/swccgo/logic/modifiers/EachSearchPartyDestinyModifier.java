package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that affects each search party destiny at the specified location.
 */
public class EachSearchPartyDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that affects each search party destiny for the specified player at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param modifierAmount the amount of the modifier
     * @param playerId the player
     */
    public EachSearchPartyDestinyModifier(PhysicalCard source, Filterable locationFilter, float modifierAmount, String playerId) {
        this(source, locationFilter, null, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that each search party destiny for the specified player at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player
     */
    protected EachSearchPartyDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.EACH_SEARCH_PARTY_DESTINY_AT_LOCATION, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Each search party destiny +" + GuiUtils.formatAsString(value);
        else
            return "Each search party destiny " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
