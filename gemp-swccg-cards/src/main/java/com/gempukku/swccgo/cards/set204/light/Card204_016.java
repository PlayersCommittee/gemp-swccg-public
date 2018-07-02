package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractPoliticalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Effect
 * Subtype: Political
 * Title: You Assume Too Much
 */
public class Card204_016 extends AbstractPoliticalEffect {
    public Card204_016() {
        super(Side.LIGHT, 3, "You Assume Too Much", Uniqueness.UNIQUE);
        setLore("'This is your arena. I feel I must return to mine.'");
        setGameText("Deploy on table. If no senator here, you may place a senator here from hand to draw two cards from top of Reserve Deck. If an order or rebellion agenda here, once during your draw phase may place all cards on your Political Effects in Used Pile.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (!GameConditions.hasStackedCards(game, self, Filters.senator)
                && GameConditions.hasInHand(game, playerId, Filters.or(Filters.senator, Filters.grantedToBePlacedOnOwnersPoliticalEffect))
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw two cards from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new StackCardFromHandEffect(action, playerId, self, Filters.or(Filters.senator, Filters.grantedToBePlacedOnOwnersPoliticalEffect)));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 2));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)
                && GameConditions.hasStackedCards(game, self, Filters.or(Filters.order_agenda, Filters.rebellion_agenda))) {
            Collection<PhysicalCard> cardsOnYourPoliticalEffects = Filters.filterStacked(game,
                    Filters.stackedOn(self, Filters.and(Filters.your(self), Filters.Political_Effect)));
            if (!cardsOnYourPoliticalEffects.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place cards on your Political Effects into Used Pile");
                action.setActionMsg("Place cards on " + playerId + "'s Political Effects into Used Pile");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PutStackedCardsInUsedPileEffect(action, playerId, cardsOnYourPoliticalEffects, true));
                actions.add(action);
            }
        }

        return actions;
    }
}