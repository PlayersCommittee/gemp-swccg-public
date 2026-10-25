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
 * Gergall 2015 + 2023 Advanced Rulebook: fire weapons one at a time (pay then draw each). Hits are not
 * checked until all firings complete. Sum destinies (draw modifiers stay on each draw) and apply TOTAL
 * weapon destiny modifiers once to that grand total. Same title modifying the combined total counts once
 * unless the card says cumulatively. Apply that grand total separately to each non-canceled weapon.
 */
public class CombinedAttackFiringState {
    private final PhysicalCard _source;
    private final PhysicalCard _target;
    private final List<PhysicalCard> _weaponsInOrder;
    private final List<Float> _destinyDraws = new ArrayList<Float>();
    private final List<TotalModContribution> _totalMods = new ArrayList<TotalModContribution>();
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
     * Record one firing's destiny draws (including each-draw modifiers such as Targeting Computer -1
     * and Defiance +2). Total-weapon-destiny modifiers are snapshotted separately and applied once
     * to the grand total. Snapshot Variable X now, while WeaponFiringState is still active.
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

    /**
     * Snapshot one TOTAL_WEAPON_DESTINY contribution from this firing. Zero amounts are ignored.
     * Same title is collapsed once at resolve unless the modifier is cumulative.
     */
    public void addTotalModContribution(String title, float amount, boolean cumulative) {
        if (amount == 0f) {
            return;
        }
        _totalMods.add(new TotalModContribution(title, amount, cumulative));
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

    /**
     * TOTAL_WEAPON_DESTINY modifiers applied once to the Combined Attack grand total.
     * Multiple copies of the same title count once unless the modifier is cumulative.
     */
    public float getSameTitleOnceTotalModifier() {
        // TotalWeaponDestinyModifier(source, amount, targetFilter) passes cumulative=true by default,
        // because in a normal firing each copy only affects its own weapon. Combined Attack collects
        // those copies onto one grand total, so same title still counts once unless we later teach
        // this path about cards that actually say "cumulatively".
        Map<String, Float> onceByTitle = new LinkedHashMap<String, Float>();
        for (TotalModContribution contribution : _totalMods) {
            String title = contribution._title != null ? contribution._title : "";
            if (!onceByTitle.containsKey(title)) {
                onceByTitle.put(title, contribution._amount);
            }
        }
        float total = 0f;
        for (Float value : onceByTitle.values()) {
            if (value != null) {
                total += value;
            }
        }
        return total;
    }

    public float getGrandTotal() {
        return Math.max(0, getDrawSum() + getSameTitleOnceTotalModifier());
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

    public String getAddedDestiniesMessage(String drawSumFormatted, String totalModsFormatted, String grandTotalFormatted) {
        float totalMods = getSameTitleOnceTotalModifier();
        String mods = "";
        if (totalMods != 0f) {
            String sign = totalMods > 0f ? "+" : "";
            mods = ". Total modifiers " + sign + totalModsFormatted;
        }
        return "Combined Attack destinies: " + formatAddends(_destinyDraws) + " = " + drawSumFormatted
                + mods + ". Total weapon destiny " + grandTotalFormatted + ".";
    }

    public String getAddedDestiniesMessage(String drawSumFormatted) {
        return getAddedDestiniesMessage(drawSumFormatted,
                GuiUtils.formatAsString(getSameTitleOnceTotalModifier()),
                GuiUtils.formatAsString(getGrandTotal()));
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

    public String getFiringDrawsMessage(PhysicalCard weapon, float drawDestiny) {
        String weaponLink = weapon != null ? GameUtils.getCardLink(weapon) : "weapon";
        return "Combined Attack: " + weaponLink + " destiny " + GuiUtils.formatAsString(drawDestiny)
                + " (draw modifiers only)";
    }

    public String getFiringSubtotalMessage(PhysicalCard weapon, float subtotal) {
        return getFiringDrawsMessage(weapon, subtotal);
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

    public static class TotalModContribution {
        private final String _title;
        private final float _amount;
        private final boolean _cumulative;

        private TotalModContribution(String title, float amount, boolean cumulative) {
            _title = title;
            _amount = amount;
            _cumulative = cumulative;
        }
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
