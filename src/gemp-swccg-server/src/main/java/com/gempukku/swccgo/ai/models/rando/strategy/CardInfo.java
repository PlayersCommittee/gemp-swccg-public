package com.gempukku.swccgo.ai.models.rando.strategy;

import com.gempukku.swccgo.ai.common.AiCardHelper;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;

/**
 * Simplified card information for planning calculations.
 *
 * Ported from Python card dict structure used in deploy_planner.py.
 */
public class CardInfo {
    public final PhysicalCard card;
    public final String blueprintId;
    public final String name;
    public final int power;
    public final int cost;
    public final int ability;
    public final boolean isCharacter;
    public final boolean isStarship;
    public final boolean isVehicle;
    public final boolean isPilot;
    public final boolean isLocation;

    public CardInfo(PhysicalCard card) {
        this.card = card;
        SwccgCardBlueprint bp = card.getBlueprint();

        this.blueprintId = card.getBlueprintId(true);
        this.name = card.getTitle();

        // Get power
        int powerVal = 0;
        if (bp != null && bp.hasPowerAttribute()) {
            Float p = bp.getPower();
            powerVal = p != null ? p.intValue() : 0;
        }
        this.power = powerVal;

        // Get deploy cost
        int costVal = 0;
        if (bp != null) {
            try {
                Float c = bp.getDeployCost();
                costVal = c != null ? c.intValue() : 0;
            } catch (UnsupportedOperationException e) {
                // No deploy cost
            }
        }
        this.cost = costVal;

        // Get ability
        int abilityVal = 0;
        if (bp != null && bp.hasAbilityAttribute()) {
            Float a = bp.getAbility();
            abilityVal = a != null ? a.intValue() : 0;
        }
        this.ability = abilityVal;

        // Get category flags
        CardCategory cat = bp != null ? bp.getCardCategory() : null;
        this.isCharacter = cat == CardCategory.CHARACTER;
        this.isStarship = cat == CardCategory.STARSHIP;
        this.isVehicle = cat == CardCategory.VEHICLE;
        this.isLocation = cat == CardCategory.LOCATION;

        // Check if pilot (characters with pilot ability)
        // Use AiCardHelper.isPilot() which checks the card properly
        this.isPilot = isCharacter && AiCardHelper.isPilot(card);
    }

    /**
     * Calculate value ratio (power + ability) / cost.
     */
    public float getValueRatio() {
        int value = power + ability;
        return cost > 0 ? (float) value / cost : value;
    }

    /**
     * Check if card can draw battle destiny (ability >= 4).
     */
    public boolean canDrawDestiny() {
        return ability >= 4;
    }

    @Override
    public String toString() {
        return String.format("%s (P:%d, C:%d, A:%d)", name, power, cost, ability);
    }
}
