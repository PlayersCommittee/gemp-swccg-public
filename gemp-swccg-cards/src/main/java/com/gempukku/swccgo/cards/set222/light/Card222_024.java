package com.gempukku.swccgo.cards.set222.light;

import com.gempukku.swccgo.cards.AbstractLostOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Interrupt
 * Subtype: Lost or Starting
 * Title: Lone Rogue (V)
 */
public class Card222_024 extends AbstractLostOrStartingInterrupt {
    public Card222_024() {
        super(Side.LIGHT, 4, Title.Lone_Rogue, Uniqueness.UNRESTRICTED, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setLore("The pilots at the Rebel Base on Hoth are trained to respond quickly to the Empire's forces. Many Rebels feel that they could take on the whole Empire themselves.");
        setGameText("LOST: If your [Hoth] objective on table, [upload] Dack or Wes. " +
                "STARTING: If a [Hoth] objective on table, deploy Echo Base Garrison and two Effects that deploy for free and are always immune to Alter. " +
                "Place Interrupt in Lost Pile.");
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_22);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LONE_ROGUE__UPLOAD_DACK_OR_WES;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a Dack or Wes into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.persona(Persona.DACK), Filters.persona(Persona.WES)), true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }


    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy Echo Base Garrison and up to 2 Effects that deploy for free and are always immune to Alter.");
        // Allow response(s)

        if (GameConditions.canSpot(game, self, Filters.and(Filters.icon(Icon.HOTH), Filters.Objective))) {
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.title("Echo Base Garrison"), true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.immune_to_Alter,
                                            Filters.deploysForFree), 1, 2, true, false));
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