package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardActionReason;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
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
 * Subtype: Used
 * Title: Turn It Off! Turn It Off! (V)
 */
public class Card201_037 extends AbstractUsedInterrupt {
    public Card201_037() {
        super(Side.DARK, 5, "Turn It Off! Turn It Off!", Uniqueness.UNRESTRICTED, ExpansionSet.SET_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Turn it off! Turn it off! Off! TURN IT OFF!");
        setGameText("Play a Defensive Shield from under your Starting Effect. OR If opponent just deployed a [Maintenance] card, it may not battle for remainder of turn. OR Cancel Out Of Commission (if attempting to place a card out of play). [Immune to Sense]");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_1);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (startingEffect != null) {
            Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
            if (GameConditions.hasStackedCards(game, startingEffect, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
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
        if (TriggerConditions.justDeployed(game, effectResult, opponent, filter)) {
            final PlayCardResult playCardResult = (PlayCardResult) effectResult;
            final PhysicalCard playedCard = playCardResult.getPlayedCard();
            final List<PhysicalCard> cardsToAffect = new LinkedList<PhysicalCard>();
            cardsToAffect.add(playCardResult.getPlayedCard());
            PhysicalCard otherPlayedCard = playCardResult.getOtherPlayedCard();
            if (otherPlayedCard != null && filter.accepts(game, otherPlayedCard)) {
                cardsToAffect.add(otherPlayedCard);
            }

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            action.setText("Prevent " + GameUtils.getFullName(playedCard) + " from battling");
            action.addAnimationGroup(cardsToAffect);
            // Allow response(s)
            action.allowResponses("Prevent " + GameUtils.getAppendedNames(cardsToAffect) + " from battling until end of turn",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new MayNotBattleUntilEndOfTurnEffect(action, cardsToAffect));
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
        if (TriggerConditions.isPlayingCardForReason(game, effect, Filters.Out_Of_Commission, PlayCardActionReason.ATTEMPTING_TO_PLACE_A_CARD_OUT_OF_PLAY)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}