package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.*;

import java.util.*;

/**
 * The abstract class containing basic structure and implementation used for all actions.
 */
public abstract class AbstractAction implements Action {
    /**
     * Lists for the costs and effects for the action
     */
    private LinkedList<TargetingEffect> _targetingCosts = new LinkedList<TargetingEffect>();
    private LinkedList<TargetingEffect> _processedTargetingCosts = new LinkedList<TargetingEffect>();
    private LinkedList<UsageEffect> _usageCosts = new LinkedList<UsageEffect>();
    private LinkedList<UsageEffect> _processedUsageCosts = new LinkedList<UsageEffect>();
    private LinkedList<StandardEffect> _beforeStandardCosts = new LinkedList<StandardEffect>();
    private LinkedList<StandardEffect> _processedBeforeStandardCosts = new LinkedList<StandardEffect>();
    private LinkedList<StandardEffect> _standardCosts = new LinkedList<StandardEffect>();
    private LinkedList<StandardEffect> _processedStandardCosts = new LinkedList<StandardEffect>();
    private LinkedList<StandardEffect> _effects = new LinkedList<StandardEffect>();
    private LinkedList<StandardEffect> _processedEffects = new LinkedList<StandardEffect>();
    private LinkedList<StandardEffect> _afterEffects = new LinkedList<StandardEffect>();
    private LinkedList<StandardEffect> _processedAfterEffects = new LinkedList<StandardEffect>();

    private String _performingPlayer;
    private boolean _virtualCardAction;
    private boolean _allowAbort;
    private boolean _choosingTargetsComplete;

    protected int _latestTargetGroupId;
    protected Map<Integer, String> _targetingTextMap = new HashMap<Integer, String>();
    protected Map<Integer, Integer> _minimumMap = new HashMap<Integer, Integer>();
    protected Map<Integer, Integer> _maximumMap = new HashMap<Integer, Integer>();
    protected Map<Integer, Integer> _maximumAcceptsCountMap = new HashMap<Integer, Integer>();
    protected Map<Integer, Boolean> _matchPartialModelTypeMap = new HashMap<Integer, Boolean>();
    protected Map<Integer, Boolean> _targetingAllMap = new HashMap<Integer, Boolean>();
    protected Map<Integer, TargetingType> _targetingTypeMap = new HashMap<Integer, TargetingType>();
    protected Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> _targetGroupMap = new HashMap<Integer, Map<PhysicalCard, Set<TargetingReason>>>();
    protected Map<Integer, Map<InactiveReason, Boolean>> _spotOverridesMap = new HashMap<Integer, Map<InactiveReason, Boolean>>();
    protected Map<Integer, Map<TargetingReason, Filterable>> _targetFiltersMap = new HashMap<Integer, Map<TargetingReason, Filterable>>();
    protected List<Collection<PhysicalCard>> _animationGroupList = new ArrayList<Collection<PhysicalCard>>();
    protected List<Filterable> _secondaryTargetFiltersList = new ArrayList<Filterable>();

    /**
     * Needed to generate snapshot.
     */
    public AbstractAction() {
    }

