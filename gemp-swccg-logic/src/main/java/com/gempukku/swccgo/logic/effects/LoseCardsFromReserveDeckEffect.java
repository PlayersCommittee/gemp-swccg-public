package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;


public class LoseCardsFromReserveDeckEffect extends AbstractStandardEffect {
    private String _performingPlayer;
    private String _zoneOwner;
    private Collection<PhysicalCard> _cards;
    private Filterable _filters;

    public LoseCardsFromReserveDeckEffect(Action action, Collection<PhysicalCard> cards) {
        this(action, action.getPerformingPlayer(), action.getPerformingPlayer(), cards);
    }

    public LoseCardsFromReserveDeckEffect(Action action, String performingPlayer, String zoneOwner, Collection<PhysicalCard> cards) {
        super(action);
        _performingPlayer = performingPlayer;
        _zoneOwner = zoneOwner;
        _cards = cards;
    }

    public LoseCardsFromReserveDeckEffect(Action action, String performingPlayer, String zoneOwner, Filterable filters) {
        super(action);
        _performingPlayer = performingPlayer;
        _zoneOwner = zoneOwner;
        _filters = filters;
    }


    @Override
    public Type getType() {
        return null;
    }

    @Override
    public String getText(SwccgGame game) {
        return null;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        GameState gameState = game.getGameState();
        Collection<PhysicalCard> cardsToLose;
        if (_cards != null)
            cardsToLose = Filters.filter(_cards, game, Filters.zoneOfPlayer(Zone.RESERVE_DECK, _zoneOwner));
        else
            cardsToLose = Filters.filter(gameState.getReserveDeck(_zoneOwner), game, _filters);

        if (cardsToLose.isEmpty())
            return new FullEffectResult(false);

        if (_performingPlayer!=null)
            game.getGameState().sendMessage(_performingPlayer + " causes " + GameUtils.getAppendedNames(cardsToLose) + " be lost from " + _zoneOwner + "'s Reserve Deck");
        else
            game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(cardsToLose) + " be lost from " + _zoneOwner + "'s Reserve Deck");

        // Choose order that cards are placed in Lost Pile
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(new LoseCardsFromOffTableSimultaneouslyEffect(subAction, cardsToLose, false));
        game.getActionsEnvironment().addActionToStack(subAction);

        return new FullEffectResult(true);
    }
}
