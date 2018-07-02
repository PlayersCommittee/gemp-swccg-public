package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

public class ShuffleForcePileEffect extends ShufflePileEffect {

    public ShuffleForcePileEffect(Action action, PhysicalCard source) {
        this(action, source, source.getOwner());
    }

    public ShuffleForcePileEffect(Action action, PhysicalCard source, String playerId) {
        this(action, source, playerId, playerId);
    }

    public ShuffleForcePileEffect(Action action, PhysicalCard source, String playerId, String zoneOwner) {
        super(action, source, playerId, zoneOwner, Zone.FORCE_PILE, false);
    }
}
