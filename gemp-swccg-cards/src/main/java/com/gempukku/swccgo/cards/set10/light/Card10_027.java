package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnForcePileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ResetAttritionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Yoda Stew & You Do Have Your Moments
 */
public class Card10_027 extends AbstractUsedOrLostInterrupt {
    public Card10_027() {
        super(Side.LIGHT, 2, "Yoda Stew & You Do Have Your Moments", Uniqueness.UNIQUE);
        addComboCardTitles(Title.Yoda_Stew, Title.You_Do_Have_Your_Moments);
        setGameText("USED: During opponent's turn, take up to 4 cards from your hand and place them on top of your Force Pile. LOST: During a battle, before any cards have been forfeited, cause all attrition for both sides to be reduced to 0.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isOpponentsTurn(game, self)
                && GameConditions.hasInHand(game, playerId, Filters.not(self))) {
            final int numberOnCard = game.getModifiersQuerying().isDoubled(game.getGameState(), self) ? 8 : 4;

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Put cards from hand on Force Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardsFromHandOnForcePileEffect(action, playerId, 1, numberOnCard));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && (GameConditions.isAttritionRemaining(game, playerId)
                || GameConditions.isAttritionRemaining(game, opponent))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Reduce all attrition to zero");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ResetAttritionEffect(action, playerId, 0));
                            action.appendEffect(
                                    new ResetAttritionEffect(action, opponent, 0));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}