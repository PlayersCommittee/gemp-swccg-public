package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that adds destiny to attrition during a battle.
 * This is used to when the source card "Adds X destiny to attrition [only]".
 */
public class AddsDestinyToAttritionModifier extends NumDestinyDrawsToAttritionOnlyModifier {
    private boolean _skipInBattleCheck;

    /**
     * Creates a modifier that adds destiny to attrition during a battle.
     * @param source the source of the modifier
     * @param modifierAmount the number of destiny to add
     */
    public AddsDestinyToAttritionModifier(PhysicalCard source, int modifierAmount) {
        this(source, null, new ConstantEvaluator(modifierAmount), source.getOwner(), false);
    }

    /**
     * Creates a modifier that adds destiny to attrition during a battle.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the number of destiny to add
     */
    public AddsDestinyToAttritionModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, null, evaluator, source.getOwner(), false);
    }

    /**
     * Creates a modifier that adds destiny to attrition during a battle.
     * @param source the source of the modifier
     * @param modifierAmount the number of destiny to add
     * @param playerId the player to add destiny to attrition
     */
    public AddsDestinyToAttritionModifier(PhysicalCard source, int modifierAmount, String playerId) {
        this(source, null, new ConstantEvaluator(modifierAmount), playerId, false);
    }

    /**
     * Creates a modifier that adds destiny to attrition during a battle.
     * @param source the source of the modifier
     * @param modifierAmount the number of destiny to add
     * @param playerId the player to add destiny to attrition
     * @param skipInBattleCheck true if check for source card in battle is skipped, otherwise false
     */
    public AddsDestinyToAttritionModifier(PhysicalCard source, int modifierAmount, String playerId, boolean skipInBattleCheck) {
        this(source, null, new ConstantEvaluator(modifierAmount), playerId, skipInBattleCheck);
    }

    /**
     * Creates a modifier that adds destiny to attrition during a battle.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the number of destiny to add
     * @param playerId the player to add destiny to attrition
     */
    public AddsDestinyToAttritionModifier(PhysicalCard source, Evaluator evaluator, String playerId) {
        this(source, null, evaluator, playerId, false);
    }

    /**
     * Creates a modifier that adds destiny to attrition during a battle.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the number of destiny to add
     */
    public AddsDestinyToAttritionModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        this(source, condition, new ConstantEvaluator(modifierAmount), source.getOwner(), false);
    }

    /**
     * Creates a modifier that adds destiny to attrition during a battle.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the number of destiny to add
     */
    public AddsDestinyToAttritionModifier(PhysicalCard source, Condition condition, Evaluator evaluator) {
        this(source, condition, evaluator, source.getOwner(), false);
    }

    /**
     * Creates a modifier that adds destiny to attrition during a battle.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the number of destiny to add
     * @param playerId the player to add destiny to attrition
     */
    public AddsDestinyToAttritionModifier(PhysicalCard source, Condition condition, int modifierAmount, String playerId) {
        this(source, condition, new ConstantEvaluator(modifierAmount), playerId, false);
    }

    /**
     * Creates a modifier that adds destiny to attrition during a battle.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the number of destiny to add
     * @param playerId the player to add destiny to attrition
     * @param skipInBattleCheck true if check for source card in battle is skipped, otherwise false
     */
    public AddsDestinyToAttritionModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId, boolean skipInBattleCheck) {
        super(source, condition, evaluator, playerId, Filters.battleLocation);
        _skipInBattleCheck = skipInBattleCheck;
    }

    @Override
    public Condition getAdditionalCondition(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard source = getSource(gameState);
        final int permSourceCardId = source.getPermanentCardId();

        Condition notPreventedCondition = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                return !modifiersQuerying.mayNotAddDestinyDrawsToAttrition(gameState, source.getOwner());
            }
        };
        // If source card is a character, device, starship, vehicle, or weapon, then the card must be participating in
        // the battle for its modifier to be applied
        if (!_skipInBattleCheck
                && Filters.or(CardCategory.CHARACTER, CardCategory.DEVICE, CardCategory.STARSHIP, CardCategory.VEHICLE, CardCategory.WEAPON).accepts(gameState, modifiersQuerying, source)) {
            return new AndCondition(notPreventedCondition, new InBattleCondition(source));
        }
        return notPreventedCondition;
    }
}
