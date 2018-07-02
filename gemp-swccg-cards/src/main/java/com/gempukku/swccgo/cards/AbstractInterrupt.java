package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.PlayChoiceAction;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The abstract class providing the common implementation for Interrupts.
 */
public abstract class AbstractInterrupt extends AbstractSwccgCardBlueprint {

    /**
     * Creates a blueprint for an Interrupt.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractInterrupt(Side side, Float destiny, String title, Uniqueness uniqueness) {
        super(side, destiny, title, uniqueness);
        setCardCategory(CardCategory.INTERRUPT);
        addCardType(CardType.INTERRUPT);
        addIcon(Icon.INTERRUPT);
    }

    /**
     * Determines if the card can be played.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param playCardOption the play card option, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return true if card can be played, otherwise false
     */
    @Override
    protected final boolean checkPlayRequirements(String playerId, SwccgGame game, PhysicalCard self, DeploymentRestrictionsOption deploymentRestrictionsOption, PlayCardOption playCardOption, ReactActionOption reactActionOption) {
        return super.checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, playCardOption, reactActionOption)
                && reactActionOption == null
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 0)
                && checkGameTextPlayRequirements(playerId, game, self);
    }

    /**
     * Determines if this type of card is deployed or played
     * @return true if card is "deployed", false if card is "played"
     */
    @Override
    public final boolean isCardTypeDeployed() {
        return false;
    }

    /**
     * Gets the top-level actions that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    @Override
    public final List<Action> getTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = super.getTopLevelActions(playerId, game, self);

        if (checkPlayRequirements(playerId, game, self, null, null, null)) {
            List<PlayInterruptAction> actionList1 = getGameTextTopLevelActions(playerId, game, self);
            if (actionList1 != null) {
                actions.addAll(actionList1);
            }

            // Check if interrupt was granted ability to perform a top-level action from another card
            List<PlayCardAction> actionList2 = getGrantedTopLevelActions(playerId, game, self);
            if (actionList2 != null) {
                actions.addAll(actionList2);
            }
        }

        return actions;
    }

    /**
     * Gets the top-level actions that can be performed by the specified player during an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    @Override
    public List<Action> getTopLevelAttackRunActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = super.getTopLevelAttackRunActions(playerId, game, self);

        if (checkPlayRequirements(playerId, game, self, null, null, null)) {
            List<PlayInterruptAction> actionList1 = getGameTextTopLevelAttackRunActions(playerId, game, self);
            if (actionList1 != null) {
                actions.addAll(actionList1);
            }
        }

        return actions;
    }

    /**
     * Gets the top-level actions that this card has been granted by other cards.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    private List<PlayCardAction> getGrantedTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayCardAction> actionList = new LinkedList<PlayCardAction>();

        List<PlayInterruptAction> initiateEpicDuelActions = game.getModifiersQuerying().getInitiateEpicDuelActions(game.getGameState(), self);
        if (initiateEpicDuelActions != null) {
            actionList.addAll(initiateEpicDuelActions);
        }

        // Check condition(s)
        Collection<PhysicalCard> grantedToBeCanceledByList = Filters.filterActive(game, self,
                Filters.and(Filters.grantedToBeCanceledBy(self), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_CANCELED)));
        for (PhysicalCard grantedToBeCanceledBy : grantedToBeCanceledByList) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, grantedToBeCanceledBy, grantedToBeCanceledBy.getTitle());
            actionList.add(action);
        }

        return actionList;
    }

    /**
     * Gets the optional "before" actions for the specified effect that can be performed by the specified player. This includes
     * actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<Action> getOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();

        if (checkPlayRequirements(playerId, game, self, null, null, null)) {
            List<PlayInterruptAction> actionList1 = getGameTextOptionalBeforeActions(playerId, game, effect, self);
            if (actionList1 != null) {
                actions.addAll(actionList1);
            }

            // Check if interrupt was granted ability to perform a "before" action by another card
            List<PlayCardAction> actionList2 = getGrantedBeforeActions(playerId, game, effect, self);
            if (actionList2 != null) {
                actions.addAll(actionList2);
            }
        }

        return actions;
    }

    /**
     * Gets the optional "before" actions for the specified effect that can be performed by the specified player. This includes
     * actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    private List<PlayCardAction> getGrantedBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayCardAction> actions = new LinkedList<PlayCardAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.any)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (cardBeingPlayed != null
                    && game.getModifiersQuerying().mayPlayInterruptToCancelCard(game.getGameState(), self, cardBeingPlayed)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
                actions.add(action);
            }
        }

        return actions;
    }

    /**
     * Gets the optional "after" actions for the specified effect result that can be performed by the specified player.
     * This includes actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<Action> getOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();

        if (checkPlayRequirements(playerId, game, self, null, null, null)) {
            List<PlayInterruptAction> actionList1 = getGameTextOptionalAfterActions(playerId, game, effectResult, self);
            if (actionList1 != null) {
                actions.addAll(actionList1);
            }

            // Check if interrupt was granted ability to perform an "after" action by another card
            List<PlayCardAction> actionList2 = getGrantedAfterActions(playerId, game, effectResult, self);
            if (actionList2 != null) {
                actions.addAll(actionList2);
            }
        }

        return actions;
    }

    /**
     * Gets the optional "after" actions for the specified effect result that this card has been granted by other cards.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    private List<PlayCardAction> getGrantedAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<PlayCardAction> actions = new LinkedList<PlayCardAction>();

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiated(game, effectResult)
                && GameConditions.canCancelForceDrain(game, playerId, self)) {
            PhysicalCard forceDrainLocation = game.getGameState().getForceDrainLocation();
            if (forceDrainLocation != null
                    && game.getModifiersQuerying().mayPlayInterruptToCancelForceDrain(game.getGameState(), self, forceDrainLocation)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Cancel Force drain");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new CancelForceDrainEffect(action));
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }

    /**
     * Gets the action playing this card as a starting interrupt.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action
     */
    @Override
    public final PlayCardAction getStartingInterruptAction(String playerId, SwccgGame game, PhysicalCard self) {
        return getGameTextStartingAction(playerId, game, self);
    }

    /**
     * Gets the play card action for the card if it can be played. If the card can be played in multiple ways, then
     * this will return an action that has the player choose which way to play the card, then that plays the card that
     * way.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if playing card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously, or null
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param deployTargetFilter the filter for where the card can be played
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null         @return the play card action
     */
    @Override
    public final PlayCardAction getPlayCardAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions) {
        List<PlayCardAction> playCardActions = getPlayCardActions(playerId, game, self, sourceCard, forFree, changeInCost, deploymentOption, deploymentRestrictionsOption, null, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deployTargetFilter, specialLocationConditions);
        if (playCardActions.isEmpty())
            return null;

        if (playCardActions.size() == 1)
            return playCardActions.get(0);

        return new PlayChoiceAction(playerId, sourceCard, self, playCardActions);
    }

    /**
     * Gets the action playing this Interrupt in response to an effect or an effect result.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the action
     * @param effect the effect to response to
     * @param effectResult the effect result to response to
     * @return the action
     */
    @Override
    public PlayCardAction getPlayInterruptAsResponseAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, Effect effect, EffectResult effectResult) {
        if (!checkPlayRequirements(playerId, game, self, null, null, null))
            return null;

        List<PlayCardAction> playCardActions = new LinkedList<PlayCardAction>();

        if (effect != null) {
            List<PlayInterruptAction> actionList1 = getGameTextOptionalBeforeActions(playerId, game, effect, self);
            if (actionList1 != null) {
                playCardActions.addAll(actionList1);
            }

            // Check if interrupt was granted ability to perform a "before" action by another card
            List<PlayCardAction> actionList2 = getGrantedBeforeActions(playerId, game, effect, self);
            if (actionList2 != null) {
                playCardActions.addAll(actionList2);
            }
        }
        else if (effectResult != null) {
            List<PlayInterruptAction> actionList1 = getGameTextOptionalAfterActions(playerId, game, effectResult, self);
            if (actionList1 != null) {
                playCardActions.addAll(actionList1);
            }

            // Check if interrupt was granted ability to perform an "after" action by another card
            List<PlayCardAction> actionList2 = getGrantedAfterActions(playerId, game, effectResult, self);
            if (actionList2 != null) {
                playCardActions.addAll(actionList2);
            }
        }

        if (playCardActions.isEmpty())
            return null;

        if (playCardActions.size() == 1)
            return playCardActions.get(0);

        return new PlayChoiceAction(playerId, sourceCard, self, playCardActions);
    }

    /**
     * Gets the play card actions for each way the card can be played by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if playing card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously, or null
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param deployTargetFilter the filter for where the card can be played
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null         @return the play card actions
     */
    @Override
    public List<PlayCardAction> getPlayCardActions(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions) {
        if (!checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, null, reactActionOption))
            return Collections.emptyList();

        List<PlayCardAction> playCardActions = new LinkedList<PlayCardAction>();

        if (game.getGameState().isDuringAttackRun()) {
            List<PlayInterruptAction> actionList1 = getGameTextTopLevelAttackRunActions(playerId, game, self);
            if (actionList1 != null) {
                playCardActions.addAll(actionList1);
            }
        }
        else {
            List<PlayInterruptAction> actionList1 = getGameTextTopLevelActions(playerId, game, self);
            if (actionList1 != null) {
                playCardActions.addAll(actionList1);
            }

            // Check if interrupt was granted ability to perform a top-level action from another card
            List<PlayCardAction> actionList2 = getGrantedTopLevelActions(playerId, game, self);
            if (actionList2 != null) {
                playCardActions.addAll(actionList2);
            }
        }

        return playCardActions;
    }

    /**
     * Gets the required "before" triggers when the specified Interrupt itself is being played.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredInterruptPlayedTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        List<RequiredGameTextTriggerAction> actionList1 = getGameTextRequiredInterruptPlayedTriggers(game, effect, self);
        if (actionList1 != null) {
            actions.addAll(actionList1);
        }

        return actions;
    }

    /**
     * Gets the required "before" triggers for the specified effect.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        // Not used for Interrupts
        return null;
    }

    /**
     * Gets the optional "before" triggers for the specified effect that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Not used for Interrupts
        return null;
    }

    /**
     * Gets the required "after" triggers when the specified card is drawn for destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getRequiredDrawnAsDestinyTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Actions from game text
        List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredDrawnAsDestinyTriggers(game, effectResult, self, self.getCardId());
        if (gameTextActions != null) {
            actions.addAll(gameTextActions);
        }

        return actions;
    }

    /**
     * Gets the optional "after" triggers when the specified card is drawn for destiny that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getOptionalDrawnAsDestinyTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Actions from game text
        List<OptionalGameTextTriggerAction> gameTextActions = getGameTextOptionalDrawnAsDestinyTriggers(playerId, game, effectResult, self, self.getCardId());
        if (gameTextActions != null) {
            actions.addAll(gameTextActions);
        }

        return actions;
    }

    /**
     * Gets the optional "after" triggers for the specified effect result that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        // Not used for Interrupts
        return null;
    }

    /**
     * Gets the required "after" triggers for the specified effect result.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        // Not used for Interrupts
        return null;
    }

    /**
     * This method is overridden by individual cards to specify card play requirements.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true if requirements met, otherwise false
     */
    protected boolean checkGameTextPlayRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return true;
    }

    /**
     * This method is overridden by individual cards to specify a play as starting interrupt action.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    protected PlayInterruptAction getGameTextStartingAction(String playerId, SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions, or null
     */
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions during an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions, or null
     */
    protected List<PlayInterruptAction> getGameTextTopLevelAttackRunActions(String playerId, SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "before" play card actions to the specified
     * effect that can be performed by the specified player. This includes actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the play card actions, or null
     */
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" play card actions to the specified effect
     * result that can be performed by the specified player. This includes actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the play card actions, or null
     */
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify a required trigger when the Interrupt itself is played.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredInterruptPlayedTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers when the card is drawn for destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredDrawnAsDestinyTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" triggers when the card is drawn for destiny
     * that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalDrawnAsDestinyTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }
}
