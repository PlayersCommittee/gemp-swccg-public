package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;

public interface MovingAsReactEffect extends Effect {

    Collection<PhysicalCard> getCardsMoving();

    PhysicalCard getMovingFrom();
}
