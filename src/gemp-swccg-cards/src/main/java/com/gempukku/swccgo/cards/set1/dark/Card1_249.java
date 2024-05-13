package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Title: Imperial Barrier
 */
public class Card1_249 extends AbstractUsedInterrupt {
    public Card1_249() {
        super(Side.DARK, 4, Title.Imperial_Barrier, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("As is often the case with a hasty plan, a quick heroic escape from the Death Star was temporarily thwarted by the magnetically sealed door in the trash compactor.");
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
                            protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> cardsTargeted) {
                                action.addAnimationGroup(cardsTargeted);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 1));
                                // Allow response(s)
                                action.allowResponses("Prevent " + GameUtils.getAppendedNames(cardsTargeted) + " from battling or moving until end of turn",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new MayNotMoveOrBattleUntilEndOfTurnEffect(action, cardsTargeted));
                                            }
                                        }
                                );
                            }
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}