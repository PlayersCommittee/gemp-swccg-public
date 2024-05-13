package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to resets the power, forfeit, and landspeed of a card until end of the specified player's next turn.
 */
public class ResetPowerForfeitAndLandspeedUntilEndOfYourNextTurnEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private PhysicalCard _cardToReset;
    private float _resetValue;

    /**
     * Creates an effect that the power, forfeit, and landspeed of a card until end of the specified player's next turn
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardToReset the card whose attributes is reset
     * @param resetValue the reset value
     */
    public ResetPowerForfeitAndLandspeedUntilEndOfYourNextTurnEffect(Action action, String playerId, PhysicalCard cardToReset, float resetValue) {
        super(action);
        _playerId = playerId;
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

        // Check if card's landspeed may not be increased
        boolean resetLandspeed = true;
        float currentLandspeed = modifiersQuerying.getLandspeed(gameState, _cardToReset);
        if (_resetValue > currentLandspeed && modifiersQuerying.isProhibitedFromHavingLandspeedIncreased(gameState, _cardToReset)) {
            resetLandspeed = false;
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s landspeed is prevented from being increased");
        }

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        if (!resetPower && !resetForfeit && !resetLandspeed)
            return;

        StringBuilder sb = new StringBuilder(GameUtils.getCardLink(_cardToReset)).append("'s");
        int numValuesReset = 0;
        if (resetPower) {
            sb.append(" power");
            numValuesReset++;
        }
        if (resetPower && resetForfeit) {
            sb.append(",");
        }
        if (resetForfeit) {
            sb.append(" forfeit");
            numValuesReset++;
        }
        if (resetForfeit && resetLandspeed) {
            sb.append(",");
        }
        if (resetLandspeed) {
            if (resetPower || resetForfeit) {
                sb.append(" and");
            }
            sb.append(" landspeed");
            numValuesReset++;
        }
        sb.append(" ").append(GameUtils.be(numValuesReset)).append(" reset to ").append(GuiUtils.formatAsString(_resetValue));
        sb.append(" until end of ").append(_playerId).append("'s next turn");
        gameState.sendMessage(sb.toString());

        gameState.cardAffectsCard(performingPlayerId, source, _cardToReset);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToReset), Filters.in_play);

        if (resetPower) {
            modifiersEnvironment.addUntilEndOfPlayersNextTurnModifier(
                    new ResetPowerModifier(source, cardFilter, _resetValue), _playerId);
        }
        if (resetForfeit) {
            modifiersEnvironment.addUntilEndOfPlayersNextTurnModifier(
                    new ResetForfeitModifier(source, cardFilter, _resetValue), _playerId);
        }
        if (resetLandspeed) {
            modifiersEnvironment.addUntilEndOfPlayersNextTurnModifier(
                    new ResetLandspeedModifier(source, cardFilter, _resetValue), _playerId);
        }

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(performingPlayerId, _cardToReset));
    }
}
