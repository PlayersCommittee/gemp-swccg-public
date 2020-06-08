package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.EscortingCaptiveCondition;
import com.gempukku.swccgo.cards.conditions.PlayersTurnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.InPlayDataAsFloatEvaluator;
import com.gempukku.swccgo.cards.evaluators.SubtractEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseOpponentsForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Epic Event
 * Title: If The Trace Was Correct
 */
public class Card11_080 extends AbstractEpicEventDeployable {
    public Card11_080() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.If_The_Trace_Was_Correct, Uniqueness.UNIQUE);
        setGameText("Deploy on I Will Find Them Quickly, Master. Once during each of your deploy phases may deploy a Sith Probe Droid from Reserve Deck; reshuffle. If Amidala forfeited at same site as Maul, she is captured instead of lost. While Maul escorting Amidala, opponent's battle destiny draws are -1 and during your turn you may use up to 3 Force in opponent's Force Pile. While Maul escorting Amidala at a non-Tatooine battleground site, opponent's Force drains at Tatooine sites are -1 and at the end of every turn, may reshuffle opponent's Reserve Deck.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.I_Will_Find_Them_Quickly_Master;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.IF_THE_TRACE_WAS_CORRECT__DOWNLOAD_SITH_PROBE_DROID;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Sith Probe Droid from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Sith_Probe_Droid, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        TargetingReason targetingReason = TargetingReason.TO_BE_CAPTURED;
        Filter amidalaFilter = Filters.and(Filters.Amidala, Filters.canBeTargetedBy(self, targetingReason), Filters.at(Filters.sameSiteAs(self, Filters.Maul)));

        // Check condition(s)
        if (TriggerConditions.isAboutToBeForfeited(game, effectResult, amidalaFilter)) {
            final AboutToForfeitCardFromTableResult result = (AboutToForfeitCardFromTableResult) effectResult;
            final PhysicalCard cardToBeForfeited = result.getCardToBeForfeited();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Capture " + GameUtils.getFullName(cardToBeForfeited));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Amidala", targetingReason, cardToBeForfeited) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Capture " + GameUtils.getCardLink(targetedCard),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            result.getForfeitCardEffect().preventEffectOnCard(finalTarget);
                                            action.appendEffect(
                                                    new RestoreCardToNormalEffect(action, finalTarget));
                                            action.appendEffect(
                                                    new CaptureCharacterOnTableEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition maulEscortingAmidala = new EscortingCaptiveCondition(self, Filters.Maul, Filters.Amidala);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, maulEscortingAmidala, -1, opponent));
        modifiers.add(new MayUseOpponentsForceModifier(self, new AndCondition(new PlayersTurnCondition(playerId), maulEscortingAmidala),
                new SubtractEvaluator(3, new InPlayDataAsFloatEvaluator(self)), playerId));
        modifiers.add(new ForceDrainModifier(self, Filters.Tatooine_site, new EscortingCaptiveCondition(self, Filters.and(Filters.Maul,
                Filters.at(Filters.and(Filters.non_Tatooine_location, Filters.battleground_site))), Filters.Amidala), -1, opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isUsingForce(game, effect, playerId)
                && GameConditions.isDuringYourTurn(game, self)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Maul, Filters.escorting(Filters.Amidala))))  {
            final UseForceEffect useForceEffect = (UseForceEffect) effect;
            final int maxForceToUseViaCard = game.getModifiersQuerying().getMaxOpponentsForceToUseViaCard(game.getGameState(), playerId, self, useForceEffect.getAmountForOpponentToUse(), 0);
            if (maxForceToUseViaCard > 0) {
                final int maxForceToUse = Math.min(maxForceToUseViaCard, useForceEffect.getTotalAmountOfForceToUse());

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setRepeatableTrigger(true);
                action.setText("Use opponent's Force");
                // Perform result(s)
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of opponent's Force to use", 1, maxForceToUse, maxForceToUse) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        final int validatedResult = Math.min(maxForceToUse, result);
                                        useForceEffect.setAmountForOpponentToUse(useForceEffect.getAmountForOpponentToUse() + validatedResult);
                                        Float forceUsed = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getFloatValue() : null;
                                        if (forceUsed == null)
                                            forceUsed = (float) validatedResult;
                                        else
                                            forceUsed += validatedResult;
                                        self.setWhileInPlayData(new WhileInPlayData(forceUsed));
                                    }
                                }
                        )
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isUsingForce(game, effect, playerId)
                && GameConditions.isDuringYourTurn(game, self)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Maul, Filters.escorting(Filters.Amidala)))) {
            final UseForceEffect useForceEffect = (UseForceEffect) effect;
            int minOpponentForceToUse = Math.max(0, useForceEffect.getTotalAmountOfForceToUse() - game.getGameState().getForcePile(playerId).size());
            if (minOpponentForceToUse > 0) {
                final int maxForceToUseViaCard = game.getModifiersQuerying().getMaxOpponentsForceToUseViaCard(game.getGameState(), playerId, self, useForceEffect.getAmountForOpponentToUse(), minOpponentForceToUse);
                if (maxForceToUseViaCard > 0) {
                    final int maxForceToUse = Math.min(maxForceToUseViaCard, useForceEffect.getTotalAmountOfForceToUse());

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setRepeatableTrigger(true);
                    action.setText("Use opponent's Force");
                    // Perform result(s)
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, playerId,
                                    new IntegerAwaitingDecision("Choose amount of opponent's Force to use", 1, maxForceToUse, maxForceToUse) {
                                        @Override
                                        public void decisionMade(int result) throws DecisionResultInvalidException {
                                            final int validatedResult = Math.min(maxForceToUse, result);
                                            useForceEffect.setAmountForOpponentToUse(useForceEffect.getAmountForOpponentToUse() + validatedResult);
                                            Float forceUsed = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getFloatValue() : null;
                                            if (forceUsed == null)
                                                forceUsed = (float) validatedResult;
                                            else
                                                forceUsed += validatedResult;
                                            self.setWhileInPlayData(new WhileInPlayData(forceUsed));
                                        }
                                    }
                            )
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)
                && GameConditions.hasReserveDeck(game, opponent)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Maul, Filters.escorting(Filters.Amidala),
                Filters.at(Filters.and(Filters.non_Tatooine_location, Filters.battleground_site))))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Shuffle opponent's Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, opponent));
            return Collections.singletonList(action);
        }
        return null;
    }
}