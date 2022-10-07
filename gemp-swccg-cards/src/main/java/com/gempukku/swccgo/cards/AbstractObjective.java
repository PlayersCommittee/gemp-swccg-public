package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The abstract class providing the common implementation for Objective.
 */
public abstract class AbstractObjective extends AbstractNonLocationPlaysToTable {

    /**
     * Creates a blueprint for an Objective.
     * @param side the side of the Force
     * @param destiny the destiny value
     */
    protected AbstractObjective(Side side, float destiny, String title) {
        this(side, destiny,  title, null, null);
    }

    /**
     * Creates a blueprint for an Objective.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractObjective(Side side, float destiny, String title, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, null, title, null, expansionSet, rarity);
        setCardCategory(CardCategory.OBJECTIVE);
        addCardType(CardType.OBJECTIVE);
        addIcon(Icon.OBJECTIVE);
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
        return true;
    }

    /**
     * Determines if the card type, subtype, etc. always plays for free.
     * @return true if card type card type, subtype, etc. always plays for free, otherwise false
     */
    @Override
    protected final boolean isCardTypeAlwaysPlayedForFree() {
        return true;
    }

    /**
     * Determines if this type of card is deployed or played
     * @return true if card is "deployed", false if card is "played"
     */
    @Override
    public final boolean isCardTypeDeployed() {
        return true;
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
        List<Action> actions = new LinkedList<Action>();

        // Actions from game text
        List<TopLevelGameTextAction> gameTextActions;
        if (self.isObjectiveDeploymentComplete()) {

            gameTextActions = getGameTextTopLevelActions(playerId, game, self, self.getCardId());
            if (gameTextActions != null)
                actions.addAll(gameTextActions);
        }

        return actions;
    }

