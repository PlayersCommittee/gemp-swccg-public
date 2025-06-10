package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to resets the power and hyperspeed of a card.
 */
public class ResetPowerAndHyperspeedEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToReset;
    private float _resetValue;

    /**
     * Creates an effect that resets the power and hyperspeed of a card.
     * @param action the action performing this effect
     * @param cardToReset the card whose attributes is reset
     * @param resetValue the reset value
     */
    public ResetPowerAndHyperspeedEffect(Action action, PhysicalCard cardToReset, float resetValue) {
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

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        if (!resetPower) {
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s hyperspeed is reset to " + GuiUtils.formatAsString(_resetValue));
        }
        else {
            gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s power and hyperspeed are reset to " + GuiUtils.formatAsString(_resetValue));
        }

        gameState.cardAffectsCard(performingPlayerId, source, _cardToReset);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToReset), Filters.in_play);

        Modifier powerModifier = new ResetPowerModifier(source, cardFilter, _resetValue);
        powerModifier.skipSettingNotRemovedOnRestoreToNormal();
        Modifier hyperspeedModifier = new ResetHyperspeedModifier(source, cardFilter, _resetValue);
        hyperspeedModifier.skipSettingNotRemovedOnRestoreToNormal();

        // If during battle and the source if the action is not a weapon, then reset until end of the battle, otherwise
        // lasts for remainder of game (until card leaves play).
        if (gameState.isDuringBattle()
                && !Filters.weapon_or_character_with_permanent_weapon.accepts(gameState, modifiersQuerying, source)) {
            if (resetPower) {
                modifiersEnvironment.addUntilEndOfBattleModifier(powerModifier);
            }
            modifiersEnvironment.addUntilEndOfBattleModifier(hyperspeedModifier);
        }
        else {
            if (resetPower) {
                modifiersEnvironment.addUntilEndOfGameModifier(powerModifier);
            }
            modifiersEnvironment.addUntilEndOfGameModifier(hyperspeedModifier);
        }

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(performingPlayerId, _cardToReset));
    }
}
