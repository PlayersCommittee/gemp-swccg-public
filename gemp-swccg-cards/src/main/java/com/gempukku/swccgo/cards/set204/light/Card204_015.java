package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Effect
 * Title: We're Leaving (V)
 */
public class Card204_015 extends AbstractNormalEffect {
    public Card204_015() {
        super(Side.LIGHT, 2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "We're Leaving", Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Qui-Gon realized that sometimes it's best to just leave, before any more damage is done.");
        setGameText("Deploy on table. Once per game, may stack top card of Lost Pile beneath Credits Will Do Fine. May examine cards stacked beneath Credits Will Do Fine and place any two in owner's Lost Pile to [upload] (or retrieve into hand) a starship. [Immune to Alter]");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.VIRTUAL_SET_4);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Check condition(s)
        GameState gameState = game.getGameState();
        PhysicalCard credits = Filters.findFirstActive(game, self, Filters.Credits_Will_Do_Fine);
        if (credits != null) {

            GameTextActionId gameTextActionId = GameTextActionId.WERE_LEAVING__STACK_CARD_FROM_LOST_PILE_ON_CREDITS;

            if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
                PhysicalCard topOfLostPile = gameState.getTopOfLostPile(playerId);
                if (topOfLostPile != null) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Stack top card from Lost Pile");
                    action.setActionMsg("Stack top card of Lost Pile on " + GameUtils.getCardLink(credits));
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerGameEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new StackOneCardFromLostPileEffect(action, topOfLostPile, credits, true, false, false));
                    actions.add(action);
                }
            }

            // Check condition(s)
            if (GameConditions.hasStackedCards(game, credits, 2)) {

                gameTextActionId = GameTextActionId.WERE_LEAVING__UPLOAD_STARSHIP;

                if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Take a starship into hand from Reserve Deck");
                    // Pay cost(s)
                    action.appendCost(
                            new PutStackedCardsInLostPileEffect(action, playerId, 2, 2, credits));
                    // Perform result(s)
                    action.appendEffect(
                            new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.starship, true));
                    // Perform result(s)
                    actions.add(action);
                }

                gameTextActionId = GameTextActionId.WERE_LEAVING__RETRIEVE_STARSHIP_INTO_HAND;

                if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId, true)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Retrieve a starship into hand");
                    // Pay cost(s)
                    action.appendCost(
                            new PutStackedCardsInLostPileEffect(action, playerId, 2, 2, credits));
                    // Perform result(s)
                    action.appendEffect(
                            new RetrieveCardIntoHandEffect(action, playerId, Filters.starship));
                    // Perform result(s)
                    actions.add(action);
                }
            }
        }

        return actions;
    }
}