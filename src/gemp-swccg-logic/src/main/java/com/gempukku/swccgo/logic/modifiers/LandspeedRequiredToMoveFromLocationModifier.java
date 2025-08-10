package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to the amount of landspeed required to move from specified locations using landspeed.
 */
public class LandspeedRequiredToMoveFromLocationModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _movingCardFilter;

    /**
     * Creates a move using landspeed cost modifier when moving from the source location using landspeed.
     * @param source the card that is the source of the modifier and whose move using landspeed cost is modified
     * @param modifierAmount the amount of the modifier
     * @param movingCardFilter the filter for cards affected by this modifier
     * @param playerId the player whose cards are affected
     */
    public LandspeedRequiredToMoveFromLocationModifier(PhysicalCard source, int modifierAmount, Filterable movingCardFilter, String playerId) {
        this(source, source, null, new ConstantEvaluator(modifierAmount), movingCardFilter, playerId);
    }

    /**
     * Creates a move using landspeed cost modifier when moving from specified locations using landspeed.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param movingCardFilter the filter for cards affected by this modifier
     * @param playerId the player whose cards are affected
     */
    private LandspeedRequiredToMoveFromLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, Filterable movingCardFilter, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MOVE_FROM_LOCATION_LANDSPEED_REQUIREMENT, false);
        _evaluator = evaluator;
        _movingCardFilter = Filters.and(movingCardFilter);
        _playerId = playerId;
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_movingCardFilter).accepts(gameState, modifiersQuerying, targetCard);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
    }
}
