package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The interface which contains methods that all actions must implement.
 */
public interface Action extends Snapshotable<Action> {
    /**
     * The action types.
     */
    enum Type {
        GAME_PROCESS, RULE_TOP_LEVEL, RULE_TRIGGER, PLAY_CARD, GAME_TEXT_FIRE_WEAPON, GAME_TEXT_TOP_LEVEL, GAME_TEXT_TRIGGER, GAME_TEXT_MOVE_AS_REACT
    }

    /**
     * Gets the action type.
     * @return the action type
     */
    Type getType();

    /**
     * Gets the card that is the source of the action or null if the action from a game rule not from a specified card.
     * @return the card, or null
     */
    PhysicalCard getActionSource();

    /**
     * Gets the card that is the source of the action or the card that the action is attached to if the action comes
     * from a game rule.
     * @return the card, or null
     */
    PhysicalCard getActionAttachedToCard();

    /**
     * Gets the card built-in that is the source of the action or null if not associated with a card built-in.
     * @return the card built-in, or null
     */
    SwccgBuiltInCardBlueprint getActionAttachedToCardBuiltIn();

    /**
     * Sets an action as being an optional action from a card that may not on the table. This is used so the user
     * interface can show the image of the card that is off table, so the action can be select to be performed.
     * @param offTableCardAction true if action is an optional off table card action, otherwise false.
     */
    void setOptionalOffTableCardAction(boolean offTableCardAction);

    /**
     * Determines if this action is an optional action from a card that may not on the table. This is used so the user
     * interface can show the image of the card that is off table, so the action can be select to be performed.
     * @return true if action is an optional off table card action, otherwise false
     */
    boolean isOptionalOffTableCardAction();

    /**
     * Sets the player performing the action, or null if no player is specifically performing the action (such as a game rule).
     * @param playerId the player, or null
     */
    void setPerformingPlayer(String playerId);

    /**
     * Gets the player performing the action, or null if no player is specifically performing the action (such as a game
     * rule or required trigger action).
     * @return the player, or null
     */
    String getPerformingPlayer();

    /**
     * Determines if the action is from game text.
     * @return true if from game text, otherwise false
     */
    boolean isFromGameText();

    /**
     * Gets the card id of the card the game text is originally from
     * @return the card id
     */
    int getGameTextSourceCardId();

    /**
     * Gets the game text action id.
     * @return the game text action id
     */
    GameTextActionId getGameTextActionId();

    /**
     * Gets the text to show in the user interface to choose this action.
     * @return the text
     */
    String getText();

    /**
     * Sets if action may be aborted when choosing targets.
     * @param allowAbort true if action may be aborted when choosing targets, otherwise false
     */
    void setAllowAbort(boolean allowAbort);

    /**
     * Determines if action may be aborted when targets.
     * @return true if action be aborted when choosing targets, otherwise false
     */
    boolean isAllowAbort();

    /**
     * Determines if choosing targets is complete. If choosing targets is complete, then action cannot be aborted and
     * any message about the action being initiated can be sent.
     * @return true if choosing targets is complete, otherwise false.
     */
    boolean isChoosingTargetsComplete();

    /**
     * Gets the next effect to be performed as part of this action, or null if no effects remain.
     * @param game the game
     * @return the next effect to be performed as part of this action, or null if no effects remain
     */
    Effect nextEffect(SwccgGame game);

    /**
     * Gets the next after effect to be performed after this action, or null if no after effects remain.
     * @param game the game
     * @return the next after effect to be performed after this action, or null if no after effects remain
     */
    Effect nextAfterEffect(SwccgGame game);

    /**
     * Determines if all the effects of the action were carried out successfully.
     * @return true if all the effects of the action were carried out successfully, otherwise false
     */
    boolean wasCarriedOut();

    /**
     * Adds primary target cards to the the group with the target group id. The spotOverrides and targetFiltersMap define the
     * parameters used in the targeting, which can be needed if a response need to know what else would be a valid target.
     * @param text the text shown when selecting the targets
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param isTargetAll determines if all cards that can be targeted are automatically targeted
     * @param targetingType the type of targeting to use
     * @param targets the cards and the reasons each card is targeted
     * @param spotOverrides overrides which cards can be seen as "active" for this targeting
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return the target group id
     */
    int addPrimaryTargetCards(String text, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, boolean isTargetAll, TargetingType targetingType,
                              Map<PhysicalCard, Set<TargetingReason>> targets, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap);

    /**
     * Gets the text shown when choosing targets for the specified target group id.
     * @param targetGroupId the target group id
     * @return the text
     */
    String getPrimaryTargetingText(int targetGroupId);

    /**
     * Gets the minimum number of cards to target when choosing targets for the specified target group id.
     * @param targetGroupId the target group id
     * @return the text
     */
    Integer getPrimaryMinimumCardsToTarget(int targetGroupId);

    /**
     * Gets the maximum number of cards to target when choosing targets for the specified target group id.
     * @param targetGroupId the target group id
     * @return the text
     */
    Integer getPrimaryMaximumCardsToTarget(int targetGroupId);

    /**
     * Gets the the maximum number of times cards may be accepted by the filter for the specified target group id, which
     * will further limit cards that can be selected when cards with multiple model types accept filter multiple times.
     * @param targetGroupId the target group id
     * @return the text
     */
    Integer getPrimaryMaximumAcceptsCountToTarget(int targetGroupId);

