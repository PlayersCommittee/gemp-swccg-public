package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ProbedHiddenBasedResult;
import com.gempukku.swccgo.logic.timing.results.ProbedSystemResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Objective
 * Title: Hidden Base / Systems Will Slip Through Your Fingers
 */
public class Card7_136_BACK extends AbstractObjective {
    public Card7_136_BACK() {
        super(Side.LIGHT, 7, "Systems Will Slip Through Your Fingers");
        setGameText("While this side up, to draw a card from Force Pile, opponent must first use 1 Force. For each battleground system you control, you may cancel one opponent's Force drain (limit twice per turn). You may not deploy any systems. At each system opponent occupies during any deploy phase, opponent may 'probe' there by placing one card from hand face down beneath that system. Place out of play if 'Hidden Base' system is 'probed.' Dark side places 'probe' cards in Used Pile (and may retrieve 1 Force for each Probe Droid used to 'probe').");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, Filters.system, playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent)
                && GameConditions.canCancelForceDrain(game, self)) {
            final int numPerTurn = Math.min(2, Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground_system, Filters.controls(playerId))));
            if (GameConditions.isNumTimesPerTurn(game, self, playerId, numPerTurn, gameTextSourceCardId, gameTextActionId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Cancel Force drain");
                // Update usage limit(s)
                action.appendUsage(
                        new NumTimesPerTurnEffect(action, numPerTurn));
                // Perform result(s)
                action.appendEffect(
                        new CancelForceDrainEffect(action));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isEitherPlayersPhase(game, Phase.DEPLOY)
                && GameConditions.hasHand(game, playerId)) {
            Filter systemsFilter = Filters.and(Filters.system, Filters.occupies(playerId), Filters.not(Filters.hasStacked(Filters.probeCard)));
            if (GameConditions.canSpotLocation(game, systemsFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("'Probe' a system");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose system to 'probe'", systemsFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("'Probe' " + GameUtils.getCardLink(targetedCard),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ProbeSystemEffect(action, targetedCard));
                                            }
                                        }
                                );
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawCardFromForcePile(game, effectResult, opponent)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            // Perform result(s)
            action.appendEffect(
                    new UseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_4;

        // Check condition(s)
        if (TriggerConditions.systemProbed(game, effectResult)) {
            PhysicalCard systemProbed = ((ProbedSystemResult) effectResult).getSystemProbed();
            final PhysicalCard hiddenBaseIndicator = game.getGameState().getStackedCards(self).iterator().next();
            String hiddenBaseTitle = hiddenBaseIndicator.getBlueprint().getTitle();
            if (systemProbed.getBlueprint().getTitle().equals(hiddenBaseTitle)) {
                final Collection<PhysicalCard> probeCards = Filters.filter(game.getGameState().getAllStackedCards(), game, Filters.probeCard);
                final int probeDroidsUsedProbe = Filters.filter(probeCards, game, Filters.probe_droid).size();

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place out of play");
                action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
                // Perform result(s)
                action.appendEffect(
                        new SendMessageEffect(action, "'Hidden Base' system " + GameUtils.getCardLink(systemProbed) + " was 'probed'"));
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                // Just remove the card from the stacked zone and just let it just disappear (since it's "not in play")
                                game.getGameState().activatedCard(null, hiddenBaseIndicator);
                                game.getGameState().removeCardsFromZone(Collections.singleton(hiddenBaseIndicator));
                                game.getGameState().showCardOnScreen(hiddenBaseIndicator);
                            }
                        }
                );
                action.appendEffect(
                        new PlaceCardOutOfPlayFromTableEffect(action, self));
                action.appendEffect(
                        new PutStackedCardsInUsedPileEffect(action, opponent, probeCards, false));
                action.appendEffect(
                        new TriggeringResultEffect(action, new ProbedHiddenBasedResult(opponent)));
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                if (probeDroidsUsedProbe > 0) {
                                    action.appendEffect(
                                            new PlayoutDecisionEffect(action, opponent,
                                                    new YesNoDecision("Do you want to retrieve " + probeDroidsUsedProbe + " Force?") {
                                                        @Override
                                                        protected void yes() {
                                                            action.appendEffect(
                                                                    new RetrieveForceEffect(action, opponent, probeDroidsUsedProbe));
                                                        }
                                                    }
                                            )
                                    );
                                }
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}