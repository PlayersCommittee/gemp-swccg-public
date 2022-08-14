package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 6
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Scomp Link Access (V)
 */
public class Card601_174 extends AbstractUsedOrStartingInterrupt {
    public Card601_174() {
        super(Side.LIGHT, 3, Title.Scomp_Link_Access);
        setVirtualSuffix(true);
        setLore("A computer connection access port used mainly by droids to plug into database networks and locate information, evaluate threats, execute diagnostics or perform maintenance.");
        setGameText("USED: Take a non-[Episode I] droid (except IL-19) into hand from Reserve Deck; reshuffle. OR Place a card from hand on Used Pile. " +
                "STARTING: Deploy from Reserve Deck two always [Immune to Alter] Effects. If you have only one location (with less than 3 [Light Side Force]) (except Massassi Throne Room) on table, may deploy a [Cloud City] site. Place this Interrupt in hand.");
        addIcons(Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SCOMP_LINK_ACCESS_V__TAKE_A_DROID_INTO_HAND;

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take droid into hand from Reserve Deck");
            action.setActionMsg("Take a non-[Episode I] droid (except IL-19) into hand from Reserve Deck");

            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(
                            new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.not(Icon.EPISODE_I), Filters.droid, Filters.except(Filters.title("IL-19"))), true));
                }
            });

            actions.add(action);
        }


        // Check condition(s)
        if (GameConditions.hasInHand(game, playerId, Filters.not(self))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place a card from hand on Used Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardFromHandOnUsedPileEffect(action, playerId));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy Effects from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Deploys two Effects that are always immune to Alter from Reserve Deck. If you have only one location on table (with < 3 [Light Side] on table) (except Massassi Throne Room), may deploy a [Cloud City] site.",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 2, 2, true, false));
                        action.appendEffect(new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                Collection<PhysicalCard> myLocations = Filters.filterTopLocationsOnTable(game, Filters.your(self));
                                if(myLocations.size()==1) {
                                    PhysicalCard location = myLocations.iterator().next();
                                    if (!Filters.Massassi_Throne_Room.accepts(game, location) && game.getModifiersQuerying().getIconCount(game.getGameState(), location, Icon.LIGHT_FORCE) < 3) {
                                        action.appendEffect(new DeployCardsFromReserveDeckEffect(action, Filters.and(Icon.CLOUD_CITY, Filters.site), 0, 1, true,true));
                                    }
                                }
                            }
                        });
                        action.appendEffect(
                                new TakeCardFromVoidIntoHandEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}