    @Override
    public void generateSnapshot(Action selfSnapshot, SnapshotData snapshotData) {
        AbstractAction snapshot = (AbstractAction) selfSnapshot;

        snapshot._targetingCosts.addAll(_targetingCosts);
        snapshot._processedTargetingCosts.addAll(_processedTargetingCosts);
        snapshot._usageCosts.addAll(_usageCosts);
        snapshot._processedUsageCosts.addAll(_processedUsageCosts);
        snapshot._beforeStandardCosts.addAll(_beforeStandardCosts);
        snapshot._processedBeforeStandardCosts.addAll(_processedBeforeStandardCosts);
        snapshot._standardCosts.addAll(_standardCosts);
        snapshot._processedStandardCosts.addAll(_processedStandardCosts);
        snapshot._effects.addAll(_effects);
        snapshot._processedEffects.addAll(_processedEffects);
        snapshot._afterEffects.addAll(_afterEffects);
        snapshot._processedAfterEffects.addAll(_processedAfterEffects);
        snapshot._performingPlayer = _performingPlayer;
        snapshot._virtualCardAction = _virtualCardAction;
        snapshot._allowAbort = _allowAbort;
        snapshot._choosingTargetsComplete = _choosingTargetsComplete;
        snapshot._latestTargetGroupId = _latestTargetGroupId;
        snapshot._targetingTextMap.putAll(_targetingTextMap);
        snapshot._minimumMap.putAll(_minimumMap);
        snapshot._maximumMap.putAll(_maximumMap);
        snapshot._maximumAcceptsCountMap.putAll(_maximumAcceptsCountMap);
        snapshot._matchPartialModelTypeMap.putAll(_matchPartialModelTypeMap);
        snapshot._targetingAllMap.putAll(_targetingAllMap);
        snapshot._targetingTypeMap.putAll(_targetingTypeMap);
        for (Integer targetGroupId : _targetGroupMap.keySet()) {
            Map<PhysicalCard, Set<TargetingReason>> snapshotMap = new HashMap<PhysicalCard, Set<TargetingReason>>();
            snapshot._targetGroupMap.put(targetGroupId, snapshotMap);
            Map<PhysicalCard, Set<TargetingReason>> map = _targetGroupMap.get(targetGroupId);
            for (PhysicalCard card : map.keySet()) {
                Set<TargetingReason> snapshotSet = new HashSet<TargetingReason>(map.get(card));
                snapshotMap.put(snapshotData.getDataForSnapshot(card), snapshotSet);
            }
        }
        for (Integer targetGroupId : _spotOverridesMap.keySet()) {
            Map<InactiveReason, Boolean> snapshotMap = new HashMap<InactiveReason, Boolean>(_spotOverridesMap.get(targetGroupId));
            snapshot._spotOverridesMap.put(targetGroupId, snapshotMap);
        }
        for (Integer targetGroupId : _targetFiltersMap.keySet()) {
            Map<TargetingReason, Filterable> snapshotMap = new HashMap<TargetingReason, Filterable>(_targetFiltersMap.get(targetGroupId));
            snapshot._targetFiltersMap.put(targetGroupId, snapshotMap);
        }
        for (Collection<PhysicalCard> cardCollection : _animationGroupList) {
            Collection<PhysicalCard> snapshotCollection = new ArrayList<PhysicalCard>();
            snapshot._animationGroupList.add(snapshotCollection);
            for (PhysicalCard card : cardCollection) {
                snapshotCollection.add(snapshotData.getDataForSnapshot(card));
            }
        }
        snapshot._secondaryTargetFiltersList.addAll(_secondaryTargetFiltersList);
    }

    /**
     * Gets the card that is the source of the action or the card that the action is attached to if the action comes
     * from a game rule.
     *
     * @return the card, or null
     */
    @Override
    public PhysicalCard getActionAttachedToCard() {
        return getActionSource();
    }

    /**
     * Gets the card built-in that is the source of the action or null if not associated with a card built-in.
     *
     * @return the card built-in, or null
     */
    @Override
    public SwccgBuiltInCardBlueprint getActionAttachedToCardBuiltIn() {
        return null;
    }

    /**
     * Sets an action as being an optional action from a card that may not on the table. This is used so the user
     * interface can show the image of the card that is off table, so the action can be select to be performed.
     *
     * @param offTableCardAction true if action is an optional off table card action, otherwise false.
     */
    @Override
    public void setOptionalOffTableCardAction(boolean offTableCardAction) {
        _virtualCardAction = offTableCardAction;
    }

