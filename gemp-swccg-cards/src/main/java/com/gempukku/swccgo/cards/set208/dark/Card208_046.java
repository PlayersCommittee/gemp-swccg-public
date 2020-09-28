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
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Interrupt
 * Subtype: Lost Or Starting
 * Title: You Know What I've Come For
 */

public class Card208_046 extends AbstractUsedOrStartingInterrupt {
    public Card208_046() {
        super(Side.DARK, 4, "You Know What I've Come For", Uniqueness.UNIQUE);
        setGameText("USED: Use 1 Force to [upload] a [First Order] shuttle. STARTING: Deploy Jakku: Landing Site, Bow To The First Order, and one Effect that deploys on table and is always immune to Alter. Place this Interrupt in hand.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.YOU_KNOW_WHAT_IVE_COME_FOR__UPLOAD_FIRST_ORDER_SHUTTLE;

        // Check condition(s)
        if (GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take shuttle into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Take a [First Order] shuttle into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.shuttle, Icon.FIRST_ORDER), true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy cards from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Deploy Jakku: Landing Site, Bow To The First Order, and one Effect from Reserve Deck",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        if (GameConditions.hasInReserveDeck(game, playerId, Filters.and(Filters.Jakku_Landing_Site, Filters.deployable(self, null, true, 0)))
                                && GameConditions.hasInReserveDeck(game, playerId, Filters.and(Filters.Bow_To_The_First_Order, Filters.deployable(self, null, true, 0)))
                                && GameConditions.hasInReserveDeck(game, playerId, Filters.and(Filters.and(Filters.Effect, Filters.not(Filters.Bow_To_The_First_Order),
                                Filters.always_immune_to_Alter, Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table")))))) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Jakku_Landing_Site, true, false));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Bow_To_The_First_Order, true, false));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.and(Filters.Effect, Filters.always_immune_to_Alter,
                                            Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table")))), true, false));
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
}