package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier for "May not draw more than X battle destiny" at specified battle locations.
 */
public class MayNotDrawMoreThanBattleDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a "May not draw more than X battle destiny" modifier.
     * @param source the source of the modifier
     * @param modifierAmount the maximum number of destiny to draw
     * @param playerId the player that may not draw more than specified number of battle destiny
     */
    public MayNotDrawMoreThanBattleDestinyModifier(PhysicalCard source, int modifierAmount, String playerId) {
        this(source, Filters.any, null, modifierAmount, playerId);
    }

    /**
     * Creates a "May not draw more than X battle destiny" modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param modifierAmount the maximum number of destiny to draw
     * @param playerId the player that may not draw more than specified number of battle destiny
     */
    public MayNotDrawMoreThanBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, int modifierAmount, String playerId) {
        this(source, locationFilter, null, modifierAmount, playerId);
    }

    /**
     * Creates a "May not draw more than X battle destiny" modifier.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the maximum number of destiny to draw
     * @param playerId the player that may not draw more than specified number of battle destiny
     */
    public MayNotDrawMoreThanBattleDestinyModifier(PhysicalCard source, Condition condition, int modifierAmount, String playerId) {
        this(source, Filters.any, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a "May not draw more than X battle destiny" modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the maximum number of destiny to draw
     * @param playerId the player that may not draw more than specified number of battle destiny
     */
    public MayNotDrawMoreThanBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int modifierAmount, String playerId) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a "May not draw more than X battle destiny" modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the number of destiny
     * @param playerId the player that may not draw more than specified number of battle destiny
     */
    public MayNotDrawMoreThanBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(locationFilter, Filters.battleLocation), condition, ModifierType.MAX_BATTLE_DESTINY_DRAWS, true);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String sideText;
        if (gameState.getDarkPlayer().equals(_playerId))
            sideText = "Dark side";
        else
            sideText = "Light side";

        if (value == 0)
            return sideText + " draws no battle destiny";
        else
            return sideText + " draws no more than " + value + " battle destiny";
    }

    @Override
    public int getMaximumBattleDestinyDrawsModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (_playerId.equals(playerId))
            return (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
        else
            return Integer.MAX_VALUE;
    }
}
