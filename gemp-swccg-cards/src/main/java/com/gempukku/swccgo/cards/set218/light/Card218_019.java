package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelDuelEffect;
import com.gempukku.swccgo.cards.effects.CancelLightsaberCombatEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import com.gempukku.swccgo.logic.timing.results.RetrieveForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 18
 * Type: Interrupt
 * Subtype: Used
 * Title: Fall Of The Legend (V)
 */
public class Card218_019 extends AbstractUsedInterrupt {
    public Card218_019() {
        super(Side.LIGHT, 6, "Fall Of The Legend", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setGameText("If opponent just lost a Dark Jedi or leader, take a card into hand from Used Pile; reshuffle. OR If opponent just retrieved Force, opponent loses 1 Force. OR If lightsaber combat or a duel was just initiated, lose 2 Force (3 if during your move phase) to cancel it.");
        addIcons(Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        final String opponent = game.getOpponent(playerId);

        final GameTextActionId gameTextActionId = GameTextActionId.FALL_OF_THE_LEGEND_V__SEARCH_USED_PILE;
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.opponents(self), Filters.or(Filters.Dark_Jedi, Filters.leader)))
                && GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {

            final PhysicalCard justLostCharacter = ((LostFromTableResult) effectResult).getCard();

            if (justLostCharacter != null) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Take card into hand from Used Pile");

                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new TakeCardIntoHandFromUsedPileEffect(action, playerId, true));
                    }
                });

                actions.add(action);
            }
        }

        // Check condition(s)
        if (TriggerConditions.justRetrievedForce(game, effectResult, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent lose 1 Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, 1));
                        }
                    }
            );
            actions.add(action);
        }

        if (TriggerConditions.duelInitiated(game, effectResult)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel duel");
            int forceCost = (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)?3:2);
            action.appendCost(
                    new LoseForceEffect(action, playerId, forceCost, true));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDuelEffect(action));
                        }
                    }
            );
            actions.add(action);
        }

        if (TriggerConditions.lightsaberCombatInitiated(game, effectResult)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel lightsaber combat");
            int forceCost = (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)?3:2);
            action.appendCost(
                    new LoseForceEffect(action, playerId, forceCost, true));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelLightsaberCombatEffect(action));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}