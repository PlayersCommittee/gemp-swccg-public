package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ResetAttritionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: You Do Have Your Moments
 */
public class Card4_074 extends AbstractLostInterrupt {
    public Card4_074() {
        super(Side.LIGHT, 2, Title.You_Do_Have_Your_Moments, Uniqueness.UNIQUE);
        setLore("Sarcastic. Sardonic. Irreverent. Impertinent. Exasperating. Disrespectful. Outrageous and charming. Han was a scoundrel but Leia began to realize that she loved him.");
        setGameText("During a battle, before any cards have been forfeited, cause all attrition for both sides to be reduced to zero.");
        addIcons(Icon.DAGOBAH);
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