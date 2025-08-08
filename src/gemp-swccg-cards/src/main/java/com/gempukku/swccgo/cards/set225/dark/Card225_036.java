package com.gempukku.swccgo.cards.set225.dark;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.CancelCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.CancelReactEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnLostPileEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.ReduceAttritionEffect;
import com.gempukku.swccgo.logic.effects.ShuffleLostPileEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Set: Set 25
 * Type: Epic Event
 * Title: With Thunderous Applause
 */
public class Card225_036 extends AbstractEpicEventDeployable {
    public Card225_036() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.With_Thunderous_Applause, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setGameText("Deploy on Galactic Senate. [V17] Passel Argente's game text and your Political Effects are canceled. Twice per turn, may target your agenda here: Blockade: Cancel a 'react.' Taxation: Place a card with no printed destiny number > 4 from hand in Used Pile to activate 1 Force. Trade: During your draw phase, place a card from hand in Lost Pile, shuffle that pile, and take top card into hand. Wealth: Subtract 1 from attrition against you.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Galactic_Senate;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Icon.VIRTUAL_SET_17, Filters.Passel_Argente)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.your(self), Filters.Political_Effect))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        Filter yourPoliticalEffectsFilter = Filters.and(Filters.your(self), Filters.Political_Effect);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, yourPoliticalEffectsFilter)) {

            Collection<PhysicalCard> cardsToCancel = Filters.filterActive(game, self, TargetingReason.TO_BE_CANCELED, yourPoliticalEffectsFilter);

            if (!cardsToCancel.isEmpty()) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Cancel your Political Effects");

                action.appendEffect(
                        new CancelCardsOnTableEffect(action, cardsToCancel));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.WITH_THUNDEROUS_APPLAUSE__TARGET_AGENDA;

        Filter blockadeAgendaFilter = Filters.and(Filters.your(playerId), Filters.blockade_agenda, Filters.here(self));

        // Check condition(s)
        if (TriggerConditions.isReact(game, effect)
                && GameConditions.isNumTimesPerTurn(game, self, playerId, 2, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, blockadeAgendaFilter)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Blockade: Cancel 'react'");
            action.setActionMsg("Target a blockade agenda to cancel 'react'");
            // Update usage limit(s)
            action.appendUsage(
                    new NumTimesPerTurnEffect(action, 2));

            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target blockade agenda", blockadeAgendaFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);

                            // Allow response(s)
                            action.allowResponses("Target blockade agenda on " + GameUtils.getCardLink(cardTargeted) + " to cancel 'react'",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelReactEffect(action));
                                        }
                                    }
                            );

                        }
                    });

            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.WITH_THUNDEROUS_APPLAUSE__TARGET_AGENDA;
        Filter taxationAgendaFilter = Filters.and(Filters.your(playerId), Filters.taxation_agenda, Filters.here(self));
        Filter lowDestinyCard = Filters.and(Filters.not(Filters.printedDestinyGreaterThan(4)), Filters.not(Filters.printedAlternateDestinyGreaterThan(4)));

        // Check condition(s)
        if (GameConditions.isNumTimesPerTurn(game, self, playerId, 2, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, taxationAgendaFilter)
                && GameConditions.hasInHand(game, playerId, lowDestinyCard)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Taxation: Place card in Used Pile");
            action.setActionMsg("Place a card with no printed destiny number > 4 from hand in Used Pile to activate 1 Force");

            // Update usage limit(s)
            action.appendUsage(
                    new NumTimesPerTurnEffect(action, 2));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target taxation agenda", taxationAgendaFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new PutCardFromHandOnUsedPileEffect(action, playerId, lowDestinyCard, false));
                            // Allow response(s)
                            action.allowResponses("Target taxation agenda on " + GameUtils.getCardLink(cardTargeted) + " to make their next [Episode I] character deploy -1",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ActivateForceEffect(action, playerId, 1));
                                        }
                                    }
                            );

                        }
                    });
            actions.add(action);
        }

        Filter tradeAgendaFilter = Filters.and(Filters.your(playerId), Filters.trade_agenda, Filters.here(self));
        // Check condition(s)
        if (GameConditions.isNumTimesPerTurn(game, self, playerId, 2, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, tradeAgendaFilter)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.DRAW)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasLostPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Trade: Place card in Lost Pile");
            action.setActionMsg("Place a card from hand in Lost Pile, shuffle that pile, and take top card into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new NumTimesPerTurnEffect(action, 2));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target trade agenda", tradeAgendaFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new PutCardFromHandOnLostPileEffect(action, playerId));
                            // Allow response(s)
                            action.allowResponses("Target trade agenda on " + GameUtils.getCardLink(cardTargeted) + " to place card in Lost Pile",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {                                            
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ShuffleLostPileEffect(action, self));
                                            action.appendEffect(
                                                    new DrawCardIntoHandFromLostPileEffect(action, playerId));
                                        }
                                    }
                            );

                        }
                    });
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        final List<OptionalGameTextTriggerAction> actions = new LinkedList<>();   

        GameTextActionId gameTextActionId = GameTextActionId.WITH_THUNDEROUS_APPLAUSE__TARGET_AGENDA;
        Filter wealthAgendaFilter = Filters.and(Filters.your(playerId), Filters.wealth_agenda, Filters.here(self));

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isDuringBattle(game)
                && GameConditions.isNumTimesPerTurn(game, self, playerId, 2, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, wealthAgendaFilter)) {

            final BattleState battleState = game.getGameState().getBattleState();
            if (battleState.hasAttritionTotal(game.getOpponent(playerId))) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Wealth: Reduce attrition by 1");
                action.setActionMsg("Reduce attrition by 1");
                // Update usage limit(s)
                action.appendUsage(
                        new NumTimesPerTurnEffect(action, 2));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target wealth agenda", wealthAgendaFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);

                                // Allow response(s)
                                action.allowResponses("Target wealth agenda on " + GameUtils.getCardLink(cardTargeted) + " to subtract 1 from attrition",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {                                            
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ReduceAttritionEffect(action, playerId, 1));
                                            }
                                        }
                                );

                            }
                        });
                actions.add(action);
            }
        }
        return actions;
    }

}