    /**
     * Determines if card with multiple model types (i.e. squadrons) match if any model type matches the filter,
     * otherwise card only matches if all model types match the filter.
     * @param targetGroupId the target group id
     * @return true if card with multiple model types (i.e. squadrons) match if any model type matches the filter,
     * otherwise card only matches if all model types match the filter
     */
    Boolean getPrimaryTargetMatchPartialModelType(int targetGroupId);

    /**
     * Determines if all cards that can be targeted are automatically targeted.
     * @param targetGroupId the target group id
     * @return true if all cards that can be targeted are automatically targeted, otherwise false
     */
    Boolean getPrimaryTargetingAll(int targetGroupId);

    /**
     * Determines type of targeting used.
     * @param targetGroupId the target group id
     * @return the type of targeting used
     */
    TargetingType getPrimaryTargetingType(int targetGroupId);

    /**
     * Gets the cards in the group with the specified target group id.
     * @param targetGroupId the target group id
     * @return the cards
     */
    Collection<PhysicalCard> getPrimaryTargetCards(int targetGroupId);

    /**
     * Gets the card in the group with the specified target group id.
     * @param targetGroupId the target group id
     * @return the card
     */
    PhysicalCard getPrimaryTargetCard(int targetGroupId);

    /**
     * Updates the targets cards in the group with the specified target group id.
     * @param game the game
     * @param targetGroupId the target group id
     * @param targetCards the new targeted cards
     */
    void updatePrimaryTargetCards(SwccgGame game, int targetGroupId, Collection<PhysicalCard> targetCards);

    /**
     * Gets the spot overrides used in the targeting.
     * @param targetGroupId the target group id
     * @return the spot overrides
     */
    Map<InactiveReason, Boolean> getPrimaryTargetSpotOverrides(int targetGroupId);

    /**
     * Gets the target filter used in the targeting.
     * @param targetGroupId the target group id
     * @return the target filter
     */
    Map<TargetingReason, Filterable> getPrimaryTargetFilter(int targetGroupId);

    /**
     * Updates the target filter. For example, after all target groups have been targeted, the filters need to be updated
     * so if group is re-targeted, it is done in a valid combination with the other groups targeted.
     * @param targetGroupId the target group id
     * @param targetFilters the filter
     */
    void updatePrimaryTargetFilter(int targetGroupId, Filterable targetFilters);

    /**
     * Gets all the primary cards targeted by the action, including the targeting reasons.
     * @return the map of targeted cards by targetGroupId
     */
    Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> getAllPrimaryTargetCards();

    /**
     * Adds target filter defining the cards targeted as secondary targets.
     * @param targetFilter the target filter.
     */
    void addSecondaryTargetFilter(Filterable targetFilter);

    /**
     * Gets all the secondary cards targeted by the action.
     * @param game the game
     * @return the list of secondary target cards
     */
    List<PhysicalCard> getAllSecondaryTargetCards(SwccgGame game);

    /**
     * Adds a group of cards to animate together as a group. The group of cards will be animated after the previous group
     * of cards specified to be animated with addAnimationGroup.
     * @param cards the cards to animate as a group
     */
    void addAnimationGroup(PhysicalCard... cards);

    /**
     * Adds a group of cards to animate together as a group. The group of cards will be animated after the previous group
     * of cards specified to be animated with addAnimationGroup.
     * @param cards the cards to animate as a group
     */
    void addAnimationGroup(Collection<PhysicalCard> cards);

    /**
     * Appends the specified usage to the list of the costs. It will be executed after all the other costs currently in
     * the queue.
     *
     * @param cost the usage limit effect
     */
    void appendUsage(UsageEffect cost);

    /**
     * Appends the specified targeting to the list of the costs. It will be executed after all the other costs currently in
     * the queue.
     *
     * @param cost the target cards effect
     */
    void appendTargeting(TargetingEffect cost);

    /**
     * Appends the specified cost to the list of the costs. It will be executed after the other costs currently in
     * the queue, but before any added from appendCost().
     *
     * @param cost the cost
     */
    void appendBeforeCost(StandardEffect cost);

    /**
     * Appends the specified cost to the list of the costs. It will be executed after all the other costs currently in
     * the queue.
     *
     * @param cost the cost
     */
    void appendCost(StandardEffect cost);

    /**
     * Inserts the specified effects as the next effects to be executed.
     *
     * @param effect the effects
     */
    void insertEffect(StandardEffect... effect);

    /**
     * Appends the specified effect to the list of the effects. It will be executed after all the other costs currently
     * in the queue.
     *
     * @param effect the effect
     */
    void appendEffect(StandardEffect effect);

    /**
     * Inserts the specified effects as the next after effects to be executed.  It will be executed after all the other
     * effects currently in the queue. These effects do not need to be successful for the action to be considered carried out.
     *
     * @param effect the effects
     */
    void insertAfterEffect(StandardEffect... effect);

    /**
     * Appends the specified effect to the list of the after effects. It will be executed after all the other effects
     * currently in the queue. These effects do not need to be successful for the action to be considered carried out.
     *
     * @param effect the effect
     */
    void appendAfterEffect(StandardEffect effect);

    boolean isImmuneTo(String title);
}