    /**
     * Determines if this action is an optional action from a card that may not on the table. This is used so the user
     * interface can show the image of the card that is off table, so the action can be select to be performed.
     *
     * @return true if action is an optional off table card action, otherwise false
     */
    @Override
    public boolean isOptionalOffTableCardAction() {
        return _virtualCardAction;
    }

    /**
     * Sets the player performing the action, or null if no player is specifically performing the action (such as a game rule).
     *
     * @param playerId the player, or null
     */
    @Override
    public void setPerformingPlayer(String playerId) {
        _performingPlayer = playerId;
    }

    /**
     * Gets the player performing the action, or null if no player is specifically performing the action (such as a game
     * rule or required trigger action).
     *
     * @return the player, or null
     */
    @Override
    public String getPerformingPlayer() {
        return _performingPlayer;
    }

    /**
     * Determines if the action is from game text.
     *
     * @return true if from game text, otherwise false
     */
    @Override
    public boolean isFromGameText() {
        return false;
    }

    /**
     * Determines if the action is from playing an Interrupt.
     *
     * @return true if from playing an Interrupt, otherwise false
     */
    @Override
    public boolean isFromPlayingInterrupt() {
        return false;
    }

    /**
     * Gets the card id of the card the game text is originally from
     *
     * @return the card id
     */
    @Override
    public int getGameTextSourceCardId() {
        throw new UnsupportedOperationException("This method, getGameTextSourceCardId(), should not be called on action of: " + getActionSource());
    }

    /**
     * Gets the game text action id.
     *
     * @return the game text action id
     */
    @Override
    public GameTextActionId getGameTextActionId() {
        throw new UnsupportedOperationException("This method, getGameTextActionId(), should not be called on this action of: " + getActionSource());
    }

    /**
     * Sets if deployment of the card may be aborted when choosing deploy target.
     *
     * @param allowAbort true if deployment of the card may be aborted when choosing deploy target, otherwise false
     */
    @Override
    public void setAllowAbort(boolean allowAbort) {
        _allowAbort = allowAbort;
    }

    /**
     * Determines if deployment of the card may be aborted when choosing deploy target.
     *
     * @return true if deployment of the card may be aborted when choosing deploy target, otherwise false
     */
    @Override
    public boolean isAllowAbort() {
        return _allowAbort && !isChoosingTargetsComplete();
    }

    /**
     * Determines if choosing targets is complete. If choosing targets is complete, then action cannot be aborted and
     * any message about the action being initiated can be sent.
     *
     * @return true if choosing targets is complete, otherwise false.
     */
    @Override
    public final boolean isChoosingTargetsComplete() {
        return _choosingTargetsComplete;
    }

    /**
     * Appends the specified targeting to the list of the costs. It will be executed after all the other costs currently in
     * the queue.
     *
     * @param cost the target cards effect
     */
    @Override
    public final void appendTargeting(TargetingEffect cost) {
        if (!_standardCosts.isEmpty() || !_processedStandardCosts.isEmpty()
                || !_effects.isEmpty() || !_processedEffects.isEmpty()
                || !_processedAfterEffects.isEmpty())
            throw new UnsupportedOperationException("Called appendTargeting() in incorrect order");

        _targetingCosts.add(cost);
    }

    /**
     * Appends the specified usage to the list of the costs. It will be executed after all the other costs currently in
     * the queue.
     *
     * @param cost the usage limit effect
     */
    @Override
    public final void appendUsage(UsageEffect cost) {
        if (!_standardCosts.isEmpty() || !_processedStandardCosts.isEmpty()
                || !_effects.isEmpty() || !_processedEffects.isEmpty()
                || !_processedAfterEffects.isEmpty())
            throw new UnsupportedOperationException("Called appendUsage() in incorrect order");

        _usageCosts.add(cost);
    }

