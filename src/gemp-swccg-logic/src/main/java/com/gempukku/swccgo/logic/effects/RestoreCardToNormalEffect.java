package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.RestoredToNormalResult;

import java.util.List;


/**
 * An effect to restore a card to normal (remove 'hit', etc.).
 */
public class RestoreCardToNormalEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToRestore;
    private boolean _displayMessage;

    /**
     * Creates an effect that restores a card to normal (remove 'hit', etc.).
     * @param action the action performing this effect
     * @param cardToRestore the card to restore
     */
    public RestoreCardToNormalEffect(Action action, PhysicalCard cardToRestore) {
        this(action, cardToRestore, true);
    }

    /**
     * Creates an effect that restores a card to normal (remove 'hit', etc.).
     * @param action the action performing this effect
     * @param cardToRestore the card to restore
     */
    public RestoreCardToNormalEffect(Action action, PhysicalCard cardToRestore, boolean displayMessage) {
        super(action);
        _cardToRestore = cardToRestore;
        _displayMessage = displayMessage;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (!Filters.onTable.accepts(game, _cardToRestore)) {
            return;
        }

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        if (_displayMessage) {
            gameState.sendMessage(GameUtils.getCardLink(_cardToRestore) + " is restored to normal by " + GameUtils.getCardLink(_action.getActionSource()));
            gameState.cardAffectsCard(_action.getActionSource().getOwner(), _action.getActionSource(), _cardToRestore);
        }

        // Set card as no longer 'hit' or 'crashed'
        _cardToRestore.setCrashed(false);
        _cardToRestore.setHit(false);
        if (_cardToRestore.isSideways()) {
            gameState.turnCardSideways(game, _cardToRestore, true);
        }
        modifiersQuerying.clearHitOrMadeLostByWeapon(_cardToRestore);

        // Get all the persistent modifiers that can affect the card and exclude this card from those modifiers.
        List<Modifier> modifiers = modifiersQuerying.getPersistentModifiersAffectingCard(gameState, _cardToRestore);
        for (Modifier modifier : modifiers) {
            if (!modifier.isNotRemovedOnRestoreToNormal()) {
                modifiersQuerying.excludeFromBeingAffected(modifier, _cardToRestore);
            }
        }

        game.getActionsEnvironment().emitEffectResult(new RestoredToNormalResult(_action.getPerformingPlayer(), _cardToRestore, _action.getActionSource()));
    }
}
