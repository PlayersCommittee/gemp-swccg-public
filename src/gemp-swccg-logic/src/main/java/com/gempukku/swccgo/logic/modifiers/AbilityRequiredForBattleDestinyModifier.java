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
 * A modifier that changes the amount of ability required to draw battle destiny at specified locations.
 */
public class AbilityRequiredForBattleDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that changes the amount of ability required to draw battle destiny.
     * @param source the source of the modifier
     * @param value the amount ability required
     * @param playerId the player whose amount of ability required to draw battle destiny is modified
     */
    public AbilityRequiredForBattleDestinyModifier(PhysicalCard source, int value, String playerId) {
        this(source, Filters.any, null, value, playerId);
    }

    /**
     * Creates a modifier that changes the amount of ability required to draw battle destiny at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param value the amount ability required
     * @param playerId the player whose amount of ability required to draw battle destiny is modified
     */
    public AbilityRequiredForBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, int value, String playerId) {
        this(source, locationFilter, null, value, playerId);
    }

    /**
     * Creates a modifier that changes the amount of ability required to draw battle destiny.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param value the amount ability required
     * @param playerId the player whose amount of ability required to draw battle destiny is modified
     */
    public AbilityRequiredForBattleDestinyModifier(PhysicalCard source, Condition condition, int value, String playerId) {
        this(source, Filters.any, condition, new ConstantEvaluator(value), playerId);
    }

    /**
     * Creates a modifier that changes the amount of ability required to draw battle destiny at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param value the amount ability required
     * @param playerId the player whose amount of ability required to draw battle destiny is modified
     */
    public AbilityRequiredForBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int value, String playerId) {
        this(source, locationFilter, condition, new ConstantEvaluator(value), playerId);
    }

    /**
     * Creates a modifier that changes the amount of ability required to draw battle destiny at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose amount of ability required to draw battle destiny is modified
     */
    public AbilityRequiredForBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(Filters.battleLocation, locationFilter), condition, ModifierType.UNMODIFIABLE_ABILITY_REQUIRED_FOR_BATTLE_DESTINY, true);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (_playerId.equals(gameState.getDarkPlayer()))
            return "Dark side requires total ability of " + GuiUtils.formatAsString(value) + " or more to draw battle destiny";
        else
            return "Light side requires total ability of " + GuiUtils.formatAsString(value) + " or more to draw battle destiny";
    }

    @Override
    public float getUnmodifiableAbilityRequiredToDrawBattleDestiny(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (_playerId.equals(playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
        else
            return 0;
    }
}