    /**
     * Appends the specified cost to the list of the costs. It will be executed after the other costs currently in
     * the queue, but before any added from appendCost().
     *
     * @param cost the cost
     */
    @Override
    public final void appendBeforeCost(StandardEffect cost) {
        if (!_processedStandardCosts.isEmpty()
                || !_processedEffects.isEmpty()
                || !_processedAfterEffects.isEmpty())
            throw new UnsupportedOperationException("Called appendBeforeCost() in incorrect order");

        _beforeStandardCosts.add(cost);
    }

    /**
     * Appends the specified cost to the list of the costs. It will be executed after all the other costs currently in
     * the queue.
     *
     * @param cost the cost
     */
    @Override
    public void appendCost(StandardEffect cost) {
        if (!_effects.isEmpty() || !_processedEffects.isEmpty()
                || !_processedAfterEffects.isEmpty())
            throw new UnsupportedOperationException("Called appendCost() in incorrect order");

        _choosingTargetsComplete = true;
        _standardCosts.add(cost);
    }

    /**
     * Inserts the specified effects as the next effects to be executed.
     *
     * @param effect the effects
     */
    @Override
    public final void insertEffect(StandardEffect... effect) {
        _choosingTargetsComplete = true;
        _effects.addAll(0, Arrays.asList(effect));
    }

    /**
     * Appends the specified effect to the list of the effects. It will be executed after all the other costs currently
     * in the queue.
     *
     * @param effect the effect
     */
    @Override
    public final void appendEffect(StandardEffect effect) {
        _choosingTargetsComplete = true;
        _effects.add(effect);
    }

    /**
     * Inserts the specified effects as the next after effects to be executed.  It will be executed after all the other
     * effects currently in the queue. These effects do not need to be successful for the action to be considered carried out.
     *
     * @param effect the effects
     */
    @Override
    public final void insertAfterEffect(StandardEffect... effect) {
        _afterEffects.addAll(0, Arrays.asList(effect));
    }

    /**
     * Appends the specified effect to the list of the after effects. It will be executed after all the other effects
     * currently in the queue. These effects do not need to be successful for the action to be considered carried out.
     *
     * @param effect the effect
     */
    @Override
    public final void appendAfterEffect(StandardEffect effect) {
        _afterEffects.add(effect);
    }

    /**
     * Determines if any of the costs of this action failed. This is checked because if any of the costs of an action
     * fail, then the action effects are not performed.
     *
     * @return true if any cost failed, otherwise false.
     */
    protected boolean isAnyCostFailed() {
        for (Effect processedCost : _processedTargetingCosts) {
            if (!processedCost.wasCarriedOut())
                return true;
        }
        for (Effect processedCost : _processedUsageCosts) {
            if (!processedCost.wasCarriedOut())
                return true;
        }
        for (StandardEffect processedCost : _processedBeforeStandardCosts) {
            if (!processedCost.wasCarriedOut())
                return true;
        }
        for (StandardEffect processedCost : _processedStandardCosts) {
            if (!processedCost.wasCarriedOut())
                return true;
        }
        return false;
    }

    /**
     * Gets the next action cost to be processed.
     *
     * @return action cost to process
     */
    protected final Effect getNextCost() {
        TargetingEffect targetingCost = _targetingCosts.poll();
        if (targetingCost != null) {
            targetingCost.setAction(this);
            _processedTargetingCosts.add(targetingCost);
            return targetingCost;
        }

        UsageEffect usageCost = _usageCosts.poll();
        if (usageCost != null) {
            usageCost.setAction(this);
            _processedUsageCosts.add(usageCost);
            return usageCost;
        }

        StandardEffect beforeStandardCost = _beforeStandardCosts.poll();
        if (beforeStandardCost != null) {
            beforeStandardCost.setAction(this);
            _processedBeforeStandardCosts.add(beforeStandardCost);
            return beforeStandardCost;
        }

        StandardEffect standardCost = _standardCosts.poll();
        if (standardCost != null) {
            standardCost.setAction(this);
            _processedStandardCosts.add(standardCost);
        }

        return standardCost;
    }

