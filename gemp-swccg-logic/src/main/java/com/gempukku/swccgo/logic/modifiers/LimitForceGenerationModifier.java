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
 * A modifier that limits Force generation at a location for the specified player.
 */
public class LimitForceGenerationModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that limits Force generation for the specified player at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force generation is modified
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public LimitForceGenerationModifier(PhysicalCard source, Filterable locationFilter, int modifierAmount, String playerId) {
        this(source, locationFilter, null, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that limits Force generation for the specified player at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force generation is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    private LimitForceGenerationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.LIMIT_FORCE_GENERATION_AT_LOCATION, true);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public Condition getAdditionalCondition(GameState gameState, ModifiersQuerying modifiersQuerying, final PhysicalCard location) {
        PhysicalCard source = getSource(gameState);
        final int permSourceCardId = source.getPermanentCardId();

        // Check if card can limit Force generation for player
        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                return !modifiersQuerying.isImmuneToForceGenerationLimit(gameState, _playerId, location, source);
            }
        };
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String sideText;
        if (gameState.getDarkPlayer().equals(_playerId))
            sideText = "Dark side";
        else
            sideText = "Light side";

        return sideText + " Force generation limited to " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
