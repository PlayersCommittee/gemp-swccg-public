package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player to search opponent's Lost Pile for cards to steal and attach to a card.
 */
public class StealCardsAndAttachFromLostPileEffect extends StealCardsAndAttachFromPileEffect {

    /**
     * Creates an effect that causes the player to search opponent's Lost Pile for cards to steal and attach to a card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param attachTo the card to attach the stolen cards to
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     */
    public StealCardsAndAttachFromLostPileEffect(Action action, String playerId, PhysicalCard attachTo, int minimum, int maximum) {
        super(action, playerId, attachTo, minimum, maximum, Zone.LOST_PILE, false);
    }

    /**
     * Creates an effect that causes the player to search opponent's Lost Pile for cards accepted by the specified filter
     * to steal and attach to a card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param attachTo the card to attach the stolen cards to
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public StealCardsAndAttachFromLostPileEffect(Action action, String playerId, PhysicalCard attachTo, int minimum, int maximum, Filterable filters) {
        super(action, playerId, attachTo, minimum, maximum, Zone.LOST_PILE, false, filters, false);
    }
}
