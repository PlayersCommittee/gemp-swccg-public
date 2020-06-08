package com.gempukku.swccgo.game;


/**
 * This interface is implemented by classes that will "visit" PhysicalCard objects using different criteria of which cards
 * need to be "visited".
 */
public interface PhysicalCardVisitor {

    /**
     * This method is called each time a card is visited.
     * @param physicalCard the card being visited
     * @return true if no more cards need to be visited, otherwise false
     */
    boolean visitPhysicalCard(PhysicalCard physicalCard);
}
