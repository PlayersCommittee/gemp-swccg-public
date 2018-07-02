package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.FailCostEffect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;
import com.gempukku.swccgo.logic.timing.rules.JediTestAttemptRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * The abstract class providing the common implementation for Jedi Tests.
 */
public abstract class AbstractJediTest extends AbstractDeployable {

    /**
     * Creates a blueprint for a Jedi Test.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     */
    protected AbstractJediTest(Side side, float destiny, String title) {
        this(side, destiny, PlayCardZoneOption.ATTACHED, title);
    }

    /**
     * Creates a blueprint for a Jedi Test.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     */
    protected AbstractJediTest(Side side, float destiny, PlayCardZoneOption playCardZoneOption, String title) {
        super(side, destiny, playCardZoneOption, 0f, title);
        setCardCategory(CardCategory.JEDI_TEST);
        addCardType(CardType.JEDI_TEST);
        addIcon(Icon.JEDI_TEST);
    }

    /**
     * Gets effects (to be performed in order) that set any targeted cards when the card is being deployed.
     * @param action the action to perform the effect
     * @param playerId the performing player
     * @param game the game
     * @param self the card
     * @param target the target to where the card is being deployed, or null (if side of table or card pile)
     * @param playCardOption the play card option chosen
     * @return the targeting effects, or null
     */
    @Override
    public List<TargetingEffect> getTargetCardsWhenDeployedEffects(final Action action, final String playerId, final SwccgGame game, final PhysicalCard self, final PhysicalCard target, PlayCardOption playCardOption) {
        final GameState gameState = game.getGameState();

        List<TargetingEffect> targetingEffects = new LinkedList<TargetingEffect>();

        if (targetsMentor()) {
            // Target mentor
            final Filter mentorTargetFilter = getValidJediTestMentorTargetFilter(playerId, game, self, target);

            final TargetingEffect targetingEffect = new TargetCardOnTableEffect(action, playerId, "Choose mentor", mentorTargetFilter) {
                @Override
                protected void cardTargeted(int targetGroupId1, final PhysicalCard mentor) {
                    action.addAnimationGroup(mentor);
                    self.setTargetedCard(TargetId.JEDI_TEST_MENTOR, targetGroupId1, mentor, mentorTargetFilter);

                    // Check if a valid apprentice to target can be found
                    final Filter apprenticeToTargetFilter = getValidApprenticeFilter(playerId, game, self, target, mentor, false);
                    boolean canTargetApprentice = Filters.canSpot(game, self, apprenticeToTargetFilter);

                    // Check if a valid apprentice to deploy from hand can be found
                    final Filter apprenticeToDeployFilter = getValidApprenticeFilter(playerId, game, self, target, mentor, true);
                    boolean canDeployApprentice = mayDeployApprenticeToSameLocationFromHandDuringTargeting() && Filters.canSpot(gameState.getHand(playerId), game, apprenticeToDeployFilter);

                    if (canTargetApprentice && canDeployApprentice) {
                        action.appendTargeting(
                                new PlayoutDecisionEffect(action, playerId,
                                        new MultipleChoiceAwaitingDecision("Choose how to target apprentice", new String[]{"Target an apprentice on table", "Deploy an apprentice from hand"}) {
                                            @Override
                                            protected void validDecisionMade(int index, String result) {
                                                if (index == 0) {
                                                    action.appendTargeting(
                                                            new TargetCardOnTableEffect(action, playerId, "Choose apprentice", apprenticeToTargetFilter) {
                                                                @Override
                                                                protected void cardTargeted(int targetGroupId2, PhysicalCard apprentice) {
                                                                    action.addAnimationGroup(apprentice);
                                                                    self.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, targetGroupId2, apprentice, apprenticeToTargetFilter);
                                                                    gameState.addApprentice(apprentice);
                                                                }
                                                            }
                                                    );
                                                } else {
                                                    action.appendTargeting(
                                                            new DeployCardToLocationFromHandEffect(action, playerId, apprenticeToDeployFilter, Filters.sameLocation(target), false, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions()) {
                                                                @Override
                                                                protected void cardDeployed(final PhysicalCard deployedApprentice) {
                                                                    action.appendTargeting(
                                                                            new TargetCardOnTableEffect(action, playerId, "Choose apprentice", deployedApprentice) {
                                                                                @Override
                                                                                protected void cardTargeted(int targetGroupId2, PhysicalCard apprentice) {
                                                                                    action.addAnimationGroup(apprentice);
                                                                                    self.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, targetGroupId2, apprentice, Filters.or(deployedApprentice, apprenticeToTargetFilter));
                                                                                    gameState.addApprentice(apprentice);
                                                                                }
                                                                            });
                                                                }

                                                                @Override
                                                                public String getChoiceText() {
                                                                    return "Choose apprentice to deploy";
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                        }
                                )
                        );
                    } else if (canTargetApprentice) {
                        action.appendTargeting(
                                new TargetCardOnTableEffect(action, playerId, "Choose apprentice", apprenticeToTargetFilter) {
                                    @Override
                                    protected void cardTargeted(int targetGroupId2, PhysicalCard apprentice) {
                                        action.addAnimationGroup(apprentice);
                                        self.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, targetGroupId2, apprentice, apprenticeToTargetFilter);
                                        gameState.addApprentice(apprentice);
                                    }
                                }
                        );
                    } else if (canDeployApprentice) {
                        action.appendTargeting(
                                new DeployCardToLocationFromHandEffect(action, playerId, apprenticeToDeployFilter, Filters.sameLocation(target), false, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions()) {
                                    @Override
                                    protected void cardDeployed(final PhysicalCard deployedApprentice) {
                                        action.appendTargeting(
                                                new TargetCardOnTableEffect(action, playerId, "Choose apprentice", deployedApprentice) {
                                                    @Override
                                                    protected void cardTargeted(int targetGroupId2, PhysicalCard apprentice) {
                                                        action.addAnimationGroup(apprentice);
                                                        self.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, targetGroupId2, apprentice, Filters.sameCardId(deployedApprentice));
                                                        gameState.addApprentice(apprentice);
                                                    }
                                                });
                                    }

                                    @Override
                                    public String getChoiceText() {
                                        return "Choose apprentice to deploy";
                                    }
                                }
                        );
                    } else {
                        action.appendTargeting(
                                new FailCostEffect(action));
                    }
                }
            };
            targetingEffects.add(targetingEffect);
        }
        else {
            // Target apprentice
            final Filter targetFilter = getValidJediTestApprenticeTargetFilter(playerId, game, self, null, null, false);

            final TargetingEffect targetingEffect = new TargetCardOnTableEffect(action, playerId, "Choose apprentice", targetFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId2, PhysicalCard apprentice) {
                            action.addAnimationGroup(apprentice);
                            self.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, targetGroupId2, apprentice, targetFilter);
                            gameState.addApprentice(apprentice);
                        }
                    };
            targetingEffects.add(targetingEffect);
        }

