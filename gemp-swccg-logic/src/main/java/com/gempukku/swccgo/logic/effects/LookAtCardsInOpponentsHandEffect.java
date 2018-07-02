package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

public interface LookAtCardsInOpponentsHandEffect {

    Action getAction();

    PhysicalCard getCardAllowingScan();
}
