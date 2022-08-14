package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractLostOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Interrupt
 * Subtype: Lost or Starting
 * Title: Rendezvous Point On Tatooine (V)
 */
public class Card211_030 extends AbstractLostOrStartingInterrupt {
    public Card211_030() {
        super(Side.LIGHT, 4, "Rendezvous Point On Tatooine");
        setLore("'When we find Jabba the Hutt and that bounty hunter, we'll contact you.'");
        setGameText("LOST: [download] a Tatooine battleground." +
                "STARTING: If your Jabba's Palace site on table, deploy Seeking An Audience and up to 2 Effects that deploy on table and are always immune to Alter. Place Interrupt in hand.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_11);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.RENDEZVOUS_POINT_ON_TATOOINE_DEPLOY_TATOOINE_BATTLEGROUND;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Deploy a Tatooine battleground from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy a Tatooine battleground from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Tatooine_location, Filters.battleground, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        final Filter yourSiteEvenIfConverted = Filters.and(Filters.Jabbas_Palace_site, Filters.or(Filters.your(self), Filters.convertedLocationOnTopOfLocation(Filters.your(self))));

        // Check condition(s)
        if (GameConditions.canSpotLocation(game, yourSiteEvenIfConverted)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy Seeking An Audience and up to 2 Effects that deploy on table and are always immune to Alter.");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.titleContains("Seeking An Audience"),  true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.immune_to_Alter,
                                            Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table"))), 1, 2, true, false));
                            action.appendEffect(
                                    new TakeCardFromVoidIntoHandEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }
        return null;
    }
}
