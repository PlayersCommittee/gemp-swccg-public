package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LightSideGoesFirstEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;



/**
 * Set: Set 1
 * Type: Interrupt
 * Subtype: Starting
 * Title: According To My Design
 */
public class Card201_034 extends AbstractStartingInterrupt {
    public Card201_034() {
        super(Side.DARK, 4, "According To My Design", Uniqueness.UNIQUE);
        setLore("'Your fleet is lost. And your friends on the Endor moon will not survive. There is no escape, my young apprentice.'");
        setGameText("Unless your starship site or Death Star on table, [download] Emperor to your site (even if converted) and up to three Effects that deploy for free, are [Immune to Alter], and have 'deploy on table' (or 'deploy on your side of table') in game text. Light Side goes first. Place this Interrupt in Lost Pile.");
        addIcons(Icon.VIRTUAL_SET_1);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        final Filter yourSiteEvenIfConverted = Filters.and(Filters.site, Filters.or(Filters.your(self), Filters.convertedLocationOnTopOfLocation(Filters.your(self))));

        // Check condition(s)
        if (!GameConditions.canSpotLocation(game, Filters.and(Filters.your(self), Filters.or(Filters.starship_site, Filters.Death_Star_system)))
                && GameConditions.canSpotLocation(game, yourSiteEvenIfConverted)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy Emperor and up to three Effects from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Emperor, yourSiteEvenIfConverted, true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree, Filters.immune_to_Alter,
                                            Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table"))), 1, 3, true, false));
                            action.appendEffect(
                                    new LightSideGoesFirstEffect(action));
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