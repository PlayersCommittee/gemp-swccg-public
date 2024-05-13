package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

public class ShuffleLostPileEffect extends ShufflePileEffect {

    public ShuffleLostPileEffect(Action action, PhysicalCard source) {
        this(action, source, source.getOwner());
    }

    public ShuffleLostPileEffect(Action action, PhysicalCard source, String playerId) {
        this(action, source, playerId, playerId);
    }

    public ShuffleLostPileEffect(Action action, PhysicalCard source, String playerId, String zoneOwner) {
        super(action, source, playerId, zoneOwner, Zone.LOST_PILE, false);
    }
}
