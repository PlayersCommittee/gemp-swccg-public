package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Evader
 */
public class Card2_131 extends AbstractUsedOrLostInterrupt {
    public Card2_131() {
        super(Side.DARK, 4, Title.Evader);
        setLore("A panic move by Black 2 send Vader spinning wildly from the Death Star trench-ironically sparing his life. His destiny would be fulfilled another time.");
        setGameText("USED: Cancel all Revolutions in play (owner loses 1 Force for each). LOST: If Vader or Vader's Custom TIE was just lost, relocate that card to Used Pile. OR Relocate to Used Pile one Imperial just lost from any Death Star location.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Revolution)) {
            final Collection<PhysicalCard> revolutions = Filters.filterActive(game, self, null, TargetingReason.TO_BE_CANCELED, Filters.Revolution);
            if (!revolutions.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Cancel all Revolutions in play");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new CancelCardsOnTableEffect(action, revolutions));
                                action.appendEffect(
                                        new LoseForceEffect(action, opponent, revolutions.size()));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.or(Filters.Vader, Filters.Vaders_Custom_TIE))
                || TriggerConditions.justLostFromLocation(game, effectResult, Filters.Imperial, Filters.Death_Star_location)) {
            final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Place " + GameUtils.getFullName(justLostCard) + " in Used Pile");
            // Allow response(s)
            action.allowResponses("Place " + GameUtils.getCardLink(justLostCard) + " in Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardFromLostPileInUsedPileEffect(action, playerId, justLostCard, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}