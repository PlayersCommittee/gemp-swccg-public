package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks Premiere Combined Attack (1_75) while two or more starship weapons fire as one interrupt action.
 *
 * AR 2023 Appendix A: add all weapon destiny DRAWS together, then apply that shared draw-sum separately
 * for each participating weapon (each weapon brings its own total-weapon-destiny modifiers).
 * Results (hit / ionize) are applied only after all participating weapons have fired (Gergall / forum t=58557).
 *
 * This is NOT Targeting Computer fire-twice. When Targeting Computer is used inside Combined Attack,
 * those extra destinies are additional draws in this pool; the TC weapon is still one weapon when applying.
 */
public class CombinedAttackFiringState {
    private final PhysicalCard _source;
    private final PhysicalCard _target;
    private final List<PhysicalCard> _weaponsInOrder;
    private final List<Float> _destinyDraws = new ArrayList<Float>();
    private final Map<Integer, WeaponRecord> _recordsByWeaponId = new LinkedHashMap<Integer, WeaponRecord>();
    private boolean _resolved;

    public CombinedAttackFiringState(PhysicalCard source, PhysicalCard target, List<PhysicalCard> weaponsInOrder) {
        _source = source;
        _target = target;
        _weaponsInOrder = new ArrayList<PhysicalCard>(weaponsInOrder);
    }

    public PhysicalCard getSource() {
        return _source;
    }

    public PhysicalCard getTarget() {
        return _target;
    }

    public List<PhysicalCard> getWeaponsInOrder() {
        return _weaponsInOrder;
    }

    /**
     * Record one weapon-destiny firing (draw modifiers only). Snapshot that weapon's total-modifier
     * contribution now, while the weapon is still in play / WeaponFiringState is active (Intruder Missile
     * is placed in Used Pile after firing, which would otherwise drop its +3).
     */
    public void addFiring(PhysicalCard weapon, PhysicalCard cardFiringWeapon,
                          SwccgBuiltInCardBlueprint permanentWeapon, float drawModifiedDestiny,
                          float totalModifierSnapshot) {
        if (weapon == null) {
            return;
        }
        _destinyDraws.add(drawModifiedDestiny);
        int id = weapon.getCardId();
        WeaponRecord record = _recordsByWeaponId.get(id);
        if (record == null) {
            record = new WeaponRecord(weapon, cardFiringWeapon, permanentWeapon);
            _recordsByWeaponId.put(id, record);
        }
        record._drawCount++;
        record._totalModifierSnapshot = totalModifierSnapshot;
        record._cardFiringWeapon = cardFiringWeapon != null ? cardFiringWeapon : record._cardFiringWeapon;
        record._permanentWeapon = permanentWeapon != null ? permanentWeapon : record._permanentWeapon;
    }

    public int getCompletedDrawCount() {
        return _destinyDraws.size();
    }

    public float getDrawSum() {
        float total = 0f;
        for (Float value : _destinyDraws) {
            if (value != null) {
                total += value;
            }
        }
        return total;
    }

    public List<WeaponRecord> getCompletedWeaponRecordsInOrder() {
        List<WeaponRecord> ordered = new ArrayList<WeaponRecord>();
        for (PhysicalCard weapon : _weaponsInOrder) {
            WeaponRecord record = _recordsByWeaponId.get(weapon.getCardId());
            if (record != null) {
                ordered.add(record);
            }
        }
        // Include any completed weapon that was not in the original list (should not happen).
        for (WeaponRecord record : _recordsByWeaponId.values()) {
            if (!ordered.contains(record)) {
                ordered.add(record);
            }
        }
        return ordered;
    }

    public String getAddedDestiniesMessage(String drawSumFormatted) {
        return "Combined Attack destinies added together: " + drawSumFormatted
                + " (" + _destinyDraws.size() + " weapon destin" + (_destinyDraws.size() == 1 ? "y" : "ies") + ")";
    }

    public String getPerWeaponTotalMessage(PhysicalCard weapon, String totalFormatted) {
        return "Combined Attack total for " + GameUtils.getCardLink(weapon) + ": " + totalFormatted;
    }

    public void markResolved() {
        _resolved = true;
    }

    public boolean isResolved() {
        return _resolved;
    }

    public static class WeaponRecord {
        private final PhysicalCard _weapon;
        private PhysicalCard _cardFiringWeapon;
        private SwccgBuiltInCardBlueprint _permanentWeapon;
        private int _drawCount;
        private float _totalModifierSnapshot;

        private WeaponRecord(PhysicalCard weapon, PhysicalCard cardFiringWeapon, SwccgBuiltInCardBlueprint permanentWeapon) {
            _weapon = weapon;
            _cardFiringWeapon = cardFiringWeapon;
            _permanentWeapon = permanentWeapon;
        }

        public PhysicalCard getWeapon() {
            return _weapon;
        }

        public PhysicalCard getCardFiringWeapon() {
            return _cardFiringWeapon;
        }

        public SwccgBuiltInCardBlueprint getPermanentWeapon() {
            return _permanentWeapon;
        }

        public int getDrawCount() {
            return _drawCount;
        }

        public float getTotalModifierSnapshot() {
            return _totalModifierSnapshot;
        }
    }
}
