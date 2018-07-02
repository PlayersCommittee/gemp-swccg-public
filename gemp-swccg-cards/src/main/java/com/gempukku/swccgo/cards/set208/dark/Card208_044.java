package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 8
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Operational As Planned (V)
 */
public class Card208_044 extends AbstractUsedOrStartingInterrupt {
    public Card208_044() {
        super(Side.DARK, 5, Title.Operational_As_Planned, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'We shall double our efforts.'");
        setGameText("USED: Use 1 Force to [download] Krennic or a Jedha location. STARTING: If Death Star on table, deploy Superlaser and up to two Effects that deploy on table and are always immune to Alter. Place this Interrupt in hand.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.OPERATIONAL_AS_PLANNED__DOWNLOAD_KRENNIC_OR_JEDHA_LOCATION;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Deploy card from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Deploy Krennic or a Jedha location from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Krennic, Filters.Jedha_location), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canSpotLocation(game, Filters.Death_Star_system)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Deploy cards from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy Superlaser and up to two Effects from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            if (GameConditions.hasInReserveDeck(game, playerId, Filters.and(Filters.Superlaser, Filters.deployable(self, null, true, 0)))
                                    && GameConditions.hasInReserveDeck(game, playerId, Filters.and(Filters.Effect, Filters.always_immune_to_Alter,
                                    Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table"))))) {
                                action.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action, Filters.Superlaser, true, false));
                                action.appendEffect(
                                        new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter,
                                                Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table"))), 1, 2, true, false));
                            }
                            else {
                                action.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action, Filters.none, true, false));
                            }
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