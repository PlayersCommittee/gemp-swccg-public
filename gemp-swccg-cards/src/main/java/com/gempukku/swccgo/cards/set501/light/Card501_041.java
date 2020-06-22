package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: Endor Celebration (v)
 */
public class Card501_041 extends AbstractStartingInterrupt {
    public Card501_041() {
        super(Side.LIGHT, 5, Title.Endor_Celebration, Uniqueness.UNIQUE);
        setLore("The Rebel presence on Endor meant that the Ewoks would be able to live free from the Empire's tyranny.");
        setGameText("If your starting location had exactly 2 [LS] icons, deploy [V13] Chirpa's Hut up to three Effects (except Strike Planning) that deploy for free and are always immune to Alter. If Ewok Celebration on table, may take any Ewok into hand. Place Interrupt in Lost Pile.");
        addIcons(Icon.ENDOR);
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
                Filters.and(Filters.iconCount(Icon.LIGHT_FORCE, 2), Filters.location));

        // Check condition(s)
        if (validStartingLocationFilter.accepts(game, startingLocations.get(0))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy [V13] Chirpa's Hut up to three Effects");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Chief_Chirpas_Hut, Filters.icon(Icon.VIRTUAL_SET_13)), true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.immune_to_Alter, Filters.not(Filters.title("Strike Planning")),
                                            Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table"))), 1, 3, true, false));
                            if (GameConditions.canSpot(game, self, Filters.title("Ewok Celebration"))) {
                                action.appendEffect(
                                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Ewok, true)
                                );
                            }
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