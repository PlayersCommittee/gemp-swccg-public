package com.gempukku.swccgo.cards.set212.dark;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * Set: Set 12
 * Type: Interrupt
 * Subtype: Starting
 * Title: Slip Sliding Away (V)
 */
public class Card212_004 extends AbstractStartingInterrupt {
    public Card212_004() {
        super(Side.DARK, 3, Title.Slip_Sliding_Away, Uniqueness.UNIQUE);
        setLore("Luke got the shaft.");
        setGameText("If you deployed exactly one location (and it was a site with exactly 2 [Dark Side Force]), deploy a battleground site, then if you have not deployed a site with “Palace” in title, may also deploy up to 3 Effects that are always immune to Alter. Place Interrupt in Lost Pile.");
        addIcons(Icon.VIRTUAL_SET_12);
        setVirtualSuffix(true);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final Filter validStartingLocation = Filters.and(Filters.owner(playerId),
                Filters.and(Filters.iconCount(Icon.DARK_FORCE, 2), Filters.site));
        if (GameConditions.canSpotLocation(game, 1, Filters.owner(playerId)) &&
                GameConditions.canSpotLocation(game, validStartingLocation)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Deploy a battleground site and Effects from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendTargeting(
                                    new ChooseCardFromReserveDeckEffect(action, playerId, Filters.battleground_site) {
                                        @Override
                                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                            action.appendEffect(
                                                    new DeployCardFromReserveDeckEffect(action, Filters.sameCardId(selectedCard), false)
                                            );
                                            if (!GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.site, Filters.titleContains("Palace")))) {
                                                action.appendEffect(
                                                        new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 1, 3, true, false));
                                            }
                                            action.appendEffect(
                                                    new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                        }
                                    }
                            );
                        }
                    }
            );
            return action;
        }
        return null;
    }
}