        return targetingEffects;
    }

    /**
     * Gets a filter for the cards that can be the mentor for the specified Jedi Test.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Jedi Test on
     * @return the filter
     */
    @Override
    public Filter getValidJediTestMentorTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget) {
        GameState gameState = game.getGameState();
        List<PhysicalCard> validMentors = new ArrayList<PhysicalCard>();

        Filter mentorFilter = getValidMentorFilter(playerId, game, self, deployTarget);
        Collection<PhysicalCard> possibleMentors = Filters.filterActive(game, self, mentorFilter);

        for (PhysicalCard possibleMentor : possibleMentors) {
            Filter apprenticeFilter = getValidApprenticeFilter(playerId, game, self, deployTarget, possibleMentor, false);
            // Check if a valid apprentice can be found
            if (Filters.canSpot(game, self, apprenticeFilter)) {
                validMentors.add(possibleMentor);
            }
            // Check if a valid apprentice to deploy from hand can be found
            else if (mayDeployApprenticeToSameLocationFromHandDuringTargeting()) {
                Filter apprenticeToDeployFilter = getValidApprenticeFilter(playerId, game, self, deployTarget, possibleMentor, true);
                if (Filters.canSpot(gameState.getHand(playerId), game, apprenticeToDeployFilter)) {
                    validMentors.add(possibleMentor);
                }
            }
        }
        return Filters.in(validMentors);
    }

    /**
     * Gets the filter for a valid mentor for this Jedi Test.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the Utinni Effect target ids
     */
    protected Filter getValidMentorFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget) {
        return Filters.and(Filters.your(playerId), Filters.character, Filters.abilityMoreThan(2),
                Filters.not(Filters.or(Filters.mentorTargetedByJediTest(Filters.uncompleted_Jedi_Test), Filters.apprentice)),
                getGameTextValidMentorFilter(playerId, game, self, deployTarget));
    }

    /**
     * Gets a filter for the cards that can be the apprentice for the specified Jedi Test.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Jedi Test on
     * @param mentor the mentor for the apprentice
     * @param isDeployFromHand true if for an apprentice being deployed from hand
     * @return the filter
     */
    @Override
    public Filter getValidJediTestApprenticeTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand) {
        return getValidApprenticeFilter(playerId, game, self, deployTarget, mentor, isDeployFromHand);
    }

    /**
     * Gets the filter for a valid mentor for this Jedi Test.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Jedi Test on
     * @param mentor the mentor for the apprentice
     * @param isDeployFromHand true if for an apprentice being deployed from hand
     * @return the Utinni Effect target ids
     */
    protected Filter getValidApprenticeFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand) {
        int jediTestNumber = game.getModifiersQuerying().getJediTestNumber(game.getGameState(), self);
        Filter currentJediTestFilter = (jediTestNumber == 6 ? Filters.Jedi_Test_6 : (jediTestNumber == 5 ? Filters.Jedi_Test_5 : (jediTestNumber == 4 ? Filters.Jedi_Test_4 :
                (jediTestNumber == 3 ? Filters.Jedi_Test_3 : (jediTestNumber == 2 ? Filters.Jedi_Test_2 : (jediTestNumber == 1 ? Filters.Jedi_Test_1 : Filters.none))))));
        Filter filter = Filters.and(Filters.your(playerId), Filters.character, Filters.not(Filters.or(Filters.droid, Filters.Jedi,
                Filters.mentorTargetedByJediTest(Filters.uncompleted_Jedi_Test), Filters.apprenticeTargetedByJediTest(Filters.uncompleted_Jedi_Test),
                Filters.apprenticeTargetedByJediTest(currentJediTestFilter))),
                getGameTextValidApprenticeFilter(playerId, game, self, deployTarget, mentor, isDeployFromHand));
        if (targetsMentor()) {
            float abilityOfMentor = game.getModifiersQuerying().getAbility(game.getGameState(), mentor);
            filter = Filters.and(filter, Filters.not(Filters.abilityMoreThanOrEqualTo(abilityOfMentor)));
        }
        if (isDeployFromHand) {
            filter = Filters.and(filter, Filters.deployableToLocation(self, Filters.sameCardId(deployTarget), false, false, 0, null, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), null));
        }
        return filter;
    }

    /**
     * This method is overridden by individual cards to specify the filter for valid mentor targets.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Jedi Test on
     * @return the filter
     */
    protected Filter getGameTextValidMentorFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget) {
        return Filters.any;
    }

    /**
     * This method is overridden by individual cards to specify the filter for valid apprentice targets.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Jedi Test on
     * @param mentor the mentor for the apprentice
     * @param isDeployFromHand true if for an apprentice being deployed from hand
     * @return the filter
     */
    protected Filter getGameTextValidApprenticeFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand) {
        return Filters.any;
    }

    /**
     * Determines if the apprentice may be deployed to same location from had during targeting.
     * @return true or false
     */
    protected boolean mayDeployApprenticeToSameLocationFromHandDuringTargeting() {
        return false;
    }

    /**
     * Determines if targets a mentor.
     * @return true or false
     */
    protected boolean targetsMentor() {
        return true;
    }

    /**
     * Gets modifiers from the card that are in effect while the card is in play (unless game text is canceled).
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getWhileInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = super.getWhileInPlayModifiers(game, self);

        // Set persistent, so these modifiers are in effect even when cards text is suspended/canceled
        final int permCardId = self.getPermanentCardId();
        Modifier modifier = new SuspendsCardModifier(self, self,
                new Condition() {
                    @Override
                    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                        PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                        // Suspend the Jedi Test if any of its targets (except mentor if Jedi Test is complete) are not active on table
                        boolean canSpotApprenticeFromAll = Filters.canSpotFromAllOnTable(game, Filters.apprenticeTargetedByJediTest(self));
                        if (!canSpotApprenticeFromAll
                                && game.getModifiersQuerying().isJediTestSuspendedInsteadOfLost(game.getGameState(), self)) {
                            return true;
                        }
                        if (canSpotApprenticeFromAll
                                && !Filters.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.apprenticeTargetedByJediTest(self))) {
                            return true;
                        }
                        if (targetsMentor() && self.getJediTestStatus() != JediTestStatus.COMPLETED) {
                            boolean canSpotMentorFromAll = Filters.canSpotFromAllOnTable(game, Filters.mentorTargetedByJediTest(self));
                            if (!canSpotMentorFromAll
                                    && game.getModifiersQuerying().isJediTestSuspendedInsteadOfLost(game.getGameState(), self)) {
                                return true;
                            }
                            if (canSpotMentorFromAll
                                    && !Filters.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.mentorTargetedByJediTest(self))) {
                                return true;
                            }
                        }
                        return false;
                    }
                }) {
            @Override
            public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
                return self.isSuspended() ? "Suspended due to inactive Jedi Test target" : null;
            }};
        modifier.setPersistent(true);
        modifiers.add(modifier);
        return modifiers;
    }

    /**
     * Gets the required "after" triggers for the specified effect result.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = super.getRequiredAfterTriggers(game, effectResult, self);

        // Uncompleted Jedi Test is lost if mentor or apprentice leaves table
        if (self.getJediTestStatus() != JediTestStatus.COMPLETED) {
            Filter targetFilter = Filters.or(Filters.apprenticeTargetedByJediTest(self), Filters.mentorTargetedByJediTest(self));
            if (TriggerConditions.leavesTable(game, effectResult, targetFilter)
                    && !game.getModifiersQuerying().isJediTestSuspendedInsteadOfLost(game.getGameState(), self)) {

                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(new JediTestAttemptRule(), self);
                action.setSingletonTrigger(true);
                action.setText("Make " + GameUtils.getFullName(self) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, self));
                actions.add(action);
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
    public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = super.getOptionalAfterTriggers(playerId, game, effectResult, self);

        GameTextActionId gameTextActionId = GameTextActionId.JEDI_TEST__EXCHANGE_JEDI_TEST_IN_LOST_PILE;

        if (TriggerConditions.jediTestCompleted(game, effectResult, self)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, self.getCardId(), gameTextActionId);
            action.setText("Exchange card in hand for Jedi Test in Lost Pile");
            action.setActionMsg("Exchange a card in hand for a Jedi Test in Lost Pile");
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, Filters.any, Filters.Jedi_Test));
            actions.add(action);
        }
        return actions;
    }
}
