package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractLostOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Interrupt
 * Subtype: Lost
 * Title: Moment Of Triumph (V)
 */
public class Card217_016 extends AbstractLostOrStartingInterrupt {
    public Card217_016() {
        super(Side.DARK, 4, "Moment Of Triumph", Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("A ruthless ruler of Outer Rim Territories. Grand Moff Tarkin used the Death Star to destroy Alderaan, creating the doctrine of rule by fear.");
        setGameText("LOST: [download] Kessel or [Set 17] Eriadu. STARTING: If Ralltiir Operations on table, deploy Insignificant Rebellion and up to two Effects that deploy on table, deploy for free, and are always immune to Alter. Place Interrupt in hand.");
        addIcon(Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.MOMENT_OF_TRIUMPH__DOWNLOAD_BATTLEGROUND_SYSTEM;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Deploy Kessel or Eriadu");
            // Allow response(s)
            action.allowResponses("Deploy Kessel or [Set 17] Eriadu from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(CardSubtype.SYSTEM, Filters.or(Filters.Kessel_system, Filters.and(Icon.VIRTUAL_SET_17, Filters.title(Title.Eriadu)))), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {


        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.Ralltiir_Operations)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy Insignificant Rebellion and up to two Effects that deploy for free, deploy on table, and are always immune to Alter.");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Insignificant_Rebellion, true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree, Filters.always_immune_to_Alter,
                                            Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table"))), 1, 2, true, false));
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
