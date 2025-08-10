package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

public class RelocatedDeviceOrWeaponBetweenCharactersResult extends EffectResult {
    private PhysicalCard _cardMoved;
    private PhysicalCard _relocatedTo;

    /**
     * Creates an effect result that is emitted when cards are relocated between locations.
     * @param movedCard the weapon or device that moved
     * @param playerId the performing player
     * @param relocatedTo the character the weapon or device relocated to
     */
    public RelocatedDeviceOrWeaponBetweenCharactersResult(PhysicalCard movedCard, String playerId, PhysicalCard relocatedTo) {
        super(Type.RELOCATED_DEVICE_OR_WEAPON, playerId);
        _cardMoved = movedCard;
        _relocatedTo = relocatedTo;
    }

    /**
     * Gets the device or weapon that was transferred.
     * @return the device or weapon
     */
    public PhysicalCard getDeviceOrWeapon() {
        return _cardMoved;
    }

    /**
     * Gets the card the device or weapon was transferred to.
     * @return the card the device or weapon was transferred to
     */
    public PhysicalCard getTransferredTo() {
        return _relocatedTo;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_cardMoved) + " relocated to " + GameUtils.getCardLink(_relocatedTo);
    }

}
