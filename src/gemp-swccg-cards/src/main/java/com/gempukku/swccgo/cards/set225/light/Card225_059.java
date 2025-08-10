package com.gempukku.swccgo.cards.set225.light;

import java.util.LinkedList;
import java.util.List;

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
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * Set: Set 25
 * Type: Interrupt
 * Subtype: Lost or Starting
 * Title: You Will Go To The Dagobah System (V)
 */
public class Card225_059 extends AbstractLostOrStartingInterrupt {
    public Card225_059() {
        super(Side.LIGHT, 4, "You Will Go To The Dagobah System", Uniqueness.UNRESTRICTED, ExpansionSet.SET_25, Rarity.V);
        setLore("'There you will learn from Yoda, the Jedi Master who instructed me.'");
        setGameText("LOST: [Upload] [Dagobah] Luke. OR Retrieve Anakin's Lightsaber into hand. [Immune to Sense.] STARTING: If your [Dagobah] objective on table, deploy two Effects (except Wokling) that deploy for free and are always immune to Alter. Place Interrupt in hand.");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_25);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.YOU_WILL_GO_TO_THE_DAGOBAH_SYSTEM__UPLOAD_LUKE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take Luke into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take [Dagobah] Luke into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Icon.DAGOBAH, Filters.Luke), true));
                        }
                    }
            );
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.YOU_WILL_GO_TO_THE_DAGOBAH_SYSTEM__RETRIEVE_ANAKINS_LIGHTSABER;
        // Check condition(s)
        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {
            
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Retrieve Anakin's Lightsaber into hand");
            // Allow response(s)
            action.allowResponses("Retrieve Anakin's Lightsaber into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardIntoHandEffect(action, playerId, Filters.Anakins_Lightsaber));
                        }
                    }
            );
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {

        // Allow response(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.icon(Icon.DAGOBAH), Filters.Objective))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            final Filter effectFilter = Filters.and(Filters.Effect, Filters.not(Filters.Wokling), Filters.deploysForFree, Filters.always_immune_to_Alter);
            action.setText("Deploy two Effects (except Wokling) that deploy for free and are always immune to Alter");
    
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, effectFilter, 2, 2, true, false));
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