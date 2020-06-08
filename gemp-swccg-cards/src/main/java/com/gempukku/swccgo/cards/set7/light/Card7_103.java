package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlaceUsedPileOnReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: Star Destroyer!
 */
public class Card7_103 extends AbstractUsedInterrupt {
    public Card7_103() {
        super(Side.LIGHT, 7, "Star Destroyer!", Uniqueness.UNIQUE);
        setLore("Leia's perception increased as she became more attuned to the Force. She attained the uncanny ability to spot objects at long distances.");
        setGameText("If opponent just deployed or moved a Star Destroyer, say 'Star Destroyer!' After placing interrupt in Used Pile, may place Used Pile on top of Reserve Deck.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.Star_Destroyer)
                || TriggerConditions.moved(game, effectResult, opponent, Filters.Star_Destroyer)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Say 'Star Destroyer!'");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardFromVoidInUsedPileEffect(action, playerId, self) {
                                        @Override
                                        protected final void afterCardPutInCardPile() {
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, playerId,
                                                            new YesNoDecision("Do you want to place Used Pile on top of Reserve Deck?") {
                                                                @Override
                                                                protected void yes() {
                                                                    action.appendEffect(
                                                                            new PlaceUsedPileOnReserveDeckEffect(action, playerId));
                                                                }
                                                                @Override
                                                                protected void no() {
                                                                    game.getGameState().sendMessage(playerId + " chooses to not place Used Pile on top of Reserve Deck");
                                                                }
                                                            }
                                                    )
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}