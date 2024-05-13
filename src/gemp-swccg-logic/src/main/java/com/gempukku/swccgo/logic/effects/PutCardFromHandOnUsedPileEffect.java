package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect to put a card from hand on Used Pile.
 */
public class PutCardFromHandOnUsedPileEffect extends PutCardsFromHandOnUsedPileEffect {

    /**
     * Creates an effect that causes the player to put a card from hand on Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public PutCardFromHandOnUsedPileEffect(Action action, String playerId) {
       super(action, playerId, 1, 1);
    }

    /**
     * Creates an effect that causes the player to put a card accepted by the specified filter from hand on Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardFromHandOnUsedPileEffect(Action action, String playerId, Filterable filters, boolean hidden) {
        super(action, playerId, 1, 1, filters, hidden);
    }

    /**
     * Creates an effect that causes the player to put a card accepted by the specified filter from hand on specified
     * player's Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardFromHandOnUsedPileEffect(Action action, String playerId, String cardPileOwner, Filterable filters, boolean hidden) {
        super(action, playerId, cardPileOwner, 1, 1, filters, hidden);
    }

    /**
     * Creates an effect that causes the player to put a specified card from hand on specified player's Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param card the card
     */
    public PutCardFromHandOnUsedPileEffect(Action action, String playerId, String cardPileOwner, PhysicalCard card) {
        super(action, playerId, cardPileOwner, Collections.singletonList(card), false);
    }

    @Override
    public String getText(SwccgGame game) {
        return "Put card from hand on Used Pile";
    }
}
