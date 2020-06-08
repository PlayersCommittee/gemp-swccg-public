package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceTopCardOfUsedPileOnTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Droid
 * Title: Threepio With His Parts Showing (AI)
 */
public class Card11_014 extends AbstractDroid {
    public Card11_014() {
        super(Side.LIGHT, 3, 2, 1, 3, "Threepio With His Parts Showing", Uniqueness.UNIQUE);
        setAlternateImageSuffix(true);
        setLore("Protocol droid designed by Anakin in order to help his mother. C-3P0 was not to be sold when Anakin left with Qui-Gon Jinn for Coruscant.");
        setGameText("If in a battle with Shmi at a site, once per battle may place top card of your Used Pile on top of your Reserve Deck. Once per turn may place a card from hand on Used Pile to draw top card from Reserve Deck.");
        addPersona(Persona.C3PO);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
        addModelType(ModelType.PROTOCOL);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Card action 1
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.isInBattleWith(game, self, Filters.Shmi)
                && GameConditions.hasUsedPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place top card of Used Pile on Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PlaceTopCardOfUsedPileOnTopOfReserveDeckEffect(action, playerId));
            actions.add(action);
        }

        // Card action 2
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place card from hand on Used Pile");
            action.setActionMsg("Draw top card from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PutCardFromHandOnUsedPileEffect(action, playerId));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
            actions.add(action);
        }

        return actions;
    }
}
