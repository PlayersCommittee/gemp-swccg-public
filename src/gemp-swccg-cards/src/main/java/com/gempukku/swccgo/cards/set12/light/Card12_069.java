package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
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
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Speak With The Jedi Council
 */
public class Card12_069 extends AbstractUsedOrLostInterrupt {
    public Card12_069() {
        super(Side.LIGHT, 4, "Speak With The Jedi Council", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("Qui-Gon knew that urgency was required. What had transpired on Tatooine demanded the attention of the Council.");
        setGameText("USED: If the Jedi Council Chamber is not on table, you may deploy it from Reserve Deck; reshuffle. LOST: Deploy one Jedi Council member to the Jedi Council Chamber from Reserve Deck; reshuffle.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.SPEAK_WITH_THE_JEDI_COUNCIL__DOWNLOAD_JEDI_COUNCIL_CHAMBER;

        // Check condition(s)
        if (!GameConditions.canSpotLocation(game, Filters.Jedi_Council_Chamber)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Jedi_Council_Chamber)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Deploy Jedi Council Chamber from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Jedi_Council_Chamber, true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.SPEAK_WITH_THE_JEDI_COUNCIL__DOWNLOAD_JEDI_COUNCIL_MEMBER;

        // Check condition(s)
        if (GameConditions.canSpotLocation(game, Filters.Jedi_Council_Chamber)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Deploy a Jedi Council member from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy a Jedi Council member to the Jedi Council Chamber from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Jedi_Council_member, Filters.Jedi_Council_Chamber, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}