    /**
     * Gets the next action effect to be processed.
     *
     * @return action effect to process
     */
    protected final Effect getNextEffect() {
        StandardEffect effect = _effects.poll();
        if (effect != null) {
            effect.setAction(this);
            _processedEffects.add(effect);
        }

        return effect;
    }

    /**
     * Gets the next after effect to be performed after this action, or null if no after effects remain.
     *
     * @param game the game
     * @return the next after effect to be performed after this action, or null if no after effects remain
     */
    @Override
    public final Effect nextAfterEffect(SwccgGame game) {
        StandardEffect afterEffect = _afterEffects.poll();
        if (afterEffect != null) {
            afterEffect.setAction(this);
            _processedAfterEffects.add(afterEffect);
        }

        return afterEffect;
    }

    /**
     * Adds target cards to the the group with the target group id. The spotOverrides and targetFiltersMap define the
     * parameters used in the targeting, which can be needed if a response need to know what else would be a valid target.
     *
     * @param text                  the text shown when selecting the targets
     * @param minimum               the minimum number of cards to target
     * @param maximum               the maximum number of cards to target
     * @param maximumAcceptsCount   the maximum number of times cards may be accepted by the filter, which will further limit
     *                              cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param isTargetAll           determines if all cards that can be targeted are automatically targeted
     * @param targetingType         the type of targeting to use
     * @param targets               the cards and the reasons each card is targeted
     * @param spotOverrides         overrides which cards can be seen as "active" for this targeting
     * @param targetFiltersMap      the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                              seen by the source card.
     * @return the target group id
     */
    @Override
    public final int addPrimaryTargetCards(String text, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, boolean isTargetAll, TargetingType targetingType,
                                           Map<PhysicalCard, Set<TargetingReason>> targets, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        _latestTargetGroupId++;
        _targetingTextMap.put(_latestTargetGroupId, text);
        _minimumMap.put(_latestTargetGroupId, minimum);
        _maximumMap.put(_latestTargetGroupId, maximum);
        _maximumAcceptsCountMap.put(_latestTargetGroupId, maximumAcceptsCount);
        _matchPartialModelTypeMap.put(_latestTargetGroupId, matchPartialModelType);
        _targetingAllMap.put(_latestTargetGroupId, isTargetAll);
        _targetingTypeMap.put(_latestTargetGroupId, targetingType);
        Map<PhysicalCard, Set<TargetingReason>> reasonMap = _targetGroupMap.get(_latestTargetGroupId);
        if (reasonMap == null) {
            reasonMap = new HashMap<PhysicalCard, Set<TargetingReason>>();
            _targetGroupMap.put(_latestTargetGroupId, reasonMap);
        }
        reasonMap.putAll(targets);
        _spotOverridesMap.put(_latestTargetGroupId, spotOverrides);
        _targetFiltersMap.put(_latestTargetGroupId, targetFiltersMap);
        return _latestTargetGroupId;
    }

    /**
     * Gets the text shown when choosing targets for the specified target group id.
     *
     * @param targetGroupId the target group id
     * @return the text
     */
    @Override
    public final String getPrimaryTargetingText(int targetGroupId) {
        return _targetingTextMap.get(targetGroupId);
    }

    /**
     * Gets the minimum number of cards to target when choosing targets for the specified target group id.
     *
     * @param targetGroupId the target group id
     * @return the text
     */
    @Override
    public final Integer getPrimaryMinimumCardsToTarget(int targetGroupId) {
        return _minimumMap.get(targetGroupId);
    }

    /**
     * Gets the maximum number of cards to target when choosing targets for the specified target group id.
     *
     * @param targetGroupId the target group id
     * @return the text
     */
    @Override
    public final Integer getPrimaryMaximumCardsToTarget(int targetGroupId) {
        return _maximumMap.get(targetGroupId);
    }

