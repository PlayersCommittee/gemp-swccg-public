package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A multiplier modifier to total battle destiny.
 */
public class TotalBattleDestinyMultiplierModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private boolean _skipInBattleCheck;

    /**
     * Creates a multiplier modifier to total battle destiny.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose total battle destiny is modified
     */
    public TotalBattleDestinyMultiplierModifier(PhysicalCard source, float modifierAmount, String playerId) {
        this(source, Filters.any, null, new ConstantEvaluator(modifierAmount), playerId, false);
    }

    /**
     * Creates a multiplier modifier to total battle destiny.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose total battle destiny is modified
     * @param skipInBattleCheck true if check for source card in battle is skipped, otherwise false
     */
    private TotalBattleDestinyMultiplierModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId, boolean skipInBattleCheck) {
        super(source, null, Filters.and(Filters.battleLocation, locationFilter), condition, ModifierType.TOTAL_BATTLE_DESTINY_AT_LOCATION, false);
        _evaluator = evaluator;
        _playerId = playerId;
        _skipInBattleCheck = skipInBattleCheck;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);

        String sideText;
        if (gameState.getSide(_playerId)== Side.DARK)
            sideText = "Dark side total battle destiny";
        else
            sideText = "Light side total battle destiny";

        if (value == 3)
            return sideText + " is tripled";
        else if (value == 2)
            return sideText + " is doubled";
        else
            return sideText + " is *" + value;
    }

    @Override
    public Condition getAdditionalCondition(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        // If source card is a character, device, starship, vehicle, or weapon, then the card must be participating in the battle
        // for its modifier to be applied
        PhysicalCard source = getSource(gameState);
        if (!_skipInBattleCheck
                && Filters.or(CardCategory.CHARACTER, CardCategory.DEVICE, CardCategory.STARSHIP, CardCategory.VEHICLE, CardCategory.WEAPON).accepts(gameState, modifiersQuerying, source)) {
            return new InBattleCondition(source);
        }
        return null;
    }

    @Override
    public float getMultiplierValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
    }
}
