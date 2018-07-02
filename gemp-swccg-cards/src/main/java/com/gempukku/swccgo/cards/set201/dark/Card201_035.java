package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PreventEffectOnCardEffect;
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
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToRemoveJustLostCardFromLostPileResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Interrupt
 * Subtype: Used
 * Title: Cold Feet (V)
 */
public class Card201_035 extends AbstractUsedInterrupt {
    public Card201_035() {
        super(Side.DARK, 5, "Cold Feet");
        setVirtualSuffix(true);
        setLore("Wampas pack snow around the appendages of captured prey, making use of Hoth's cold environment to immobilize them.");
        setGameText("Play a Defensive Shield from under your Starting Effect. [Immune to Sense] OR Cancel Mantellian Savrip or Surprise Assault. OR Cancel an attempt to remove a just-lost character from opponent's Lost Pile.");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_1);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

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
                actions.add(action);
            }
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Mantellian_Savrip)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Mantellian_Savrip, Title.Mantellian_Savrip);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Mantellian_Savrip, Filters.Surprise_Assault))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isAboutToRemoveJustLostCardFromLostPile(game, effectResult, opponent, Filters.and(Filters.opponents(self), Filters.character))) {
            final AboutToRemoveJustLostCardFromLostPileResult result = (AboutToRemoveJustLostCardFromLostPileResult) effectResult;
            final PhysicalCard cardToRemoveFromLostPile = result.getCardToRemoveFromLostPile();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel attempt to remove " + GameUtils.getFullName(cardToRemoveFromLostPile) + " from opponent's Lost Pile");
            // Allow response(s)
            action.allowResponses("Cancel attempt to remove " + GameUtils.getCardLink(cardToRemoveFromLostPile) + " from opponent's Lost Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PreventEffectOnCardEffect(action, result.getPreventableCardEffect(), cardToRemoveFromLostPile,
                                            "Cancels attempt to remove " + GameUtils.getCardLink(cardToRemoveFromLostPile) + " from opponent's Lost Pile"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}