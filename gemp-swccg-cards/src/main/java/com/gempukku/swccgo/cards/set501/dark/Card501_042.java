package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
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

/**
 * Set: Set 12
 * Type: Interrupt
 * Subtype: Starting
 * Title: Slip Sliding Away (V)
 */
public class Card501_042 extends AbstractStartingInterrupt {
    public Card501_042() {
        super(Side.DARK, 3, "Slip Sliding Away", Uniqueness.UNIQUE);
        setLore("Luke got the shaft.");
        setGameText("If you deployed exactly one location (which was a site with exactly 2 [DS]): deploy one non-Coruscant battleground site with a scomp link and up to 3 Effects that are always immune to Alter. Place Interrupt in Lost pile.");
        addIcons(Icon.VIRTUAL_SET_12);
        setVirtualSuffix(true);
        setTestingText("Slip Sliding Away (V)");
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        final Filter validStartingLocation = Filters.and(Filters.owner(playerId),
                Filters.and(Filters.iconCount(Icon.DARK_FORCE, 2), Filters.site));
     if (GameConditions.canSpotLocation(game, 1, Filters.owner(playerId)) &&
                GameConditions.canSpotLocation(game, validStartingLocation)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Deploy a non-Coruscant battleground site with a scomp link and Effects from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.not(Filters.Coruscant_site), Filters.icon(Icon.SCOMP_LINK), Filters.battleground_site), false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 1, 3, true, false));
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