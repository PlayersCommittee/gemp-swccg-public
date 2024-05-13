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

/**
 * A modifier that adds battle destiny during a battle.
 * This is used to when the source card "Adds X battle destiny".
 */
public class AddsBattleDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private boolean _skipInBattleCheck;

    /**
     * Creates a modifier that adds battle destiny during a battle.
     * @param source the source of the modifier
     * @param modifierAmount the number of destiny to add
     */
    public AddsBattleDestinyModifier(PhysicalCard source, int modifierAmount) {
        this(source, null, new ConstantEvaluator(modifierAmount), source.getOwner(), false);
    }

    /**
     * Creates a modifier that adds battle destiny during a battle.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the number of destiny to add
     */
    public AddsBattleDestinyModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, null, evaluator, source.getOwner(), false);
    }

    /**
     * Creates a modifier that adds battle destiny during a battle.
     * @param source the source of the modifier
     * @param modifierAmount the number of destiny to add
     * @param playerId the player to add battle destiny
     */
    public AddsBattleDestinyModifier(PhysicalCard source, int modifierAmount, String playerId) {
        this(source, null, new ConstantEvaluator(modifierAmount), playerId, false);
    }

    /**
     * Creates a modifier that adds battle destiny during a battle.
     * @param source the source of the modifier
     * @param modifierAmount the number of destiny to add
     * @param playerId the player to add battle destiny
     * @param skipInBattleCheck true if check for source card in battle is skipped, otherwise false
     */
    public AddsBattleDestinyModifier(PhysicalCard source, int modifierAmount, String playerId, boolean skipInBattleCheck) {
        this(source, null, new ConstantEvaluator(modifierAmount), playerId, skipInBattleCheck);
    }

    /**
     * Creates a modifier that adds battle destiny during a battle.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the number of destiny to add
     * @param playerId the player to add battle destiny
     */
    public AddsBattleDestinyModifier(PhysicalCard source, Evaluator evaluator, String playerId) {
        this(source, null, evaluator, playerId, false);
    }

    /**
     * Creates a modifier that adds battle destiny during a battle.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the number of destiny to add
     */
    public AddsBattleDestinyModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        this(source, condition, new ConstantEvaluator(modifierAmount), source.getOwner(), false);
    }

    /**
     * Creates a modifier that adds battle destiny during a battle.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the number of destiny to add
     */
    public AddsBattleDestinyModifier(PhysicalCard source, Condition condition, Evaluator evaluator) {
        this(source, condition, evaluator, source.getOwner(), false);
    }

    /**
     * Creates a modifier that adds battle destiny during a battle.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the number of destiny to add
     * @param playerId the player to add battle destiny
     */
    public AddsBattleDestinyModifier(PhysicalCard source, Condition condition, int modifierAmount, String playerId) {
        this(source, condition, new ConstantEvaluator(modifierAmount), playerId, false);
    }

    /**
     * Creates a modifier that adds battle destiny during a battle.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the number of destiny to add
     * @param playerId the player to add battle destiny
     * @param skipInBattleCheck true if check for source card in battle is skipped, otherwise false
     */
    public AddsBattleDestinyModifier(PhysicalCard source, Condition condition, int modifierAmount, String playerId, boolean skipInBattleCheck) {
        this(source, condition, new ConstantEvaluator(modifierAmount), playerId, skipInBattleCheck);
    }

    /**
     * Creates a modifier that adds battle destiny during a battle.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the number of destiny to add
     * @param playerId the player to add battle destiny
     * @param skipInBattleCheck true if check for source card in battle is skipped, otherwise false
     */
    public AddsBattleDestinyModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId, boolean skipInBattleCheck) {
        super(source, null, Filters.battleLocation, condition, ModifierType.NUM_BATTLE_DESTINY_DRAWS, false);
        _evaluator = evaluator;
        _playerId = playerId;
        _skipInBattleCheck = skipInBattleCheck;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (_playerId.equals(gameState.getDarkPlayer()))
            return "Dark Side: Add " + value + " battle destiny";
        else
            return "Light Side: Add " + value + " battle destiny";
    }

    @Override
    public Condition getAdditionalCondition(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard source = getSource(gameState);
        final int permSourceCardId = source.getPermanentCardId();

        Condition notPreventedCondition = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                return !modifiersQuerying.mayNotAddBattleDestinyDraws(gameState, source);
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

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
    }
}
