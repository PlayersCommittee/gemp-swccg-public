package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that modifies power of cards accepted by the specified filter until the end of the player's next turn.
 */
public class ModifyPowerUntilEndOfPlayersNextTurnEffect extends AddUntilEndOfPlayersNextTurnModifierEffect {

    /**
     * Creates an effect that modifies power of cards accepted by the specified filter until the end of the player's next turn.
     * @param action the action
     * @param playerId the player
     * @param affectFilter the filter
     * @param modifierAmount the amount of power to modify
     * @param actionMsg the message to send about the modifier
     */
    public ModifyPowerUntilEndOfPlayersNextTurnEffect(Action action, String playerId, Filterable affectFilter, int modifierAmount, String actionMsg) {
        super(action, playerId, new PowerModifier(action.getActionSource(), affectFilter, modifierAmount), actionMsg);
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        String performingPlayerId = _action.getPerformingPlayer();
        PhysicalCard actionSourceCard = _action.getActionSource();
        PhysicalCard cardToModify = _modifier.getSource(gameState);
        float modifierAmount = _modifier.getPowerModifier(gameState, modifiersQuerying, cardToModify);

        // Check if card's power may not be increased
        if (actionSourceCard != null && modifierAmount > 0 && modifiersQuerying.isProhibitedFromHavingPowerIncreasedByCard(gameState, cardToModify, performingPlayerId, actionSourceCard)) {
            gameState.sendMessage(GameUtils.getCardLink(cardToModify) + "is prevented from having its power increased by " + GameUtils.getCardLink(actionSourceCard));
            return;
        }
        super.doPlayEffect(game);
    }
}
