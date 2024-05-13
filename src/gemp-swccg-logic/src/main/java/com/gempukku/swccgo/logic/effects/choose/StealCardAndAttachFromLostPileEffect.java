package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player to search opponent's Lost Pile for a card and steal and attach to a card.
 */
public class StealCardAndAttachFromLostPileEffect extends StealCardsAndAttachFromLostPileEffect {

    /**
     * Creates an effect that causes the player to search opponent's Lost Pile for a card and steal and attach to a card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param attachTo the card to attach the stolen cards to
     */
    public StealCardAndAttachFromLostPileEffect(Action action, String playerId, PhysicalCard attachTo) {
        super(action, playerId, attachTo, 1, 1);
    }

    /**
     * Creates an effect that causes the player to search opponent's Lost Pile for a card accepted by the specified filter
     * and steal and attach to a card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param attachTo the card to attach the stolen cards to
     * @param filters the filter
     */
    public StealCardAndAttachFromLostPileEffect(Action action, String playerId, PhysicalCard attachTo, Filter filters) {
        super(action, playerId, attachTo, 1, 1, filters);
    }
}
