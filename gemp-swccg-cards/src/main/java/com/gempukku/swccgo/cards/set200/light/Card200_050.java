package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;



/**
 * Set: Set 0
 * Type: Interrupt
 * Subtype: Starting
 * Title: Careful Planning (V)
 */
public class Card200_050 extends AbstractStartingInterrupt {
    public Card200_050() {
        super(Side.LIGHT, 5, Title.Careful_Planning, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Alliance troops on planet must plan ahead to achieve success in military operations.");
        setGameText("If your starting location was a system, [download] a related site (must be a battleground if the system is a non-battleground or Endor) with < 3 [Light Side Force] icons and up to three Effects that are always immune to Alter. Place this Interrupt in Lost Pile.");
        addIcons(Icon.ENDOR, Icon.VIRTUAL_SET_0);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        final PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
        if (startingLocation != null && Filters.system.accepts(game, startingLocation)) {
            final String systemName = startingLocation.getPartOfSystem();
            if (systemName != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
                action.setText("Deploy site and Effects from Reserve Deck");
                // Allow response(s)
                action.allowResponses("Deploy a related site and up to three Effects from Reserve Deck",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                Filter specialLocationConditions = (!startingLocation.isStartingLocationBattleground() || Filters.Endor_system.accepts(game, startingLocation)) ? Filters.battleground : null;
                                // Perform result(s)
                                action.appendEffect(
                                        new DeployCardToSystemFromReserveDeckEffect(action, Filters.and(Filters.site, Filters.partOfSystem(systemName), Filters.iconCountLessThan(Icon.LIGHT_FORCE, 3)), systemName, specialLocationConditions, true, false));
                                action.appendEffect(
                                        new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 1, 3, true, false));
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