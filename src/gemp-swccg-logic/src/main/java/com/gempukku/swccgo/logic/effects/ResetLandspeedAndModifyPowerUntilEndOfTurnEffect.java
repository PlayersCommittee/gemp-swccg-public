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
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetLandspeedModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to resets the landspeed and modifies the power of a card until end of the turn.
 */
public class ResetLandspeedAndModifyPowerUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private float _landspeedResetValue;
    private float _powerModifierAmount;

    /**
     * Creates an effect that resets the landspeed and modifies the power of a card until end of the turn.
     * @param action the action performing this effect
     * @param card the card whose landspeed is reset and power modified
     * @param landspeedResetValue the reset value
     * @param powerModifierAmount the amount to modify
     */
    public ResetLandspeedAndModifyPowerUntilEndOfTurnEffect(Action action, PhysicalCard card, float landspeedResetValue, float powerModifierAmount) {
        super(action);
        _card = card;
        _landspeedResetValue = landspeedResetValue;
        _powerModifierAmount = powerModifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        String performingPlayerId = _action.getPerformingPlayer();

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        // Check if card's landspeed may not be increased
        boolean resetLandspeed = true;
        float currentLandspeed = modifiersQuerying.getLandspeed(gameState, _card);
        if (_landspeedResetValue > currentLandspeed && modifiersQuerying.isProhibitedFromHavingLandspeedIncreased(gameState, _card)) {
            resetLandspeed = false;
            gameState.sendMessage(GameUtils.getCardLink(_card) + "'s landspeed is prevented from being increased");
        }

        // Check if card's power may not be reduced
        boolean modifyPower = true;
        if (_powerModifierAmount < 0 && modifiersQuerying.isProhibitedFromHavingPowerReduced(gameState, _card, performingPlayerId)) {
            modifyPower = false;
            gameState.sendMessage(GameUtils.getCardLink(_card) + "'s power is prevented from being reduced");
        }

        if (!resetLandspeed && !modifyPower)
            return;

        StringBuilder sb = new StringBuilder(GameUtils.getCardLink(_card)).append("'s");
        if (resetLandspeed) {
            sb.append(" landspeed is reset to ").append(GuiUtils.formatAsString(_landspeedResetValue));
        }
        if (resetLandspeed && modifyPower) {
            sb.append(" and its");
        }
        if (modifyPower) {
            if (_powerModifierAmount < 0) {
                sb.append(" power is reduced by ").append(GuiUtils.formatAsString(-_powerModifierAmount));
            }
            else {
                sb.append(" power is increased by ").append(GuiUtils.formatAsString(_powerModifierAmount));
            }
        }
        sb.append(" until end of the turn");
        gameState.sendMessage(sb.toString());

        gameState.cardAffectsCard(performingPlayerId, source, _card);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_card), Filters.in_play);

        if (resetLandspeed) {
            modifiersEnvironment.addUntilEndOfTurnModifier(
                    new ResetLandspeedModifier(source, cardFilter, _landspeedResetValue));
        }
        if (modifyPower) {
            modifiersEnvironment.addUntilEndOfTurnModifier(
                    new PowerModifier(source, cardFilter, _powerModifierAmount));
        }

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(performingPlayerId, _card));
    }
}
