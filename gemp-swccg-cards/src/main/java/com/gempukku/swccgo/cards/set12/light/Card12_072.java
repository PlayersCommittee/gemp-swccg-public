package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used
 * Title: We Wish To Board At Once
 */
public class Card12_072 extends AbstractUsedInterrupt {
    public Card12_072() {
        super(Side.LIGHT, 5, "We Wish To Board At Once", Uniqueness.UNIQUE);
        setLore("Ambassadors carry a certain amount of political power. Jedi ambassadors carry a lot more.");
        setGameText("Use 3 Force to take one Effect of any kind into hand from your Reserve Deck; reshuffle. OR Take Radiant VII, or one Interrupt with the word 'Podracer(s)' in its game text, into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.WE_WISH_TO_BOARD_AT_ONCE__UPLOAD_EFFECT;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Effect into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses("Take an Effect of any kind into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Effect_of_any_Kind, true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.WE_WISH_TO_BOARD_AT_ONCE__UPLOAD_RADIANT_VII_OR_INTERRUPT;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Radiant VII or Interrupt into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Radiant VII, or an Interrupt with the word 'Podracer(s)' in its game text, into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Radiant_VII,
                                            Filters.and(Filters.Interrupt, Filters.or(Filters.gameTextContains("Podracer"), Filters.gameTextContains("Podracers")))), true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}