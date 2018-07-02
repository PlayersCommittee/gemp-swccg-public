package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.RetrieveForceResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used
 * Title: Imbalance
 */
public class Card4_144 extends AbstractUsedInterrupt {
    public Card4_144() {
        super(Side.DARK, 4, "Imbalance", Uniqueness.UNIQUE);
        setLore("'ConcentraaAAATE!'");
        setGameText("If opponent just retrieved Force, opponent must lose X Force, where X = one-half the number of cards retrieved (round up).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justRetrievedForce(game, effectResult, opponent)) {
            final int numForceToLose = (((RetrieveForceResult) effectResult).getAmountOfForceRetrieved() + 1) / 2;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent lose " + numForceToLose + " Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, numForceToLose));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}