    /**
     * Gets the the maximum number of times cards may be accepted by the filter for the specified target group id, which
     * will further limit cards that can be selected when cards with multiple model types accept filter multiple times.
     *
     * @param targetGroupId the target group id
     * @return the text
     */
    @Override
    public final Integer getPrimaryMaximumAcceptsCountToTarget(int targetGroupId) {
        return _maximumAcceptsCountMap.get(targetGroupId);
    }

    /**
     * Determines if card with multiple model types (i.e. squadrons) match if any model type matches the filter,
     * otherwise card only matches if all model types match the filter.
     *
     * @param targetGroupId the target group id
     * @return true if card with multiple model types (i.e. squadrons) match if any model type matches the filter,
     * otherwise card only matches if all model types match the filter
     */
    @Override
    public final Boolean getPrimaryTargetMatchPartialModelType(int targetGroupId) {
        return _matchPartialModelTypeMap.get(targetGroupId);
    }

    /**
     * Determines if all cards that can be targeted are automatically targeted.
     *
     * @param targetGroupId the target group id
     * @return true if all cards that can be targeted are automatically targeted, otherwise false
     */
    @Override
    public final Boolean getPrimaryTargetingAll(int targetGroupId) {
        return _targetingAllMap.get(targetGroupId);
    }

    /**
     * Determines type of targeting used.
     *
     * @param targetGroupId the target group id
     * @return the type of targeting used
     */
    @Override
    public final TargetingType getPrimaryTargetingType(int targetGroupId) {
        return _targetingTypeMap.get(targetGroupId);
    }

    /**
     * Gets the cards in the group with the specified target group id.
     *
     * @param targetGroupId the target group id
     * @return the cards
     */
    @Override
    public final Collection<PhysicalCard> getPrimaryTargetCards(int targetGroupId) {
        return _targetGroupMap.get(targetGroupId).keySet();
    }

    /**
     * Updates the targets cards in the group with the specified target group id.
     *
     * @param game          the game
     * @param targetGroupId the target group id
     * @param targetCards   the new targeted cards
     */
    @Override
    public final void updatePrimaryTargetCards(SwccgGame game, int targetGroupId, Collection<PhysicalCard> targetCards) {
        Map<PhysicalCard, Set<TargetingReason>> cardMap = _targetGroupMap.get(targetGroupId);
        cardMap.clear();
        for (PhysicalCard targetCard : targetCards) {
            Set<TargetingReason> targetingReasons = new HashSet<TargetingReason>();
            Map<TargetingReason, Filterable> filterableMap = _targetFiltersMap.get(targetGroupId);
            for (TargetingReason targetingReason : filterableMap.keySet()) {
                Filterable filterable = filterableMap.get(targetingReason);
                if (Filters.and(filterable).accepts(game.getGameState(), game.getModifiersQuerying(), targetCard)) {
                    targetingReasons.add(targetingReason);
                }
            }
            cardMap.put(targetCard, targetingReasons);
        }
    }

    /**
     * Gets the card in the group with the specified target group id.
     *
     * @param targetGroupId the target group id
     * @return the card
     */
    @Override
    public final PhysicalCard getPrimaryTargetCard(int targetGroupId) {
        if (!_targetGroupMap.get(targetGroupId).keySet().isEmpty()) {
            return _targetGroupMap.get(targetGroupId).keySet().iterator().next();
        }
        return null;
    }

    /**
     * Gets the spot overrides used in the targeting.
     *
     * @param targetGroupId the target group id
     * @return the spot overrides
     */
    @Override
    public final Map<InactiveReason, Boolean> getPrimaryTargetSpotOverrides(int targetGroupId) {
        return _spotOverridesMap.get(targetGroupId);
    }

    /**
     * Gets the target filter used in the targeting.
     *
     * @param targetGroupId the target group id
     * @return the target filter
     */
    @Override
    public final Map<TargetingReason, Filterable> getPrimaryTargetFilter(int targetGroupId) {
        return _targetFiltersMap.get(targetGroupId);
    }

