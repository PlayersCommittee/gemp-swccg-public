package com.gempukku.swccgo.ai.models.rando.strategy;

import com.gempukku.swccgo.game.PhysicalCard;
import java.util.List;
import java.util.ArrayList;

/**
 * Result of optimal card combination finding.
 *
 * Ported from Python _find_optimal_combination return value.
 */
public class OptimalCombination {
    /** Selected cards for this combination */
    public final List<PhysicalCard> cards;

    /** Total power of selected cards */
    public final int totalPower;

    /** Total deploy cost of selected cards */
    public final int totalCost;

    /** Total ability of selected cards */
    public final int totalAbility;

    /** Whether this combination can draw battle destiny (ability >= 4) */
    public final boolean hasAbility;

    /** Whether this combination achieves the power goal */
    public final boolean achievesGoal;

    public OptimalCombination(List<PhysicalCard> cards, int totalPower, int totalCost,
                              int totalAbility, boolean hasAbility, boolean achievesGoal) {
        this.cards = cards != null ? cards : new ArrayList<>();
        this.totalPower = totalPower;
        this.totalCost = totalCost;
        this.totalAbility = totalAbility;
        this.hasAbility = hasAbility;
        this.achievesGoal = achievesGoal;
    }

    public static OptimalCombination empty() {
        return new OptimalCombination(new ArrayList<>(), 0, 0, 0, false, false);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }
}
