package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A Force drain modifier.
 */
public class ForceDrainModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a Force drain modifier.
     * @param source the location card that is the source of the modifier and where Force drain is modified
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force drain is modified
     */
    public ForceDrainModifier(PhysicalCard source, float modifierAmount, String playerId) {
        this(source, source, null, modifierAmount, playerId);
    }

    /**
     * Creates a Force drain modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force drain is modified
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force drain is modified
     */
    public ForceDrainModifier(PhysicalCard source, Filterable locationFilter, float modifierAmount, String playerId) {
        this(source, locationFilter, null, modifierAmount, playerId);
    }

    /**
     * Creates a Force drain modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force drain is modified
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force drain is modified
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public ForceDrainModifier(PhysicalCard source, Filterable locationFilter, float modifierAmount, String playerId, boolean cumulative) {
        this(source, locationFilter, null, new ConstantEvaluator(modifierAmount), playerId, cumulative);
    }

    /**
     * Creates a Force drain modifier.
     * @param source the location card that is the source of the modifier and where Force drain is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force drain is modified
     */
    public ForceDrainModifier(PhysicalCard source, Condition condition, float modifierAmount, String playerId) {
        this(source, source, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a Force drain modifier.
     * @param source the location card that is the source of the modifier and where Force drain is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force drain is modified
     */
    public ForceDrainModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        this(source, source, condition, evaluator, playerId);
    }

    /**
     * Creates a Force drain modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force drain is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force drain is modified
     */
    public ForceDrainModifier(PhysicalCard source, Filterable locationFilter, Condition condition, float modifierAmount, String playerId) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a Force drain modifier.
     * @param source the location card that is the source of the modifier and where Force drain is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force drain is modified
     */
    public ForceDrainModifier(PhysicalCard source, Evaluator evaluator, String playerId) {
        this(source, source, null, evaluator, playerId);
    }

    /**
     * Creates a Force drain modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force drain is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force drain is modified
     */
    public ForceDrainModifier(PhysicalCard source, Filterable locationFilter, Evaluator evaluator, String playerId) {
        this(source, locationFilter, null, evaluator, playerId);
    }

    /**
     * Creates a Force drain modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force drain is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force drain is modified
     */
    public ForceDrainModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        this(source, locationFilter, condition, evaluator, playerId, false);
    }

    /**
     * Creates a Force drain modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force drain is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force drain is modified
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public ForceDrainModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId, boolean cumulative) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.FORCE_DRAIN_AMOUNT, cumulative);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public Condition getAdditionalCondition(GameState gameState, ModifiersQuerying modifiersQuerying, final PhysicalCard location) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
        PhysicalCard source = getSource(gameState);
        final int permSourceCardId = source.getPermanentCardId();

        // Check if card can modify Force drains for player
        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                return !modifiersQuerying.isForceDrainModifierCanceled(gameState, location, source, source.getOwner(), _playerId, value);
            }
        };
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
        String sideText;
        if (gameState.getSide(_playerId)==Side.DARK)
            sideText = "Dark side";
        else
            sideText = "Light side";

        if (value >= 0)
            return sideText + " Force drain +" + GuiUtils.formatAsString(value);
        else
            return sideText + " Force drain " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getForceDrainModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        if (_playerId.equals(playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
        else
            return 0;
    }
}
