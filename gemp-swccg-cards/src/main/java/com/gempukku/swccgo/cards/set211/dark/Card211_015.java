package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Interrupt
 * Subtype: Used
 * Title: Twi'lek Advisor (V)
 */
public class Card211_015 extends AbstractUsedInterrupt {
    public Card211_015() {
        super(Side.DARK, 5, "Twi'lek Advisor", Uniqueness.UNIQUE);
        setLore("'He's no Jedi.'");
        setGameText("If Audience Chamber on table, deploy Passenger Deck or a pit from Reserve Deck; reshuffle. OR Deploy an alien leader or Twi'lek to Audience Chamber from Reserve Deck; reshuffle.");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_11);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();


        GameTextActionId gameTextActionId = GameTextActionId.TWILEK_ADVISOR_V_DOWNLOAD_EFFECT;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Audience_Chamber)) {

            final PlayInterruptAction deployPassengerDeckOrPitAction = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            deployPassengerDeckOrPitAction.setText("Deploy Passenger Deck or a pit from Reserve Deck");
            // Allow response(s)
            deployPassengerDeckOrPitAction.allowResponses(
                    new RespondablePlayCardEffect(deployPassengerDeckOrPitAction) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            deployPassengerDeckOrPitAction.appendEffect(
                                    new DeployCardFromReserveDeckEffect(deployPassengerDeckOrPitAction,Filters.or(Filters.title(Title.Passenger_Deck), Filters.pit) , true));
                        }
                    }
            );
            actions.add(deployPassengerDeckOrPitAction);

            final PlayInterruptAction deployAlienLeaderOrTwilekAction = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            deployAlienLeaderOrTwilekAction.setText("Deploy an alien leader or Twi'lek to Audience Chamber from Reserve Deck");
            // Allow response(s)
            deployAlienLeaderOrTwilekAction.allowResponses(
                    new RespondablePlayCardEffect(deployAlienLeaderOrTwilekAction) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            deployAlienLeaderOrTwilekAction.appendEffect(
                                    new DeployCardToLocationFromReserveDeckEffect(deployAlienLeaderOrTwilekAction, Filters.or(Filters.species(Species.TWILEK), Filters.and(Filters.alien, Filters.leader)), Filters.title(Title.Audience_Chamber), true));
                        }
                    }
            );
            actions.add(deployAlienLeaderOrTwilekAction);
        }

        return actions;
    }
}
