package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * An abstract effect to target all cards at the same location that are accepted by a specified filter.
 */
public abstract class TargetAllCardsAtSameLocationEffect extends TargetCardsAtSameLocationEffect {

    /**
     * Creates an effect to target all cards accepted by the specified filter that are at the same location.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, Filterable targetFilters) {
        this(action, playerId, choiceText, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target all cards accepted by the specified filter that are at the same location.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, boolean matchPartialModelType, Filterable targetFilters) {
        this(action, playerId, choiceText, matchPartialModelType, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target all cards accepted by the specified filter that are at the same location for the specified targeting reason.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, TargetingReason targetingReason, Filterable targetFilters) {
        this(action, playerId, choiceText, null, targetingReason, targetFilters);
    }

    /**
     * Creates an effect to target all cards accepted by the specified filter that are at the same location for the specified targeting reason.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, boolean matchPartialModelType, TargetingReason targetingReason, Filterable targetFilters) {
        this(action, playerId, choiceText, matchPartialModelType, null, targetingReason, targetFilters);
    }

    /**
     * Creates an effect to target all cards accepted by the specified filter that are at the same location for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        this(action, playerId, choiceText, null, targetingReasons, targetFilters);
    }

    /**
     * Creates an effect to target all cards accepted by the specified filter that are at the same location for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, boolean matchPartialModelType, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        this(action, playerId, choiceText, matchPartialModelType, null, targetingReasons, targetFilters);
    }

    /**
     * Creates an effect to target all cards accepted by the specified filters that are at the same location for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, Map<TargetingReason, Filterable> targetFiltersMap) {
        this(action, playerId, choiceText, null, targetFiltersMap);
    }

    /**
     * Creates an effect to target all cards accepted by the specified filters that are at the same location for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, boolean matchPartialModelType, Map<TargetingReason, Filterable> targetFiltersMap) {
        this(action, playerId, choiceText, matchPartialModelType, null, targetFiltersMap);
    }

    /**
     * Creates an effect to target all cards accepted by the specified filter that are at the same location (using the specified
     * spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param spotOverrides the spot overrides
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        this(action, playerId, choiceText, spotOverrides, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target all cards accepted by the specified filter that are at the same location (using the specified
     * spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param spotOverrides the spot overrides
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        this(action, playerId, choiceText, matchPartialModelType, spotOverrides, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target all cards accepted by the specified filter that are at the same location for the specified
     * targeting reason (using the specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param spotOverrides the spot overrides
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        this(action, playerId, choiceText, spotOverrides, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Creates an effect to target all cards accepted by the specified filter that are at the same location for the specified
     * targeting reason (using the specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param spotOverrides the spot overrides
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        this(action, playerId, choiceText, matchPartialModelType, spotOverrides, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter that are at the same location for the specified
     * targeting reasons (using the specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param spotOverrides the spot overrides
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        this(action, playerId, choiceText, true, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter that are at the same location for the specified
     * targeting reasons (using the specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param spotOverrides the spot overrides
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, matchPartialModelType, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filters for the specified targeting reasons (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param spotOverrides the spot overrides
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        this(action, playerId, choiceText, true, spotOverrides, targetFiltersMap);
    }

    /**
     * Creates an effect to target cards accepted by the specified filters for the specified targeting reasons (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param spotOverrides the spot overrides
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetAllCardsAtSameLocationEffect(Action action, String playerId, String choiceText, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        super(action, playerId, choiceText, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, matchPartialModelType, spotOverrides, targetFiltersMap);
    }

    /**
     * Determines whether all cards together at the location can be targeted are targeted.
     */
    @Override
    protected final boolean isTargetAll() {
        return true;
    }
}
