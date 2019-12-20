package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.CancelPreviousBattleDestinyDrawsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Interrupt
 * Subtype: Used
 * Title: Mandalorian Mishap (V)
 */
public class Card208_020 extends AbstractUsedInterrupt {
    public Card208_020() {
        super(Side.LIGHT, 5, "Mandalorian Mishap", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Most space-faring adventurers meet their fate with a calm, quiet dignity.");
        setGameText("Cancel the game text of Jodo or any Fett for remainder of turn. OR [Upload] Sabine, Under Attack, or a blaster. OR During a battle, if opponent just drew more than two battle destiny cancel one of those destiny draws (your choice).");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter filter = Filters.or(Filters.Jodo, Filters.Fett);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel character's game text");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Jodo or any Fett", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.MANDALORIAN_MISHAP__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Sabine, Under Attack, or a blaster into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Sabine, Filters.Under_Attack, Filters.blaster), true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.didDrawMoreThanBattleDestinies(game, opponent, 2)
                && GameConditions.canCancelOpponentsPreviouslyDrawnBattleDestiny(game, playerId)) {
            final int numToRemain = game.getGameState().getBattleState().getNumBattleDestinyDrawn(opponent) - 1;
            if (numToRemain > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Cancel a battle destiny draw");
                // Allow response(s)
                action.allowResponses("Cancel an opponent's battle destiny draw",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new CancelPreviousBattleDestinyDrawsEffect(action, opponent, numToRemain));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}