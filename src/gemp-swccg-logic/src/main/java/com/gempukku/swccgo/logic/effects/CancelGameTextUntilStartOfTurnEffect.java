package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.CanceledGameTextResult;

/**
 * An effect to cancel the game text of a card until the end of the turn.
 */
public class CancelGameTextUntilStartOfTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _targetCard;
    private String _player;

    /**
     * Creates an effect that cancels the game text of a card until end of the turn.
     * @param action the action performing this effect
     * @param targetCard the card whose game text is canceled
     */
    public CancelGameTextUntilStartOfTurnEffect(Action action, PhysicalCard targetCard, String player) {
        super(action);
        _targetCard = targetCard;
        _player = player;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check if card's game text may not be canceled
        if (modifiersQuerying.isProhibitedFromHavingGameTextCanceled(gameState, _targetCard)) {
            gameState.sendMessage(GameUtils.getCardLink(_targetCard) + "'s game text is not allowed to be canceled");
            return;
        }

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getCardLink(_targetCard) + "'s game text is canceled until start of " + _player + "'s next turn");
        gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _targetCard);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_targetCard), Filters.in_play);

        _targetCard.setGameTextCanceled(true);
        modifiersEnvironment.addUntilStartOfPlayersNextTurnModifier(
                new CancelsGameTextModifier(source, cardFilter), _player);

        actionsEnvironment.emitEffectResult(new CanceledGameTextResult(_action.getPerformingPlayer(), _targetCard));
    }
}
