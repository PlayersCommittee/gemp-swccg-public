package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes cards to be lost from hand.
 */
public class LoseCardsFromHandEffect extends AbstractStandardEffect {
    private String _performingPlayer;
    private String _zoneOwner;
    private Filterable _filters;
    private Collection<PhysicalCard> _cards;

    /**
     * Creates an effect that causes cards accepted by the specified filter to be lost from the specified player's hand.
     * @param action the action performing this effect
     * @param zoneOwner the player
     * @param filters the filter
     */
    public LoseCardsFromHandEffect(Action action, String zoneOwner, Filterable filters) {
        super(action);
        _performingPlayer = action.getPerformingPlayer();
        _zoneOwner = zoneOwner;
        _filters = filters;
    }

    /**
     * Creates an effect that causes the specified cards to be lost from the specified player's hand.
     * @param action the action performing this effect
     * @param zoneOwner the player
     * @param cards the cards
     */
    public LoseCardsFromHandEffect(Action action, String zoneOwner, Collection<PhysicalCard> cards) {
        super(action);
        _performingPlayer = action.getPerformingPlayer();
        _zoneOwner = zoneOwner;
        _cards = cards;
    }

    @Override
    public final boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        String opponent = game.getOpponent(_zoneOwner);
        String actionSourceOwner = _action.getActionSource() != null ? _action.getActionSource().getOwner() : null;
        if ((opponent.equals(_performingPlayer) || opponent.equals(actionSourceOwner)) && modifiersQuerying.mayNotRemoveCardsFromOpponentsHand(gameState, _action.getActionSource(), opponent)) {
            gameState.sendMessage(opponent + " is not allowed to remove cards from " + _zoneOwner + "'s hand");
            return new FullEffectResult(false);
        }

        // Determine the cards to lose
        Collection<PhysicalCard> cardsToLose;
        if (_cards != null)
            cardsToLose = Filters.filter(_cards, game, Filters.inHand(_zoneOwner));
        else
            cardsToLose = Filters.filter(gameState.getHand(_zoneOwner), game, _filters);

        if (cardsToLose.isEmpty()) {
            return new FullEffectResult(false);
        }

        game.getGameState().sendMessage(_performingPlayer + " causes " + GameUtils.getAppendedNames(cardsToLose) + " be lost from " + _zoneOwner + "'s hand");

        // Choose order that cards are placed in Lost Pile
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(new LoseCardsFromOffTableSimultaneouslyEffect(subAction, cardsToLose, false));
        game.getActionsEnvironment().addActionToStack(subAction);

        return new FullEffectResult(true);
    }
}
