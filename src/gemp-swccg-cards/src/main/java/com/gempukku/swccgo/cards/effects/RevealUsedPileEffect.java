package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

public class RevealUsedPileEffect extends RevealCardPileEffect {

    public RevealUsedPileEffect(Action action, String zoneOwner) {
        super(action, action.getPerformingPlayer(), zoneOwner, Zone.USED_PILE);
    }
}
