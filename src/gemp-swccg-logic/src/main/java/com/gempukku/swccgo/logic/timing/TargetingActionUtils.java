package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

import java.util.*;


/**
 * Utility methods for querying or updating targeting data for an action.
 */
public class TargetingActionUtils {

    /**
     * Determines if a card accepted by the target filter is being targeted by the specified action.
     * @param game the game
     * @param action the action
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean isTargeting(SwccgGame game, Action action, Filter targetFilter) {
        // Check primary targets
        Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> targetingInfo = action.getAllPrimaryTargetCards();
        for (Map<PhysicalCard, Set<TargetingReason>> targetingGroup : targetingInfo.values()) {
            Set<PhysicalCard> targetedCards = targetingGroup.keySet();
            if (Filters.canSpot(targetedCards, game, targetFilter)) {
                return true;
            }
        }
        // Check secondary targets
        List<PhysicalCard> secondaryTargets = action.getAllSecondaryTargetCards(game);
        if (Filters.canSpot(secondaryTargets, game, targetFilter)) {
            return true;
        }
        return false;
    }

    /**
     * Determines if a card accepted by the target filter is being targeted by the specified action for the specified reason.
     * @param game the game
     * @param action the action
     * @param targetingReason the reason to target
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean isTargeting(SwccgGame game, Action action, TargetingReason targetingReason, Filter targetFilter) {
        return isTargeting(game, action, Collections.singleton(targetingReason), targetFilter);
    }

    /**
     * Determines if a card accepted by the target filter is being targeted by the specified action for any of the specified reasons.
     * @param game the game
     * @param action the action
     * @param targetingReasons the reasons to target
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean isTargeting(SwccgGame game, Action action, Collection<TargetingReason> targetingReasons, Filter targetFilter) {
        // Check primary targets
        Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> targetingInfo = action.getAllPrimaryTargetCards();
        for (Map<PhysicalCard, Set<TargetingReason>> targetingGroup : targetingInfo.values()) {
            for (PhysicalCard targetedCard : targetingGroup.keySet()) {
                for (TargetingReason targetingReason : targetingReasons) {
                    if (targetingGroup.get(targetedCard).contains(targetingReason)) {
                        if (Filters.and(targetFilter).accepts(game, targetedCard)) {
                            return true;
                        }
                    }
                }
            }
        }
        // Skip checking secondary targets, since they are not targeted for any specific reason
        return false;
    }

    /**
     * Gets the cards accepted by the target filter being targeted by the specified action.
     * @param game the game
     * @param action the action
     * @param targetFilter the target filter
     * @return true or false
     */
    public static List<PhysicalCard> getCardsTargeted(SwccgGame game, Action action, Filter targetFilter) {
        List<PhysicalCard> cardsTargeted = new LinkedList<PhysicalCard>();

        // Add primary targets
        Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> targetingInfo = action.getAllPrimaryTargetCards();
        for (Map<PhysicalCard, Set<TargetingReason>> targetingGroup : targetingInfo.values()) {
            for (PhysicalCard targetedCard : targetingGroup.keySet()) {
                if (!cardsTargeted.contains(targetedCard)) {
                    if (Filters.and(targetFilter).accepts(game, targetedCard)) {
                        cardsTargeted.add(targetedCard);
                    }
                }
            }
        }

        // Add secondary targets
        List<PhysicalCard> secondaryTargets = action.getAllSecondaryTargetCards(game);
        for (PhysicalCard secondaryTarget : secondaryTargets) {
            if (!cardsTargeted.contains(secondaryTarget)) {
                if (Filters.and(targetFilter).accepts(game, secondaryTarget)) {
                    cardsTargeted.add(secondaryTarget);
                }
            }
        }

        return cardsTargeted;
    }

    /**
     * Gets the targeting reasons that cards accepted by the target filter are being targeted by the specified action.
     * @param game the game
     * @param action the action
     * @param targetFilter the target filter
     * @return true or false
     */
    public static Set<TargetingReason> getTargetingReasons(SwccgGame game, Action action, Filter targetFilter) {
        Set<TargetingReason> targetingReasons = new HashSet<TargetingReason>();

        Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> targetingInfo = action.getAllPrimaryTargetCards();
        for (Map<PhysicalCard, Set<TargetingReason>> targetingGroup : targetingInfo.values()) {
            for (PhysicalCard targetedCard : targetingGroup.keySet()) {
                if (Filters.and(targetFilter).accepts(game, targetedCard)) {
                    targetingReasons.addAll(targetingGroup.get(targetedCard));
                }
            }
        }
        return targetingReasons;
    }


    /**
     * Gets the cards accepted by the target filter being targeted by the specified action for any of the specified reasons.
     * @param game the game
     * @param action the action
     * @param targetingReasons the reasons to target
     * @param targetFilter the target filter
     * @return true or false
     */
    public static List<PhysicalCard> getCardsTargetedForReason(SwccgGame game, Action action, Collection<TargetingReason> targetingReasons, Filter targetFilter) {
        List<PhysicalCard> cardsTargeted = new LinkedList<PhysicalCard>();

        Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> targetingInfo = action.getAllPrimaryTargetCards();
        for (Map<PhysicalCard, Set<TargetingReason>> targetingGroup : targetingInfo.values()) {
            for (PhysicalCard targetedCard : targetingGroup.keySet()) {
                if (!cardsTargeted.contains(targetedCard)) {
                    for (TargetingReason targetingReason : targetingReasons) {
                        if (targetingGroup.get(targetedCard).contains(targetingReason)) {
                            if (Filters.and(targetFilter).accepts(game, targetedCard)) {
                                cardsTargeted.add(targetedCard);
                            }
                        }
                    }
                }
            }
        }
        return cardsTargeted;
    }
}
