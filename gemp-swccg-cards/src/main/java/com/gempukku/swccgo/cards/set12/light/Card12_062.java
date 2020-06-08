package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Mindful Of The Future
 */
public class Card12_062 extends AbstractUsedOrStartingInterrupt {
    public Card12_062() {
        super(Side.LIGHT, 5, "Mindful Of The Future", Uniqueness.UNIQUE);
        setLore("'But not at the expense of the moment.'");
        setGameText("USED: Use 2 Force to deploy a unique (•) battleground not on table, from Reserve Deck; reshuffle. STARTING: Deploy from your Reserve Deck one Effect which deploys on table (or your side of table) and has no deploy cost; reshuffle. Place Interrupt in Lost Pile.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.MINDFUL_OF_THE_FUTURE__DOWNLOAD_UNIQUE_BATTLEGROUND;

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Deploy unique battleground from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Allow response(s)
            action.allowResponses("Deploy a unique battleground not on table from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.unique, Filters.location, Filters.not(Filters.sameTitleAs(self, Filters.onTable))), Filters.battleground, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy an Effect from Reserve Deck");
        // Allow response(s)
        action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree,
                                        Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table"))), true, true));
                        action.appendEffect(
                                new PutCardFromVoidInLostPileEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}