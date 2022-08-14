package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractLostOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Computer Interface (V)
 */
public class Card218_016 extends AbstractLostOrStartingInterrupt {
    public Card218_016() {
        super(Side.LIGHT, 3, Title.Computer_Interface);
        setVirtualSuffix(true);
        setLore("Lobot's direct link with the Cloud City central computer allowed him to efficiently manipulate the floating city's resources.");
        setGameText("LOST: Retrieve Lobot or a droid. OR Draw top card of Used Pile. STARTING: Deploy a mobile site that has a Scomp link, has exactly one [Light Side], and is related to a site on table. Deploy two Effects that deploy for free and are always immune to Alter. Place Interrupt in Reserve Deck.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.COMPUTER_INTERFACE_V__RETRIEVE_CARD;

        // Retrieve Lobot or a droid.
        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Retrieve Lobot or a droid");

            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, Filters.or(Filters.Lobot, Filters.droid)));
                        }
                    }
            );

            actions.add(action);
        }


        // Draw top card of Used Pile
        if (GameConditions.hasUsedPile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Draw top card of Used Pile");

            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawCardIntoHandFromUsedPileEffect(action, playerId));
                        }
                    }
            );

            actions.add(action);
        }

        return actions;
    }


    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {

        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy site and Effects from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Deploy a mobile site with a Scomp link and exactly one [Light Side] that is related to a site on table and two Effects from Reserve Deck",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        Collection<PhysicalCard> sitesOnTable = Filters.filterTopLocationsOnTable(game, Filters.site);
                        Collection<PhysicalCard> reserveDeck = game.getGameState().getReserveDeck(playerId);
                        Collection<PhysicalCard> locationsToDeployFromReserveDeck = new LinkedList<>();
                        for (PhysicalCard c : sitesOnTable) {
                            locationsToDeployFromReserveDeck.addAll(Filters.filter(reserveDeck, game, Filters.relatedLocationEvenWhenNotInPlay(c)));
                        }

                        // Perform result(s)
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.mobile_site, Filters.iconCount(Icon.LIGHT_FORCE, 1), Filters.has_Scomp_link, Filters.in(locationsToDeployFromReserveDeck)), true, false));
                        action.appendEffect(
                                new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree, Filters.always_immune_to_Alter), 2, 2, true, false));
                        action.appendEffect(
                                new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}