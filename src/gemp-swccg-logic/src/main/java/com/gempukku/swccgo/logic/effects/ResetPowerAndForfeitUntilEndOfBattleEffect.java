package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ResetPowerModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to resets the power and forfeit of a card until end of the current battle.
 */
public class ResetPowerAndForfeitUntilEndOfBattleEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToReset;
    private float _resetValue;

    /**
     * Creates an effect that resets the power and forfeit of a card until end of the current battle.
     * @param action the action performing this effect
     * @param cardToReset the card whose power is reset
     * @param resetValue the reset value
     */
    public ResetPowerAndForfeitUntilEndOfBattleEffect(Action action, PhysicalCard cardToReset, float resetValue) {
        super(action);
        _cardToReset = cardToReset;
        _resetValue = resetValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        String performingPlayerId = _action.getPerformingPlayer();

        // Check if card's power may not be reduced
        boolean resetPower = true;
        float currentPower = modifiersQuerying.getPower(gameState, _cardToReset);
        if (_resetValue < currentPower && modifiersQuerying.isProhibitedFromHavingPowerReduced(gameState, _cardToReset, performingPlayerId)) {
            resetPower = false;
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s power is prevented from being reduced");
        }

        // Check if card's forfeit may not be reduced
        boolean resetForfeit = true;
        float currentForfeit = modifiersQuerying.getForfeit(gameState, _cardToReset);
        if (_resetValue < currentForfeit && modifiersQuerying.isProhibitedFromHavingForfeitReduced(gameState, _cardToReset)) {
            resetForfeit = false;
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s forfeit is prevented from being reduced");
        }

        if (!resetPower && !resetForfeit) {
            return;
        }

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        if (!resetPower) {
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s forfeit is reset to " + GuiUtils.formatAsString(_resetValue) + " until end of battle");
        }
        else if (!resetForfeit) {
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s power is reset to " + GuiUtils.formatAsString(_resetValue) + " until end of battle");
        }
        else {
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s power and forfeit are reset to " + GuiUtils.formatAsString(_resetValue) + " until end of battle");
        }

        gameState.cardAffectsCard(performingPlayerId, source, _cardToReset);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToReset), Filters.in_play);

        if (resetPower) {
            modifiersEnvironment.addUntilEndOfBattleModifier(
                    new ResetPowerModifier(source, cardFilter, _resetValue));
        }
        if (resetForfeit) {
            modifiersEnvironment.addUntilEndOfBattleModifier(
                    new ResetForfeitModifier(source, cardFilter, _resetValue));
        }

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(performingPlayerId, _cardToReset));
    }
}
