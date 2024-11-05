package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Interrupt
 * Subtype: Starting
 * Title: Degentrification
 */
public class Card304_112 extends AbstractStartingInterrupt {
    public Card304_112() {
        super(Side.LIGHT, 3, Title.Degentrification, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("In areas across the galaxy after the fall of the Empire, public funding dried up. Corruption ran wild and once thriving (though oppressed) areas became infested with criminals.");
        setGameText("If you have deployed a site with exactly two [Light Side] (and no other locations), " +
                "deploy a Ulress battleground site; then, unless your Waiting Room or Claudius Palace sites on table, " +
                "deploy three Effects that deploy for free and are always immune to Alter. Place Interrupt in Lost Pile.");
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PhysicalCard> cardsPlayedThisGame = game.getModifiersQuerying().getCardsPlayedThisGame(playerId);

        final List<PhysicalCard> startingLocations = new ArrayList<>();
        for (PhysicalCard card : cardsPlayedThisGame) {
            if (Filters.location.accepts(game, card)) {
                startingLocations.add(card);
            }
        }

        final Filter validStartingLocationFilter = Filters.and(Filters.owner(playerId),
                Filters.and(Filters.iconCount(Icon.LIGHT_FORCE, 2), Filters.site));

        if (startingLocations.size() == 1 && validStartingLocationFilter.accepts(game, startingLocations.get(0))) {

            boolean palaceOrImperialSquareOnTable =
                    Filters.or(Filters.titleContains("Waiting"), Filters.Claudius_Palace_site).accepts(game, startingLocations.get(0));

            if (!palaceOrImperialSquareOnTable) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
                action.setText("Deploy a Cloud City battleground site and Effects from Reserve Deck");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action, Filters.Ulress_battleground_site, true, false));
                                action.appendEffect(
                                        new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree, Filters.always_immune_to_Alter), 1, 3, true, false));
                                action.appendEffect(
                                        new PutCardFromVoidInLostPileEffect(action, playerId, self));
                            }
                        }
                );
                return action;
            } else {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
                action.setText("Deploy a Ulress battleground site");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action, Filters.Ulress_battleground_site, true, false));
                                action.appendEffect(
                                        new PutCardFromVoidInLostPileEffect(action, playerId, self));
                            }
                        }
                );
                return action;
            }


        }
        return null;
    }
}