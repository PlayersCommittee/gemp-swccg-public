package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.MayNotAllowPlayerToDownloadCardsUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.MayNotBattleUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Perimeter Scan (V)
 */
public class Card201_014 extends AbstractUsedOrLostInterrupt {
    public Card201_014() {
        super(Side.LIGHT, 4, "Perimeter Scan");
        setVirtualSuffix(true);
        setLore("'It's a good bet the Empire knows we're here.'");
        setGameText("USED: Play a Defensive Shield from under your Starting Effect. OR Target opponent's just-deployed [Maintenance] card, it may not battle (or allow opponent to [download] cards) for remainder of turn. [Immune to Sense] LOST: Cancel Trample (unless targeting an Undercover spy).");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_1);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (startingEffect != null) {
            Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
            if (GameConditions.hasStackedCards(game, startingEffect, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setImmuneTo(Title.Sense);
                action.setText("Play a Defensive Shield");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, startingEffect, filter) {
                            @Override
                            protected void cardSelected(final PhysicalCard selectedCard) {
                                // Allow response(s)
                                action.allowResponses(
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter filter = Filters.and(Filters.opponents(self), Icon.MAINTENANCE, Filters.canBeTargetedBy(self));

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, filter)) {
            final PlayCardResult playCardResult = (PlayCardResult) effectResult;
            final PhysicalCard playedCard = playCardResult.getPlayedCard();
            final List<PhysicalCard> cardsToAffect = new LinkedList<PhysicalCard>();
            cardsToAffect.add(playCardResult.getPlayedCard());
            PhysicalCard otherPlayedCard = playCardResult.getOtherPlayedCard();
            if (otherPlayedCard != null && filter.accepts(game, otherPlayedCard)) {
                cardsToAffect.add(otherPlayedCard);
            }

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Sense);
            action.setText("Target " + GameUtils.getFullName(playedCard));
            action.addAnimationGroup(cardsToAffect);
            // Allow response(s)
            action.allowResponses("Prevent " + GameUtils.getAppendedNames(cardsToAffect) + " from battling or allowing opponent to [download] cards until end of turn",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new MayNotBattleUntilEndOfTurnEffect(action, cardsToAffect));
                            action.appendEffect(
                                    new MayNotAllowPlayerToDownloadCardsUntilEndOfTurnEffect(action, cardsToAffect, opponent));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Trample)
                && !TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Trample, Filters.undercover_spy)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}