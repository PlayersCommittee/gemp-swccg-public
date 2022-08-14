package com.gempukku.swccgo.cards.set212.dark;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.common.*;
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
 * Set: Set 12
 * Type: Interrupt
 * Subtype: Starting
 * Title: Slip Sliding Away (V)
 */
public class Card212_004 extends AbstractStartingInterrupt {
    public Card212_004() {
        super(Side.DARK, 3, Title.Slip_Sliding_Away, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Luke got the shaft.");
        setGameText("If you have deployed a site (except Imperial Square) with exactly two [Dark Side] (and no other locations), deploy a related (or mobile) battleground site and up to three Effects that deploy for free and are always immune to Alter. Place Interrupt in Lost Pile.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_12);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PhysicalCard> cardsPlayedThisGame = game.getModifiersQuerying().getCardsPlayedThisGame(playerId);

        final List<PhysicalCard> startingLocations = new ArrayList<>();
        for(PhysicalCard card: cardsPlayedThisGame){
            if (Filters.location.accepts(game, card)){
                startingLocations.add(card);
            }
        }

        final Filter validStartingLocationFilter = Filters.and(Filters.owner(playerId),
                Filters.and(Filters.iconCount(Icon.DARK_FORCE, 2), Filters.site),
                Filters.except(Filters.Coruscant_Imperial_Square));

        if (startingLocations.size() == 1 && validStartingLocationFilter.accepts(game, startingLocations.get(0))) {
            final PhysicalCard location = startingLocations.get(0);

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Deploy a battleground site and Effects from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.or(Filters.relatedLocationEvenWhenNotInPlay(location), Filters.mobile_site), Filters.battleground_site), true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree, Filters.always_immune_to_Alter), 1, 3, true, false));
                            action.appendEffect(
                                    new PutCardFromVoidInLostPileEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }
        return null;
    }
}