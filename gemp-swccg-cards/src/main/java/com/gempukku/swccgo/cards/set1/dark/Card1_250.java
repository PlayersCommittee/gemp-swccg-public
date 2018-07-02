package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LookAtReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Imperial Code Cylinder
 */
public class Card1_250 extends AbstractUsedInterrupt {
    public Card1_250() {
        super(Side.DARK, 4, Title.Imperial_Code_Cylinder);
        setLore("Imperial officers are issued coded cylinders which access computer information via Scomp links. Each cylinder is coded to the officer's own security clearance.");
        setGameText("If any Imperial leader is present with a Scomp link, you may: Glance at the cards in any Reserve Deck for 20 seconds. Shuffle, cut and replace. OR If also at a Death Star site, cancel Scomp Link Access or Into The Garbage Chute, Flyboy.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Imperial_leader, Filters.at_Scomp_Link))
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Look at Reserve Deck");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            // Allow response(s)
                            action.allowResponses("Look at " + cardPileOwner + "'s " + cardPile.getHumanReadable(),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LookAtReserveDeckEffect(action, playerId, cardPileOwner));
                                            action.appendEffect(
                                                    new ShuffleReserveDeckEffect(action, cardPileOwner));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Imperial_leader, Filters.at_Scomp_Link, Filters.at(Filters.Death_Star_site)))) {
            if (GameConditions.canTargetToCancel(game, self, Filters.Scomp_Link_Access)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Scomp_Link_Access, Title.Scomp_Link_Access);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Into_The_Garbage_Chute_Flyboy)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Into_The_Garbage_Chute_Flyboy, Title.Into_The_Garbage_Chute_Flyboy);
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        Filter filter = Filters.or(Filters.Scomp_Link_Access, Filters.Into_The_Garbage_Chute_Flyboy);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Imperial_leader, Filters.at_Scomp_Link, Filters.at(Filters.Death_Star_site)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}