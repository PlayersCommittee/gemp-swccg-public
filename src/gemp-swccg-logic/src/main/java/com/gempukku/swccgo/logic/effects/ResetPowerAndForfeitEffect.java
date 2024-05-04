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
 * An effect to resets the power and forfeit of a card.
 */
public class ResetPowerAndForfeitEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToReset;
    private float _resetValue;

    /**
     * Creates an effect that resets the power and forfeit of a card.
     * @param action the action performing this effect
     * @param cardToReset the card whose power is reset
     * @param resetValue the reset value
     */
    public ResetPowerAndForfeitEffect(Action action, PhysicalCard cardToReset, float resetValue) {
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
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s forfeit is reset to " + GuiUtils.formatAsString(_resetValue));
        }
        else if (!resetForfeit) {
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s power is reset to " + GuiUtils.formatAsString(_resetValue));
        }
        else {
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s power and forfeit are reset to " + GuiUtils.formatAsString(_resetValue));
        }

        gameState.cardAffectsCard(performingPlayerId, source, _cardToReset);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToReset), Filters.in_play);

        Modifier powerModifier = new ResetPowerModifier(source, cardFilter, _resetValue);
        powerModifier.skipSettingNotRemovedOnRestoreToNormal();
        Modifier forfeitModifier = new ResetForfeitModifier(source, cardFilter, _resetValue);
        forfeitModifier.skipSettingNotRemovedOnRestoreToNormal();

        // If during battle and the source if the action is not a weapon, then reset until end of the battle, otherwise
        // lasts for remainder of game (until card leaves play).
        if (gameState.isDuringBattle()
                && !Filters.weapon_or_character_with_permanent_weapon.accepts(gameState, modifiersQuerying, source)) {
            if (resetPower) {
                modifiersEnvironment.addUntilEndOfBattleModifier(powerModifier);
            }
            if (resetForfeit) {
                modifiersEnvironment.addUntilEndOfBattleModifier(forfeitModifier);
            }
        }
        else {
            if (resetPower) {
                modifiersEnvironment.addUntilEndOfGameModifier(powerModifier);
            }
            if (resetForfeit) {
                modifiersEnvironment.addUntilEndOfGameModifier(forfeitModifier);
            }
        }

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(performingPlayerId, _cardToReset));
    }
}
