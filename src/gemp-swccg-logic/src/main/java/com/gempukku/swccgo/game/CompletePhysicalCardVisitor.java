package com.gempukku.swccgo.game;

// This abstract class is used to visit each card
// passed to it. It is extended by classes that
// look through all of the cards passed to it
// for various things.
//
public abstract class CompletePhysicalCardVisitor implements PhysicalCardVisitor {
    @Override
    public boolean visitPhysicalCard(PhysicalCard physicalCard) {
        doVisitPhysicalCard(physicalCard);
        return false;
    }

    protected abstract void doVisitPhysicalCard(PhysicalCard physicalCard);
}