    /**
     * Updates the target filter. For example, after all target groups have been targeted, the filters need to be updated
     * so if group is re-targeted, it is done in a valid combination with the other groups targeted.
     *
     * @param targetGroupId the target group id
     * @param targetFilters the filter
     */
    @Override
    public final void updatePrimaryTargetFilter(int targetGroupId, Filterable targetFilters) {
        if (!_targetFiltersMap.containsKey(targetGroupId)) {
            throw new UnsupportedOperationException("Called updatePrimaryTargetFilter() on invalid targetGroupId " + targetGroupId);
        }

        Map<TargetingReason, Filterable> targetFiltersMap = _targetFiltersMap.get(targetGroupId);
        for (TargetingReason targetingReason : targetFiltersMap.keySet()) {
            Filterable updatedTargetFilters = targetFilters;
            if (targetingReason != TargetingReason.NONE) {
                // Use getActionAttachedToCard so card being played is used
                updatedTargetFilters = Filters.and(updatedTargetFilters, Filters.canBeTargetedBy(getActionAttachedToCard(), getActionAttachedToCardBuiltIn(), targetingReason));
            }
            targetFiltersMap.put(targetingReason, updatedTargetFilters);
        }
    }

    /**
     * Gets all the cards targeted by the action, including the targeting reasons.
     *
     * @return the map of targeted cards by targetGroupId
     */
    @Override
    public final Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> getAllPrimaryTargetCards() {
        return _targetGroupMap;
    }

    /**
     * Adds target filter defining the cards targeted as secondary targets.
     *
     * @param targetFilter the target filter.
     */
    @Override
    public final void addSecondaryTargetFilter(Filterable targetFilter) {
        _secondaryTargetFiltersList.add(Filters.and(targetFilter, Filters.canBeTargetedBy(getActionAttachedToCard())));
    }

    /**
     * Gets all the secondary cards targeted by the action.
     * @param game the game
     * @return the list of secondary target cards
     */
    @Override
    public final List<PhysicalCard> getAllSecondaryTargetCards(SwccgGame game) {
        List<PhysicalCard> secondaryTargets = new ArrayList<PhysicalCard>();
        if (!_secondaryTargetFiltersList.isEmpty()) {
            Filter secondaryTargetFilter = Filters.none;
            for (Filterable filterable : _secondaryTargetFiltersList) {
                secondaryTargetFilter = Filters.or(filterable, secondaryTargetFilter);
            }
            secondaryTargets.addAll(Filters.filterActive(game, getActionAttachedToCard(), secondaryTargetFilter));
        }
        return secondaryTargets;
    }

    /**
     * Adds a group of cards to animate together as a group. The group of cards will be animated after the previous group
     * of cards specified to be animated with addAnimationGroup.
     * @param cards the cards to animate as a group
     */
    @Override
    public final void addAnimationGroup(PhysicalCard... cards) {
        addAnimationGroup(Arrays.asList(cards));
    }

    /**
     * Adds a group of cards to animate together as a group. The group of cards will be animated after the previous group
     * of cards specified to be animated with addAnimationGroup.
     * @param cards the cards to animate as a group
     */
    @Override
    public final void addAnimationGroup(Collection<PhysicalCard> cards) {
        _animationGroupList.add(cards);
    }

    /**
     * Determines if all the effects of the action were carried out successfully.
     * @return true if all the effects of the action were carried out successfully, otherwise false
     */
    @Override
    public boolean wasCarriedOut() {
        if (isAnyCostFailed())
            return false;
        if (!_effects.isEmpty())
            return false;
        for (Effect effect : _processedEffects) {
            if (!effect.wasCarriedOut())
                return false;
        }
        // Do not check "afterEffects", since they do not factor into whether this action was carried out.
        return true;
    }

    public boolean isImmuneTo(String title) { return false;}
}
