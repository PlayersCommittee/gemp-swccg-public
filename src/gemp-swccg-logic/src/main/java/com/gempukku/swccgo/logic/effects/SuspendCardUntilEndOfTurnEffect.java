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
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.SuspendCardResult;

/**
 * An effect to suspend a card until the end of the turn.
 */
public class SuspendCardUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _targetCard;

    /**
     * Creates an effect that suspends a card until end of the turn.
     * @param action the action performing this effect
     * @param targetCard the card to be suspended
     */
    public SuspendCardUntilEndOfTurnEffect(Action action, PhysicalCard targetCard) {
        super(action);
        _targetCard = targetCard;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check if card may not be suspended
        if (modifiersQuerying.isProhibitedFromBeingSuspended(gameState, _targetCard)) {
            gameState.sendMessage(GameUtils.getCardLink(_targetCard) + " is not allowed to be suspended");
            return;
        }

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getCardLink(_targetCard) + " is suspended until end of the turn");
        gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _targetCard);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_targetCard), Filters.in_play);

        gameState.suspendCard(_targetCard);
        modifiersEnvironment.addUntilEndOfTurnModifier(
                new SuspendsCardModifier(source, cardFilter));

        actionsEnvironment.emitEffectResult(new SuspendCardResult(_action.getPerformingPlayer(), _targetCard));
    }
}
