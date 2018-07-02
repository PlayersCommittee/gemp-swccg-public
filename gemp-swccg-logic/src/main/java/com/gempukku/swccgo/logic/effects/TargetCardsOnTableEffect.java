package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.TargetingType;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.*;

/**
 * An abstract effect to target cards on the table.
 */
public abstract class TargetCardsOnTableEffect extends AbstractTargetCardsEffect {
    private final PhysicalCard _source;
    private String _playerId;
    private String _choiceText;
    private final int _minimum;
    private final int _maximum;
    private int _maximumAcceptsCount;
    private boolean _matchPartialModelType;
    private Map<InactiveReason, Boolean> _spotOverrides = new HashMap<InactiveReason, Boolean>();
    private Map<TargetingReason, Filterable> _targetFiltersMap = new HashMap<TargetingReason, Filterable>();
    private boolean cardSelectionFailed;

    /**
     * Creates an effect to target cards accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Filterable targetFilters) {
        this(action, playerId, choiceText, minimum, maximum, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, Filterable targetFilters) {
        this(action, playerId, choiceText, minimum, maximum, maximumAcceptsCount, matchPartialModelType, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter for the specified targeting reason.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, TargetingReason targetingReason, Filterable targetFilters) {
        this(action, playerId, choiceText, minimum, maximum, null, targetingReason, targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter for the specified targeting reason.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, TargetingReason targetingReason, Filterable targetFilters) {
        this(action, playerId, choiceText, minimum, maximum, maximumAcceptsCount, matchPartialModelType, null, targetingReason, targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        this(action, playerId, choiceText, minimum, maximum, null, targetingReasons, targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        this(action, playerId, choiceText, minimum, maximum, maximumAcceptsCount, matchPartialModelType, null, targetingReasons, targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filters for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Map<TargetingReason, Filterable> targetFiltersMap) {
        this(action, playerId, choiceText, minimum, maximum, null, targetFiltersMap);
    }

    /**
     * Creates an effect to target cards accepted by the specified filters for the specified targeting reasons.
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, Map<TargetingReason, Filterable> targetFiltersMap) {
        this(action, playerId, choiceText, minimum, maximum, maximumAcceptsCount, matchPartialModelType, null, targetFiltersMap);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter (using the specified spotOverrides to override
     * which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param spotOverrides the spot overrides
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        this(action, playerId, choiceText, minimum, maximum, spotOverrides, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter (using the specified spotOverrides to override
     * which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param spotOverrides the spot overrides
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        this(action, playerId, choiceText, minimum, maximum, maximumAcceptsCount, matchPartialModelType, spotOverrides, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter for the specified targeting reason (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param spotOverrides the spot overrides
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        this(action, playerId, choiceText, minimum, maximum, spotOverrides, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter for the specified targeting reason (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param spotOverrides the spot overrides
     * @param targetingReason the targeting reason
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        this(action, playerId, choiceText, minimum, maximum, maximumAcceptsCount, matchPartialModelType, spotOverrides, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter for the specified targeting reasons (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param spotOverrides the spot overrides
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        this(action, playerId, choiceText, minimum, maximum, Integer.MAX_VALUE, true, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Creates an effect to target cards accepted by the specified filter for the specified targeting reasons (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param spotOverrides the spot overrides
     * @param targetingReasons the targeting reasons
     * @param targetFilters the filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        super(action);
        _source = action.getActionSource();
        _playerId = playerId;
        _choiceText = choiceText;
        _minimum = minimum;
        _maximum = maximum;
        _maximumAcceptsCount = maximumAcceptsCount;
        _matchPartialModelType = matchPartialModelType;
        if (spotOverrides != null) {
            _spotOverrides.putAll(spotOverrides);
        }
        // Add filter for cards that can be targeted by source card for each specified reason.
        for (TargetingReason targetingReason : targetingReasons) {
            Filterable updatedTargetFilters = targetFilters;
            if (targetingReason != TargetingReason.NONE) {
                // Use getActionAttachedToCard so card being played is used
                updatedTargetFilters = Filters.and(updatedTargetFilters, Filters.canBeTargetedBy(action.getActionAttachedToCard(), action.getActionAttachedToCardBuiltIn(), targetingReason));
            }
            _targetFiltersMap.put(targetingReason, updatedTargetFilters);
        }
    }

    /**
     * Creates an effect to target cards accepted by the specified filters for the specified targeting reasons (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param spotOverrides the spot overrides
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        this(action, playerId, choiceText, minimum, maximum, Integer.MAX_VALUE, true, spotOverrides, targetFiltersMap);
    }

    /**
     * Creates an effect to target cards accepted by the specified filters for the specified targeting reasons (using the
     * specified spotOverrides to override which cards are visible).
     * @param action the action performing this effect
     * @param playerId the player that chooses the targets
     * @param choiceText the text shown to the player choosing the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param spotOverrides the spot overrides
     * @param targetFiltersMap the map of targeting reason to target filter
     */
    public TargetCardsOnTableEffect(Action action, String playerId, String choiceText, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        super(action);
        _source = action.getActionSource();
        _playerId = playerId;
        _choiceText = choiceText;
        _minimum = minimum;
        _maximum = maximum;
        _maximumAcceptsCount = maximumAcceptsCount;
        _matchPartialModelType = matchPartialModelType;
        if (spotOverrides != null) {
            _spotOverrides.putAll(spotOverrides);
        }
        // Add filter for cards that can be targeted by source card for the specified reason.
        for (TargetingReason targetingReason : targetFiltersMap.keySet()) {
            Filterable updatedTargetFilters = targetFiltersMap.get(targetingReason);
            if (targetingReason != TargetingReason.NONE) {
                // Use getActionAttachedToCard so card being played is used
                updatedTargetFilters = Filters.and(updatedTargetFilters, Filters.canBeTargetedBy(action.getActionAttachedToCard(), action.getActionAttachedToCardBuiltIn(), targetingReason));
            }
            _targetFiltersMap.put(targetingReason, updatedTargetFilters);
        }
    }

