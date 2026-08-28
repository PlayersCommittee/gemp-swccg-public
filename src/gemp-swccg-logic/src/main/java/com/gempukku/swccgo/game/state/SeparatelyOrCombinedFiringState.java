package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks a "fire twice, separately or combined" action such as Targeting Computer (1_39).
 * This is NOT Maul-style repeated firing and is NOT Combined/Precise Attack.
 *
 * AR (Weapons - Firing Separately Or Combined): both shots are one overall action.
 * Combined: treat each firing as a single weapon destiny (draw modifiers only, even if
 * multiple draws), add them, then apply total weapon destiny modifiers once and resolve.
 *
 * Appendix B option A (coded here): Force is used for BOTH firings, but each shot's cost
 * is paid when THAT shot initiates (not all upfront). If the second shot cannot pay, skip
 * it; do not rewind the first shot and do not fail the overall action. Combined: destinies
 * from completed firings still combine (total-modifiers applied once) vs the single target.
 */
public class SeparatelyOrCombinedFiringState {
    private final PhysicalCard _device;
    private final PhysicalCard _weapon;
    private final boolean _combined;
    private final int _expectedFirings;
    private PhysicalCard _cardFiringWeapon;
    private PhysicalCard _combinedTarget;
    private final List<Float> _firingDestinies = new ArrayList<Float>();
    private boolean _resolved;

    public SeparatelyOrCombinedFiringState(PhysicalCard device, PhysicalCard weapon, boolean combined) {
        _device = device;
        _weapon = weapon;
        _combined = combined;
        _expectedFirings = 2;
    }

    public PhysicalCard getDevice() {
        return _device;
    }

    public PhysicalCard getWeapon() {
        return _weapon;
    }

    public boolean isCombined() {
        return _combined;
    }

    public void setCardFiringWeapon(PhysicalCard cardFiringWeapon) {
        if (cardFiringWeapon != null) {
            _cardFiringWeapon = cardFiringWeapon;
        }
    }

    public PhysicalCard getCardFiringWeapon() {
        return _cardFiringWeapon;
    }

    public void setCombinedTarget(PhysicalCard target) {
        if (_combinedTarget == null && target != null) {
            _combinedTarget = target;
        }
    }

    public PhysicalCard getCombinedTarget() {
        return _combinedTarget;
    }

    public void addFiringDestiny(float firingDestiny) {
        _firingDestinies.add(firingDestiny);
    }

    public int getCompletedFiringCount() {
        return _firingDestinies.size();
    }

    public boolean hasCompletedExpectedFirings() {
        return _firingDestinies.size() >= _expectedFirings;
    }

    public float getCombinedFiringDestinySum() {
        float total = 0f;
        for (Float value : _firingDestinies) {
            if (value != null) {
                total += value;
            }
        }
        return total;
    }

    public void markResolved() {
        _resolved = true;
    }

    public boolean isResolved() {
        return _resolved;
    }
}
