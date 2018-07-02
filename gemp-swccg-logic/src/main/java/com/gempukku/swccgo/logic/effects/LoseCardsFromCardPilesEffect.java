package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;


/**
 * An effect that causes cards to be lost from the card piles.
 */
public class LoseCardsFromCardPilesEffect extends AbstractSuccessfulEffect {
    private String _performingPlayer;
    private String _cardPileOwner;
    private Collection<PhysicalCard> _cardsToLose;

    /**
     * Creates an effect that causes cards to be lost from card piles.
     * @param action action
     * @param cardPileOwner the card pile owner
     * @param cardsToLose the cards to lose
     */
    public LoseCardsFromCardPilesEffect(Action action, String cardPileOwner, Collection<PhysicalCard> cardsToLose) {
        super(action);
        _performingPlayer = _action.getPerformingPlayer();
        _cardPileOwner = cardPileOwner;
        _cardsToLose = cardsToLose;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        Collection<PhysicalCard> cardsToLoseFromReserveDeck = Filters.filter(_cardsToLose, game, Filters.zoneOfPlayer(Zone.RESERVE_DECK, _cardPileOwner));
        Collection<PhysicalCard> cardsToLoseFromForcePile = Filters.filter(_cardsToLose, game, Filters.zoneOfPlayer(Zone.FORCE_PILE, _cardPileOwner));
        Collection<PhysicalCard> cardsToLoseFromUsedPile = Filters.filter(_cardsToLose, game, Filters.zoneOfPlayer(Zone.USED_PILE, _cardPileOwner));
        if (cardsToLoseFromReserveDeck.isEmpty() && cardsToLoseFromForcePile.isEmpty() && cardsToLoseFromUsedPile.isEmpty())
            return;

        StringBuilder msg = new StringBuilder();
        if (!cardsToLoseFromReserveDeck.isEmpty()) {
            msg.append(_performingPlayer).append(" causes ").append(GameUtils.getAppendedNames(cardsToLoseFromReserveDeck)).append(" be lost from ").append(_cardPileOwner).append("'s Reserve Deck");
        }
        if (!cardsToLoseFromForcePile.isEmpty()) {
            if (msg.length() == 0) {
                msg.append(_performingPlayer).append(" causes ").append(GameUtils.getAppendedNames(cardsToLoseFromForcePile)).append(" be lost from ").append(_cardPileOwner).append("'s Force Pile");
            }
            else {
                if (cardsToLoseFromUsedPile.isEmpty())
                    msg.append(" and ").append(GameUtils.getAppendedNames(cardsToLoseFromForcePile)).append(" be lost from ").append(_cardPileOwner).append("'s Force Pile");
                else
                    msg.append(", ").append(GameUtils.getAppendedNames(cardsToLoseFromForcePile)).append(" be lost from ").append(_cardPileOwner).append("'s Force Pile,");
            }
        }
        if (!cardsToLoseFromUsedPile.isEmpty()) {
            if (msg.length() == 0)
                msg.append(_performingPlayer).append(" causes ").append(GameUtils.getAppendedNames(cardsToLoseFromUsedPile)).append(" be lost from ").append(_cardPileOwner).append("'s Used Pile");
            else
                msg.append(" and ").append(GameUtils.getAppendedNames(cardsToLoseFromUsedPile)).append(" be lost from ").append(_cardPileOwner).append("'s Used Pile");
        }
        gameState.sendMessage(msg.toString());

        Collection<PhysicalCard> cardsToLose = new LinkedList<PhysicalCard>();
        cardsToLose.addAll(cardsToLoseFromReserveDeck);
        cardsToLose.addAll(cardsToLoseFromForcePile);
        cardsToLose.addAll(cardsToLoseFromUsedPile);

        // Choose order that cards are placed in Lost Pile
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(new LoseCardsFromOffTableSimultaneouslyEffect(subAction, cardsToLose, false));
        game.getActionsEnvironment().addActionToStack(subAction);
    }
}
