package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitIncreasedAbovePrintedModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: I've Got A Really Good Feeling About This
 */
public class Card501_092 extends AbstractUsedInterrupt {
    public Card501_092() {
        super(Side.LIGHT, 4, "I've Got A Really Good Feeling About This", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Recirculate if Han or Lando on table. OR At the start of any turn, place 3 cards from hand on Reserve Deck, reshuffle, and draw 3 cards. (Opponent may do the same.) OR For remainder of turn, all characters are power -1 and ignore forfeit bonuses.");
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("I've Got A Really Good Feeling About This");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.canSpot(game, self, Filters.or(Filters.Han, Filters.Lando))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Recirculate");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RecirculateEffect(action, playerId)
                            );
                        }
                    }
            );
            actions.add(action);
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Affect characters");
        // Allow response(s)
        action.allowResponses("Make opponent use 1 Force to fire a weapon and make opponent's starship weapon destiny draws -1 for remainder of turn",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action,
                                        new PowerModifier(self, Filters.character, -1),
                                        "Makes characters power -1"));
                        action.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action,
                                        new MayNotHaveForfeitIncreasedAbovePrintedModifier(self, Filters.character),
                                        "Characters ignore forfeit bonuses"));
                    }
                }
        );
        actions.add(action);

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isStartOfEachTurn(game, effectResult)
                && GameConditions.hasInHand(game, playerId, 3, Filters.any)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place cards in Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardsFromHandOnBottomOfReserveDeckEffect(action, playerId, 3, 3));
                            action.appendEffect(
                                    new ShuffleReserveDeckEffect(action, playerId));
                            action.appendEffect(
                                    new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 3)
                            );
                            if (GameConditions.hasInHand(game, opponent, 3, Filters.any)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new YesNoDecision("Put 3 Cards from hand into Reserve to draw 3 cards?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new PutCardsFromHandOnBottomOfReserveDeckEffect(action, opponent, 3, 3));
                                                        action.appendEffect(
                                                                new ShuffleReserveDeckEffect(action, opponent));
                                                        action.appendEffect(
                                                                new DrawCardsIntoHandFromReserveDeckEffect(action, opponent, 3)
                                                        );
                                                    }
                                                })
                                );
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
