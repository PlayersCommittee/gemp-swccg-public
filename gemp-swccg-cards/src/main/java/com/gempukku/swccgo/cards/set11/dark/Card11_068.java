package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Effect
 * Title: A Million Voices Crying Out
 */
public class Card11_068 extends AbstractNormalEffect {
    public Card11_068() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.A_Million_Voices_Crying_Out, Uniqueness.UNIQUE);
        setLore("Tarkin silenced the voices of Alderaan with the power of the Death Star.");
        setGameText("Deploy on table. Twice per game may take Commence Primary Ignition into hand from Lost Pile or Reserve Deck; reshuffle. If Alderaan 'blown away,' retrieve 3 Force whenever you deploy a unique (•) Star Destroyer. (Immune to Alter.)");
        addIcons(Icon.TATOOINE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.A_MILLION_VOICES_CRYING_OUT__UPLOAD_CPI;

        // Check condition(s)
        if (GameConditions.isTwicePerGame(game, self, gameTextActionId)) {
            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take card into hand from Reserve Deck");
                action.setActionMsg("Take Commence Primary Ignition into hand from Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new TwicePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Commence_Primary_Ignition, true));
                actions.add(action);
            }
            if (GameConditions.canTakeCardsIntoHandFromLostPile(game, playerId, self, gameTextActionId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take card into hand from Lost Pile");
                action.setActionMsg("Take Commence Primary Ignition into hand from Lost Pile");
                // Update usage limit(s)
                action.appendUsage(
                        new TwicePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.Commence_Primary_Ignition, true));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, playerId, Filters.and(Filters.unique, Filters.Star_Destroyer))
                && GameConditions.isBlownAway(game, Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Alderaan, true)))) {
            final PhysicalCard playedCard = ((PlayCardResult) effectResult).getPlayedCard();
            int numToRetrieve = 3;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve "+numToRetrieve+" Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, numToRetrieve) {
                        @Override
                        public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                            return Collections.singletonList(playedCard);
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}