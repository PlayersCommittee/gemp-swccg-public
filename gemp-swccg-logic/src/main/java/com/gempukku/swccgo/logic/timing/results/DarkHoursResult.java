package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.game.PhysicalCard;

public class DarkHoursResult extends EffectResult {

    public DarkHoursResult(PhysicalCard card) {
        super(Type.DARK_HOURS_EFFECT, card.getOwner());
    }
}
