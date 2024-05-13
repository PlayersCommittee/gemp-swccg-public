package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 5
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: A Trophy Sacrificed
 */
public class Card205_019 extends AbstractUsedOrLostInterrupt {
    public Card205_019() {
        super(Side.DARK, 4, "A Trophy Sacrificed", Uniqueness.UNRESTRICTED, ExpansionSet.SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jabba's minions could be expected to be sacrificed to save the Hutt, to destroy one of the Hutts enemies or to provide the Hutt and his minions with a good laugh.");
        setGameText("To play this Interrupt, you must first place a card stacked on Jabba's Trophies in owner's Lost Pile. USED: Draw top card of Reserve Deck. LOST: Cancel an attempt to use a weapon to target your alien. OR Cancel Sense.");
        addIcons(Icon.VIRTUAL_SET_5);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        PhysicalCard jabbasTrophies = Filters.findFirstActive(game, self, Filters.and(Filters.Jabbas_Trophies, Filters.hasStacked(Filters.any)));
        if (jabbasTrophies != null
                && GameConditions.hasReserveDeck(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Draw top card of Reserve Deck");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, jabbasTrophies, Filters.any) {
                        @Override
                        protected void cardSelected(final PhysicalCard stackedCard) {
                            // Pay cost(s)
                            action.appendCost(
                                    new PutStackedCardInLostPileEffect(action, playerId, stackedCard, false));
                            // Allow response(s)
                            action.allowResponses(
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.alien), Filters.any)) {
            PhysicalCard jabbasTrophies = Filters.findFirstActive(game, self, Filters.and(Filters.Jabbas_Trophies, Filters.hasStacked(Filters.any)));
            if (jabbasTrophies != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Cancel weapon firing");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, jabbasTrophies, Filters.any) {
                            @Override
                            protected void cardSelected(final PhysicalCard stackedCard) {
                                // Pay cost(s)
                                action.appendCost(
                                        new PutStackedCardInLostPileEffect(action, playerId, stackedCard, false));
                                // Allow response(s)
                                action.allowResponses(
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelWeaponTargetingEffect(action));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Sense)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PhysicalCard jabbasTrophies = Filters.findFirstActive(game, self, Filters.and(Filters.Jabbas_Trophies, Filters.hasStacked(Filters.any)));
            if (jabbasTrophies != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
                // Choose target(s)
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, jabbasTrophies, Filters.any) {
                            @Override
                            protected void cardSelected(final PhysicalCard stackedCard) {
                                // Pay cost(s)
                                action.appendCost(
                                        new PutStackedCardInLostPileEffect(action, playerId, stackedCard, false));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}