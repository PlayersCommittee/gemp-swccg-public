package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelDuelEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Lost
 * Title: A Jedi's Resilience
 */
public class Card11_029 extends AbstractLostInterrupt {
    public Card11_029() {
        super(Side.LIGHT, 6, "A Jedi's Resilience", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.U);
        setLore("Luke wasn't going to let Vader dispose of him too quickly.");
        setGameText("If you just lost a duel opponent initiated (before duel has any result) lose 1 Force to cancel the duel and return Interrupt (if any) used to initiate duel to owner's hand. OR If you just lost a character armed with a lightsaber, take that character into hand.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.lostDuelBeforeAnyResults(game, effectResult, playerId)
                && GameConditions.isDuringDuelInitiatedBy(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel duel");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDuelEffect(action));
                            PhysicalCard cardInitiatedDuel = game.getGameState().getDuelState().getCardInitiatedDuel();
                            if (cardInitiatedDuel != null && GameConditions.interruptCanBeReturnedToHand(game, cardInitiatedDuel)) {
                                action.appendEffect(
                                        new ReturnCardToHandFromOffTableEffect(action, cardInitiatedDuel));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.character))) {
            final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            if (justLostCard.wasPreviouslyArmedWithLightsaber()
                    && (Filters.hasPermanentWeapon(Filters.lightsaber).accepts(game, justLostCard)
                    || Filters.canSpot(justLostCard.getCardsPreviouslyAttached(), game, Filters.and(Filters.lightsaber, Filters.inLostPile(playerId))))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Take " + GameUtils.getFullName(justLostCard) + " into hand");
                // Allow response(s)
                action.allowResponses("Take " + GameUtils.getCardLink(justLostCard) + " into hand",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new TakeCardIntoHandFromLostPileEffect(action, playerId, justLostCard, false, true));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }

        return null;
    }
}