package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

public class VehicleCrashedResult extends EffectResult {
    private PhysicalCard _vehicleCrashed;
    private PhysicalCard _crashedByCard;

    public VehicleCrashedResult(PhysicalCard vehicleCrashed, PhysicalCard crashedByCard) {
        super(Type.FOR_EACH_CRASHED, crashedByCard.getOwner());
        _vehicleCrashed = vehicleCrashed;
        _crashedByCard = crashedByCard;
    }

    public PhysicalCard getVehicleCrashed() {
        return _vehicleCrashed;
    }

    public PhysicalCard getCrashedByCard() {
        return _crashedByCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "'Crashed' " + GameUtils.getCardLink(_vehicleCrashed);
    }
}
