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
import com.gempukku.swccgo.logic.modifiers.ResetPowerModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to resets the power of a card until end of the turn.
 */
public class ResetPowerUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToReset;
    private float _resetValue;

    /**
     * Creates an effect that resets the power of a card until end of the turn.
     * @param action the action performing this effect
     * @param cardToReset the card whose power is reset
     * @param resetValue the reset value
     */
    public ResetPowerUntilEndOfTurnEffect(Action action, PhysicalCard cardToReset, float resetValue) {
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
        float currentPower = modifiersQuerying.getPower(gameState, _cardToReset);
        if (_resetValue < currentPower && modifiersQuerying.isProhibitedFromHavingPowerReduced(gameState, _cardToReset, performingPlayerId)) {
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s power is prevented from being reduced to " + _resetValue);
            return;
        }

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s power is reset to " + GuiUtils.formatAsString(_resetValue) + " until end of the turn");
        gameState.cardAffectsCard(performingPlayerId, source, _cardToReset);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToReset), Filters.in_play);

        modifiersEnvironment.addUntilEndOfTurnModifier(
                new ResetPowerModifier(source, cardFilter, _resetValue));

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(performingPlayerId, _cardToReset));
    }
}