    /**
     * Gets the top-level actions that can be performed by the specified player (from opponent's card).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    @Override
    public final List<Action> getOpponentsCardTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();

        // Actions from game text
        List<TopLevelGameTextAction> gameTextActions;
        if (self.isObjectiveDeploymentComplete()) {

            gameTextActions = getOpponentsCardGameTextTopLevelActions(playerId, game, self, self.getCardId());
            if (gameTextActions != null)
                actions.addAll(gameTextActions);
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
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Trigger actions from game text
        List<RequiredGameTextTriggerAction> gameTextActions;
        if (self.isObjectiveDeploymentComplete()) {

            gameTextActions = getGameTextRequiredBeforeTriggers(game, effect, self, self.getCardId());
            if (gameTextActions != null)
                actions.addAll(gameTextActions);
        }

        return actions;

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
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Trigger actions from game text
        List<OptionalGameTextTriggerAction> gameTextActions;
        if (self.isObjectiveDeploymentComplete()) {

            gameTextActions = getGameTextOptionalBeforeTriggers(playerId, game, effect, self, self.getCardId());
            if (gameTextActions != null) {
                actions.addAll(gameTextActions);
            }
        }

        return actions;
    }

    /**
     * Gets the optional "before" triggers for the specified effect that can be performed by the specified player (from opponent's card).
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getOpponentsCardOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Trigger actions from game text
        List<OptionalGameTextTriggerAction> gameTextActions;
        if (self.isObjectiveDeploymentComplete()) {

            gameTextActions = getOpponentsCardGameTextOptionalBeforeTriggers(playerId, game, effect, self, self.getCardId());
            if (gameTextActions != null) {
                actions.addAll(gameTextActions);
            }
        }

        return actions;
    }

    @Override
    public final List<Action> getOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the required "after" triggers for the specified effect result.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Objective deployed
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = getGameTextWhenDeployedAction(self.getOwner(), game, self, self.getCardId());
            if (action != null) {
                action.skipInitialMessageAndAnimation();
                action.appendAfterEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                // Check that Objective is still in play
                                if (self.getZone().isInPlay()) {
                                    self.setObjectiveDeploymentComplete(true);
                                    game.getGameState().reapplyAffectingForCard(game, self);

                                    // After objective deployment complete
                                    RequiredGameTextTriggerAction afterDeploymentCompletedAction = getGameTextAfterDeploymentCompletedAction(self.getOwner(), game, self, self.getCardId());
                                    if (afterDeploymentCompletedAction != null) {
                                        afterDeploymentCompletedAction.skipInitialMessageAndAnimation();
                                        action.appendAfterEffect(new StackActionEffect(action, afterDeploymentCompletedAction));
                                    }
                                }

                            }
                        }
                );

                actions.add(action);
            }
        }
        else {
            // Trigger actions from game text
            List<RequiredGameTextTriggerAction> gameTextActions;
            if (self.isObjectiveDeploymentComplete()) {

                gameTextActions = getGameTextRequiredAfterTriggers(game, effectResult, self, self.getCardId());
                if (gameTextActions != null) {
                    actions.addAll(gameTextActions);
                }
            }
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
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Trigger actions from game text
        List<OptionalGameTextTriggerAction> gameTextActions;
        if (self.isObjectiveDeploymentComplete()) {

            gameTextActions = getGameTextOptionalAfterTriggers(playerId, game, effectResult, self, self.getCardId());
            if (gameTextActions != null) {
                actions.addAll(gameTextActions);
            }
        }

        return actions;
    }

    /**
     * Gets the optional "after" triggers for the specified effect result that can be performed by the specified player
     * (from opponent's card).
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getOpponentsCardOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Trigger actions from game text
        List<OptionalGameTextTriggerAction> gameTextActions;
        if (self.isObjectiveDeploymentComplete()) {

            gameTextActions = getOpponentsCardGameTextOptionalAfterTriggers(playerId, game, effectResult, self, self.getCardId());
            if (gameTextActions!=null) {
                actions.addAll(gameTextActions);
            }
        }

        return actions;
    }

    @Override
    public final List<Action> getOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets modifiers to the card itself that are always in effect.
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public final List<Modifier> getAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets modifiers from the card that are in effect while the card is in play (unless game text is canceled).
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public final List<Modifier> getWhileInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<Modifier>();

        // Modifiers from game text
        List<Modifier> modifiersFromGameText;
        if (self.isObjectiveDeploymentComplete()) {

            modifiersFromGameText = getGameTextWhileActiveInPlayModifiers(game, self);
            if (modifiersFromGameText != null) {
                modifiers.addAll(modifiersFromGameText);
            }
        }

        return modifiers;
    }

    //
    // This section defines common methods that are used by individual cards to define actions, etc.
    // that are defined by a card's game text. This is done to avoid a lot of super.foo() methods
    // from needing to be called in the individual cards.
    //

    /**
     * This method is overridden by individual cards to specify an action to perform when the objective is deployed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the action, or null
     */
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify an action that performs effects (namely "For remainder of game..." text)
     * that are triggers after deployment of this objective is successful. Effects included in this action will need to use
     * AddUntilEndOfGameModifierEffect and AddUntilEndOfGameActionProxyEffect.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the action, or null
     */
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    protected static ObjectiveDeployedTriggerAction deployCardWithObjectiveText(PhysicalCard self, Filter filterForCardToDeploy, final String chooseText) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(filterForCardToDeploy), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose " + chooseText + " to deploy";
                    }
                });
        return action;
    }

    protected static DeployCardFromReserveDeckEffect getDeployCardFromReserveDeckEffect(ObjectiveDeployedTriggerAction actions, Filter filterForCardToDeploy, final String chooseText) {
        return new DeployCardFromReserveDeckEffect(actions, Filters.and(filterForCardToDeploy), true, false) {
            @Override
            public String getChoiceText() {
                return chooseText;
            }
        };
    }

    protected static DeployCardToTargetFromReserveDeckEffect getDeployCardToTargetFromReserveDeckEffect(ObjectiveDeployedTriggerAction actions, Filter filterForCardToDeploy, Filter filterToDeployTo, final String chooseText) {
        return new DeployCardToTargetFromReserveDeckEffect(actions, Filters.and(filterForCardToDeploy), filterToDeployTo, true, false) {
            @Override
            public String getChoiceText() {
                return chooseText;
            }
        };
    }
}
