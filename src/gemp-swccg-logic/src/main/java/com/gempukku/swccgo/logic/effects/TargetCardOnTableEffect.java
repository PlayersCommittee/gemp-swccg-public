package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * An abstract effect to target a card on the table.
 */
public abstract class TargetCardOnTableEffect extends TargetCardsOnTableEffect {

    /**
     * Creates an effect to target a card accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, int maximumAcceptsCount, boolean matchPartialModelType, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, maximumAcceptsCount, matchPartialModelType, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filter for the specified targeting reason.
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, TargetingReason targetingReason, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, null, targetingReason, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filter for the specified targeting reason.
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, int maximumAcceptsCount, boolean matchPartialModelType, TargetingReason targetingReason, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, maximumAcceptsCount, matchPartialModelType, null, targetingReason, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filter for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, null, targetingReasons, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filter for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, int maximumAcceptsCount, boolean matchPartialModelType, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, maximumAcceptsCount, matchPartialModelType, null, targetingReasons, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filters for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, Map<TargetingReason, Filterable> targetFiltersMap) {
        super(action, playerId, choiceText, 1, 1, null, targetFiltersMap);
    }

    /**
     * Creates an effect to target a card accepted by the specified filters for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, int maximumAcceptsCount, boolean matchPartialModelType, Map<TargetingReason, Filterable> targetFiltersMap) {
        super(action, playerId, choiceText, 1, 1, maximumAcceptsCount, matchPartialModelType, null, targetFiltersMap);
    }

    /**
     * Creates an effect to target a card accepted by the specified filter (using the specified spotOverrides to override
     * which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param spotOverrides the spot overrides
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, spotOverrides, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filter (using the specified spotOverrides to override
     * which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param choiceText the text shown to the player choosing the target
     * @param spotOverrides the spot overrides
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, int maximumAcceptsCount, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, maximumAcceptsCount, matchPartialModelType, spotOverrides, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filter for the specified targeting reason (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param spotOverrides the spot overrides
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, spotOverrides, targetingReason, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filter for the specified targeting reason (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param spotOverrides the spot overrides
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, int maximumAcceptsCount, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, maximumAcceptsCount, matchPartialModelType, spotOverrides, targetingReason, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filter for the specified targeting reasons (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param spotOverrides the spot overrides
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filter for the specified targeting reasons (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param spotOverrides the spot overrides
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, int maximumAcceptsCount, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        super(action, playerId, choiceText, 1, 1, maximumAcceptsCount, matchPartialModelType, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Creates an effect to target a card accepted by the specified filters for the specified targeting reasons (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param choiceText the text shown to the player choosing the target
     * @param spotOverrides the spot overrides
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        super(action, playerId, choiceText, 1, 1, spotOverrides, targetFiltersMap);
    }

    /**
     * Creates an effect to target a card accepted by the specified filters for the specified targeting reasons (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param choiceText the text shown to the player choosing the target
     * @param spotOverrides the spot overrides
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetCardOnTableEffect(Action action, String playerId, String choiceText, int maximumAcceptsCount, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        super(action, playerId, choiceText, 1, 1, maximumAcceptsCount, matchPartialModelType, spotOverrides, targetFiltersMap);
    }

    @Override
    protected final void cardsTargeted(int targetGroupId, Collection<PhysicalCard> targetedCards) {
        if (targetedCards.size() == 1)
            cardTargeted(targetGroupId, targetedCards.iterator().next());
    }

    /**
     * This method is called when a card has been targeted.
     * @param targetGroupId the target group id
     * @param targetedCard the targeted card
     */
    protected abstract void cardTargeted(int targetGroupId, PhysicalCard targetedCard);
}
