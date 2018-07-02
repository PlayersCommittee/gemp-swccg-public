package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.MayNotMoveOrBattleUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Rebel Barrier
 */
public class Card1_105 extends AbstractUsedInterrupt {
    public Card1_105() {
        super(Side.LIGHT, 4, Title.Rebel_Barrier);
        setLore("While being chased through the Death Star, Luke and Leia disabled the blast doors behind them in order to slow down pursuing stormtroopers.");
        setGameText("Use 1 Force to prevent any character or starship just deployed by the opponent from battling or moving for the remainder of this turn.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter filter = Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.starship), Filters.canBeTargetedBy(self));

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, opponent, filter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final PlayCardResult playCardResult = (PlayCardResult) effectResult;
            final PhysicalCard playedCard = playCardResult.getPlayedCard();
            final List<PhysicalCard> cardsToBarrier = new LinkedList<PhysicalCard>();
            cardsToBarrier.add(playCardResult.getPlayedCard());
            PhysicalCard otherPlayedCard = playCardResult.getOtherPlayedCard();
            if (otherPlayedCard != null && filter.accepts(game, otherPlayedCard)) {
                cardsToBarrier.add(otherPlayedCard);
            }
            int numCards = cardsToBarrier.size();
            if (numCards > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Prevent " + GameUtils.getFullName(playedCard) + " from battling or moving");
                action.addAnimationGroup(cardsToBarrier);
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardsOnTableEffect(action, playerId, "Choose character or starship", numCards, numCards, Filters.in(cardsToBarrier)) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardsTargeted(final int targetGroupId1, final Collection<PhysicalCard> cardsTargeted) {
                                action.addAnimationGroup(cardsTargeted);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 1));
                                // Allow response(s)
                                action.allowResponses("Prevent " + GameUtils.getAppendedNames(cardsTargeted) + " from battling or moving until end of turn",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the final targeted card(s)
                                                Collection<PhysicalCard> finalCardsTargeted = action.getPrimaryTargetCards(targetGroupId1);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new MayNotMoveOrBattleUntilEndOfTurnEffect(action, finalCardsTargeted));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}