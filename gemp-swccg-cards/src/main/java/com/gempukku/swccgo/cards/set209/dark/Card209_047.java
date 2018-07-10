package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 9
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Death Squadron Assignment
 */
public class Card209_047 extends AbstractUsedOrLostInterrupt {
    public Card209_047() {
        super(Side.DARK, 4, "Death Squadron Assignment", Uniqueness.UNIQUE);
        setLore("Make ready to land our troops beyond their energy field and deploy the fleet so that nothing gets off the system.");
        setGameText("USED: [upload] a card with “Death Squadron” in lore. LOST: [download] an Imperial to a [Hoth] location.");
        addIcons(Icon.VIRTUAL_SET_9);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.DEATH_SQUADRON_ASSIGNMENT__UPLOAD_CARD_WITH_DEATH_SQUADRON_IN_LORE;

        // Check condition(s) for USED action
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take card into hand from Reserve Deck.");
            // Allow response(s)
            action.allowResponses("Take card into hand from Reserve Deck with “Death Squadron” in lore." ,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.loreContains("Death Squadron"), true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.DEATH_SQUADRON_ASSIGNMENT__DOWNLOAD_IMPERIAL_TO_HOTH_LOCATION;
        // Check condition(s) for LOST action
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Deploy Imperial from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy Imperial from Reserve Deck to Hoth location" ,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Imperial, Filters.and(Icon.HOTH, Filters.location), true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

}
