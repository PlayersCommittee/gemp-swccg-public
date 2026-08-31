package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks Premiere Combined Attack (1_75) while two or more starship weapons fire as one interrupt action.
 *
 * Each Combined Attack / Targeting Computer firing is its own complete total weapon destiny subtotal:
 * sum that firing's destiny draws (including each-draw mods), then apply that weapon's
 * TOTAL_WEAPON_DESTINY modifiers (Heavy Turbolaser Battery -1 vs capital / -6 otherwise).
 * Combined Attack adds those subtotals. Targeting Computer twice on one Heavy Turbolaser Battery
 * is two subtotals, so -1 is subtracted twice. Do not apply total modifiers again at resolve.
 * Results are applied only after all participating weapons have fired (Gergall / forum t=58557),
 * via each weapon's own destinyDraws path (hit, lost, ionize, etc.).
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
     * Record one firing's complete total weapon destiny subtotal (draws plus that firing's
     * total-weapon-destiny modifiers, already clamped at max(0, ...)). Snapshot Variable X
     * now, while WeaponFiringState is still active (Intruder Missile is placed in Used Pile
     * after firing; X is until-end-of-weapon-firing).
     */
    public void addFiring(PhysicalCard weapon, PhysicalCard cardFiringWeapon,
                          SwccgBuiltInCardBlueprint permanentWeapon, float drawModifiedDestiny,
                          float totalModifierSnapshot, float variableXSnapshot,
                          DrawDestinyEffect drawDestinyEffect) {
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
        record._variableXSnapshot = variableXSnapshot;
        record._cardFiringWeapon = cardFiringWeapon != null ? cardFiringWeapon : record._cardFiringWeapon;
        record._permanentWeapon = permanentWeapon != null ? permanentWeapon : record._permanentWeapon;
        if (drawDestinyEffect != null) {
            record._drawDestinyEffect = drawDestinyEffect;
        }
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
        for (WeaponRecord record : _recordsByWeaponId.values()) {
            if (!ordered.contains(record)) {
                ordered.add(record);
            }
        }
        return ordered;
    }

    public String getAddedDestiniesMessage(String drawSumFormatted) {
        return "Combined Attack destinies: " + formatAddends(_destinyDraws) + " = " + drawSumFormatted + ".";
    }

    public static String getPerWeaponTotalMessage(PhysicalCard weapon, float drawSum, float totalModifier, float total) {
        String weaponLink = GameUtils.getCardLink(weapon);
        if (totalModifier == 0f) {
            return "Combined Attack total for " + weaponLink + ": " + GuiUtils.formatAsString(total);
        }
        String sign = totalModifier > 0f ? "+" : "";
        return "Combined Attack total for " + weaponLink + ": " + GuiUtils.formatAsString(drawSum)
                + sign + GuiUtils.formatAsString(totalModifier) + "=" + GuiUtils.formatAsString(total);
    }

    public String getFiringSubtotalMessage(PhysicalCard weapon, float subtotal) {
        String weaponLink = weapon != null ? GameUtils.getCardLink(weapon) : "weapon";
        return "Combined Attack: " + weaponLink + " destiny " + GuiUtils.formatAsString(subtotal);
    }

    public static String formatAddends(List<Float> values) {
        if (values == null || values.isEmpty()) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(" + ");
            }
            Float value = values.get(i);
            sb.append(GuiUtils.formatAsString(value != null ? value : 0f));
        }
        return sb.toString();
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
        private float _variableXSnapshot;
        private DrawDestinyEffect _drawDestinyEffect;

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

        public float getVariableXSnapshot() {
            return _variableXSnapshot;
        }

        public DrawDestinyEffect getDrawDestinyEffect() {
            return _drawDestinyEffect;
        }
    }
}
