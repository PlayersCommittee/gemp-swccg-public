package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Stop Motion (V)
 */
public class Card601_096 extends AbstractUsedInterrupt {
    public Card601_096() {
        super(Side.DARK, 6, Title.Stop_Motion);
        setVirtualSuffix(true);
        setLore("'Your tauntaun'll freeze before you reach the first marker.'");
        setGameText("Take Cold Feet, He Hasn't Come Back Yet, or Ice Storm into hand from Reserve Deck; reshuffle. OR Cancel Dodge. OR Cancel opponent's 'react' away from battle. OR Cancel opponent's attempt to randomly remove one or more cards from your hand (except with Grimtaash).");
        addIcons(Icon.HOTH, Icon.LEGACY_BLOCK_2);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();


        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__STOP_MOTION_V__PULL_CARD;
        final Filter filter = Filters.or(Filters.title("Cold Feet"), Filters.title("He Hasn't Come Back Yet"), Filters.Ice_Storm);

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take a card into hand");
            action.setActionMsg("Take Cold Feet, He Hasn't Come Back Yet, or Ice Storm into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, filter, true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Dodge)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isReact(game, effect)
                && TriggerConditions.isReactJustInitiatedBy(game, effect, game.getOpponent(playerId))
                && TriggerConditions.isMovingAsReact(game, effect, Filters.at(Filters.battleLocation))
        ) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel 'react' away from battle");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelReactEffect(action));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    //TODO Cancel opponent's attempt to randomly remove one or more cards from your hand (except with Grimtaash).
}