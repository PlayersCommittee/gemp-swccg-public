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
 * A modifier that increases the amount of ability required to draw battle destiny at specified locations by a specified amount.
 */
public class IncreaseAbilityRequiredForBattleDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that changes the amount of ability required to draw battle destiny at locations accepted by
     * location filter by a specified amount.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose amount of ability required to draw battle destiny is modified
     */
    public IncreaseAbilityRequiredForBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, int modifierAmount, String playerId) {
        this(source, locationFilter, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier that changes the amount of ability required to draw battle destiny at locations accepted by
     * location filter by a specified amount.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose amount of ability required to draw battle destiny is modified
     */
    public IncreaseAbilityRequiredForBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Evaluator evaluator, String playerId) {
        this(source, locationFilter, null, evaluator, playerId);
    }

    /**
     * Creates a modifier that changes the amount of ability required to draw battle destiny at locations accepted by
     * location filter by a specified amount.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose amount of ability required to draw battle destiny is modified
     */
    public IncreaseAbilityRequiredForBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int modifierAmount, String playerId) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that changes the amount of ability required to draw battle destiny at locations accepted by
     * location filter by a specified amount.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose amount of ability required to draw battle destiny is modified
     */
    public IncreaseAbilityRequiredForBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.ABILITY_REQUIRED_FOR_BATTLE_DESTINY_MODIFIER, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String text;
        if (_playerId.equals(gameState.getDarkPlayer()))
            text = "Dark side ability required for battle destiny ";
        else
            text = "Light side ability required for battle destiny ";

        if (value >= 0)
            text += ("+" + GuiUtils.formatAsString(value));
        else
            text += GuiUtils.formatAsString(value);

        return text;
    }

    @Override
    public float getAbilityRequiredToDrawBattleDestinyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (_playerId.equals(playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
        else
            return 0;
    }
}
