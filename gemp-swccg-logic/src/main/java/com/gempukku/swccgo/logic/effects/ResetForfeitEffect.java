package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to resets the forfeit value of a card.
 */
public class ResetForfeitEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToReset;
    private float _resetValue;
    private Condition _expireCondition;

    /**
     * Creates an effect that resets the forfeit value of a card.
     * @param action the action performing this effect
     * @param cardToReset the card whose forfeit value is reset
     * @param resetValue the reset value
     */
    public ResetForfeitEffect(Action action, PhysicalCard cardToReset, float resetValue) {
        this(action, cardToReset, resetValue, null);
    }

    /**
     * Creates an effect that resets the forfeit value of a card.
     * @param action the action performing this effect
     * @param cardToReset the card whose forfeit value is reset
     * @param resetValue the reset value
     * @param expireCondition the condition that, if fulfilled, causes the reset to expire, or null
     */
    public ResetForfeitEffect(Action action, PhysicalCard cardToReset, float resetValue, Condition expireCondition) {
        super(action);
        _cardToReset = cardToReset;
        _resetValue = resetValue;
        _expireCondition = expireCondition;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check if card's forfeit may not be reduced
        float currentForfeit = modifiersQuerying.getForfeit(gameState, _cardToReset);
        if (_resetValue < currentForfeit && modifiersQuerying.isProhibitedFromHavingForfeitReduced(gameState, _cardToReset)) {
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s forfeit is prevented from being reduced to " + _resetValue);
            return;
        }

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s forfeit is reset to " + GuiUtils.formatAsString(_resetValue));
        gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _cardToReset);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToReset), Filters.in_play);

        Modifier modifier = new ResetForfeitModifier(source, cardFilter, _resetValue);
        modifier.skipSettingNotRemovedOnRestoreToNormal();
        modifier.setExpireCondition(_expireCondition);

        // If during battle and the source if the action is not a weapon, then reset until end of the battle, otherwise
        // lasts for remainder of game (until card leaves play).
        if (gameState.isDuringBattle()
                && !Filters.weapon_or_character_with_permanent_weapon.accepts(gameState, modifiersQuerying, source)) {
            modifiersEnvironment.addUntilEndOfBattleModifier(modifier);
        }
        else {
            modifiersEnvironment.addUntilEndOfGameModifier(modifier);
        }

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), _cardToReset));
    }
}
