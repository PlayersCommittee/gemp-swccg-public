package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceRetrievalEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedInterruptEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CostToDrawDestinyCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Objective
 * Title: I Want That Map / And Now You'll Give It To Me
 */
public class Card208_057_BACK extends AbstractObjective {
    public Card208_057_BACK() {
        super(Side.DARK, 7, Title.And_Now_Youll_Give_It_To_Me, ExpansionSet.SET_8, Rarity.V);
        setGameText("While this side up, where you have a First Order leader, opponent must first use or lose 1 Force to draw a card for battle destiny. Unless BB-8 on table, opponent's Force retrieval is canceled. If Kylo on table: During your turn, may stack an Interrupt from your Lost Pile face up on I Will Finish What You Started; and once per turn, may play an Interrupt stacked there as if from hand (then place that card out of play). Flip this card if you do not occupy two battlegrounds or if a Resistance Agent is present at a battleground site.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_8);
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        if (self.getWhileInPlayData() != null) {
            PhysicalCard revealedResistanceAgent = self.getWhileInPlayData().getPhysicalCard();
            if (revealedResistanceAgent != null) {
                return "Resistance Agent is " + GameUtils.getCardLink(revealedResistanceAgent);
            }
            else {
                return "Resistance Agent is Luke";
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isCheckingCostsToDrawBattleDestiny(game, effectResult, opponent)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.First_Order_leader))) {
            final GameState gameState = game.getGameState();
            final CostToDrawDestinyCardResult costToDrawDestinyCardResult = (CostToDrawDestinyCardResult) effectResult;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add cost to draw battle destiny");
            action.setActionMsg("Make opponent use or lose 1 Force to draw a card for battle destiny");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(final SwccgGame game) {
                            if (GameConditions.canUseForce(game, opponent, 1)) {
                                // Ask player to Use 1 Force, Lose 1 Force, or neither
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new MultipleChoiceAwaitingDecision("Choose effect to draw card for battle destiny", new String[]{"Use 1 Force", "Lose 1 Force", "Neither"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {
                                                            gameState.sendMessage(opponent + " chooses to use 1 Force");
                                                            action.appendEffect(
                                                                    new UseForceEffect(action, opponent, 1));
                                                        } else if (index == 1) {
                                                            gameState.sendMessage(opponent + " chooses to lose 1 Force");
                                                            action.appendEffect(
                                                                    new LoseForceEffect(action, opponent, 1, true));
                                                        } else {
                                                            gameState.sendMessage(opponent + " chooses to neither use or lose 1 Force to draw a card for battle destiny");
                                                            costToDrawDestinyCardResult.costToDrawCardFailed(true);
                                                        }
                                                    }
                                                }
                                        )
                                );
                            } else {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new YesNoDecision("Do you want to lose 1 Force to draw a card for battle destiny?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new LoseForceEffect(action, opponent, 1, true));
                                                    }

                                                    @Override
                                                    protected void no() {
                                                        gameState.sendMessage(opponent + " chooses to not lose 1 Force to draw a card for battle destiny");
                                                        costToDrawDestinyCardResult.costToDrawCardFailed(true);
                                                    }
                                                }
                                        )
                                );
                            }
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isForceRetrievalJustInitiated(game, effectResult, opponent)
                && !GameConditions.canSpot(game, self, Filters.BB8_or_has_BB8_as_permanent_astromech)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Force retrieval");
            // Perform result(s)
            action.appendEffect(
                    new CancelForceRetrievalEffect(action));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (!GameConditions.occupies(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.battleground)
                || GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Resistance_Agent, Filters.presentAt(Filters.battleground_site))))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_4;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasLostPile(game, playerId)
                && GameConditions.canSpot(game, self, Filters.Kylo)) {
            PhysicalCard iWillFinishWhatYouStarted = Filters.findFirstActive(game, self, Filters.I_Will_Finish_What_You_Started);
            if (iWillFinishWhatYouStarted != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Stack Interrupt from Lost Pile");
                action.setActionMsg("Stack an Interrupt from Lost Pile on " + GameUtils.getCardLink(iWillFinishWhatYouStarted));
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromLostPileEffect(action, iWillFinishWhatYouStarted, Filters.Interrupt, false));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_5;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Kylo)) {
            Filter interruptFilter = Filters.playable(self);
            final PhysicalCard iWillFinishWhatYouStarted = Filters.findFirstActive(game, self, Filters.and(Filters.I_Will_Finish_What_You_Started, Filters.hasStacked(interruptFilter)));
            if (iWillFinishWhatYouStarted != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Play stacked Interrupt");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, iWillFinishWhatYouStarted, interruptFilter) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedInterrupt) {
                                action.setActionMsg("Play " + GameUtils.getCardLink(selectedInterrupt) + " from stacked on " + GameUtils.getCardLink(iWillFinishWhatYouStarted));
                                // Perform result(s)
                                action.appendEffect(
                                        new PlayStackedInterruptEffect(action, selectedInterrupt, null, null, true));
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, final Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_5;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Kylo)) {
            Filter interruptFilter = Filters.playableInterruptAsResponse(self, effect);
            final PhysicalCard iWillFinishWhatYouStarted = Filters.findFirstActive(game, self, Filters.and(Filters.I_Will_Finish_What_You_Started, Filters.hasStacked(interruptFilter)));
            if (iWillFinishWhatYouStarted != null) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Play stacked Interrupt");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, iWillFinishWhatYouStarted, interruptFilter) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedInterrupt) {
                                action.setActionMsg("Play " + GameUtils.getCardLink(selectedInterrupt) + " from stacked on " + GameUtils.getCardLink(iWillFinishWhatYouStarted));
                                // Perform result(s)
                                action.appendEffect(
                                        new PlayStackedInterruptEffect(action, selectedInterrupt, effect, null, true));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_5;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Kylo)) {
            Filter interruptFilter = Filters.playableInterruptAsResponse(self, effectResult);
            final PhysicalCard iWillFinishWhatYouStarted = Filters.findFirstActive(game, self, Filters.and(Filters.I_Will_Finish_What_You_Started, Filters.hasStacked(interruptFilter)));
            if (iWillFinishWhatYouStarted != null) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Play stacked Interrupt");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, iWillFinishWhatYouStarted, interruptFilter) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedInterrupt) {
                                action.setActionMsg("Play " + GameUtils.getCardLink(selectedInterrupt) + " from stacked on " + GameUtils.getCardLink(iWillFinishWhatYouStarted));
                                // Perform result(s)
                                action.appendEffect(
                                        new PlayStackedInterruptEffect(action, selectedInterrupt, null, effectResult, true));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}