    /**
     * Determines whether selection are automatically made is number of cards to select is the same as the minimum number to choose.
     */
    protected boolean getUseShortcut() {
        return false;
    }

    /**
     * Determines whether stacked cards can be targeted.
     */
    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
        return false;
    }

    /**
     * Gets the targeting reasons for targeting the specified card.
     * @param game the game
     * @param matchingCard the card
     * @return the targeting reasons
     */
    private Set<TargetingReason> getTargetingReasons(SwccgGame game, PhysicalCard matchingCard) {
        Set<TargetingReason> targetingReasons = new HashSet<TargetingReason>();

        for (TargetingReason targetingReason : _targetFiltersMap.keySet()) {
            Filterable filterable = _targetFiltersMap.get(targetingReason);
            if (Filters.and(filterable).accepts(game.getGameState(), game.getModifiersQuerying(), matchingCard)) {
                targetingReasons.add(targetingReason);
            }
        }

        return targetingReasons;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        // If player is not set, set to current player to make choices
        if (_playerId == null) {
            _playerId = game.getGameState().getCurrentPlayerId();
        }

        Collection<PhysicalCard> matchingCards = Filters.filterActive(game, _source, _matchPartialModelType, _spotOverrides, _targetFiltersMap);
        if (isIncludeStackedCardsTargetedByWeaponsAsIfPresent()) {
            matchingCards = new LinkedList<PhysicalCard>(matchingCards);
            matchingCards.addAll(Filters.filter(Filters.filterStacked(game, _matchPartialModelType, _targetFiltersMap), game, Filters.canBeTargetedByWeaponAsIfPresent));
        }

        // Filter cards by accounting for cards with multiple classes
        int acceptsCountSoFar = 0;
        List<PhysicalCard> validCards = new LinkedList<PhysicalCard>();
        for (PhysicalCard selectableCard : matchingCards) {
            int acceptsCount = Filters.count(Collections.singletonList(selectableCard), game, _source, true, _targetFiltersMap);
            if (acceptsCount > 0 && acceptsCount <= _maximumAcceptsCount) {
                validCards.add(selectableCard);
                acceptsCountSoFar += acceptsCount;
            }
        }
        matchingCards = validCards;

        // Make sure at least the minimum number of cards can be found
        if (acceptsCountSoFar < _minimum) {
            return new FullEffectResult(false);
        }

        // Lets get the counts realistic
        int maximum = Math.min(_maximum, matchingCards.size());
        final int minimum = _minimum;

        if (maximum == 0) {
            cardsTargeted(-1, Collections.<PhysicalCard>emptySet());
        }
        else if (matchingCards.size() == minimum && (getUseShortcut() || !_action.isAllowAbort())) {

            // Let the action know the targeted cards (and reasons), so that info can be seen by responses to that action
            Map<PhysicalCard, Set<TargetingReason>> targetMap = new HashMap<PhysicalCard, Set<TargetingReason>>();
            for (PhysicalCard matchingCard : matchingCards) {
                targetMap.put(matchingCard, getTargetingReasons(game, matchingCard));
            }
            int targetGroupId = _action.addPrimaryTargetCards(_choiceText, _minimum, _maximum, _maximumAcceptsCount, _matchPartialModelType, false, TargetingType.TARGET_CARDS, targetMap, _spotOverrides, _targetFiltersMap);
            cardsTargeted(targetGroupId, matchingCards);
        }
        else {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new CardsSelectionDecision(_choiceText + (_action.isAllowAbort() ? ", or click 'Done' to cancel" : ""), matchingCards, _action.isAllowAbort() ? 0 : minimum, maximum) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                            int numSelected = Filters.count(selectedCards, game, _source, true, _targetFiltersMap);
                            cardSelectionFailed = numSelected < minimum;
                            if (cardSelectionFailed && _action.isAllowAbort()) {
                                return;
                            }

                            // Let the action know the targeted cards (and reasons), so that info can be seen by responses to that action
                            Map<PhysicalCard, Set<TargetingReason>> targetMap = new HashMap<PhysicalCard, Set<TargetingReason>>();
                            for (PhysicalCard selectedCard : selectedCards) {
                                targetMap.put(selectedCard, getTargetingReasons(game, selectedCard));
                            }
                            int targetGroupId = _action.addPrimaryTargetCards(_choiceText, _minimum, _maximum, _maximumAcceptsCount, _matchPartialModelType, false, TargetingType.TARGET_CARDS, targetMap, _spotOverrides, _targetFiltersMap);
                            cardsTargeted(targetGroupId, selectedCards);
                        }
                    });
        }

        return new FullEffectResult(true);
    }

    @Override
    public boolean wasCarriedOut() {
        return super.wasCarriedOut() && !cardSelectionFailed;
    }

    /**
     * This method is called when cards have been targeted.
     * @param targetGroupId the target group id
     * @param targetedCards the targeted cards
     */
    protected abstract void cardsTargeted(int targetGroupId, Collection<PhysicalCard> targetedCards);
}
