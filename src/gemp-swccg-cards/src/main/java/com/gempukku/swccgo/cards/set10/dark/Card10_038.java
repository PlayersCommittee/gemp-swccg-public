package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealOpponentsHandEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromHandEffect;
import com.gempukku.swccgo.logic.effects.PutRandomCardsFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Evader & Monnok
 */
public class Card10_038 extends AbstractUsedOrLostInterrupt {
    public Card10_038() {
        super(Side.DARK, 2, "Evader & Monnok", Uniqueness.UNRESTRICTED, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        addComboCardTitles(Title.Evader, Title.Monnok);
        setGameText("USED: Cancel all Revolutions in play. OR If opponent has 13 or more cards in hand, place all but 8 (random selection) in Used Pile. LOST: Use 4 Force to reveal opponent's hand. All cards opponent has two or more of in hand are lost.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Revolution)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel all Revolutions in play");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            final Collection<PhysicalCard> revolutions = Filters.filterActive(game, self, null, TargetingReason.TO_BE_CANCELED, Filters.Revolution);

                            // Perform result(s)
                            action.appendEffect(
                                    new CancelCardsOnTableEffect(action, revolutions));
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.numCardsInHand(game, opponent) >= 13) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place random cards in Used Pile");
            // Allow response(s)
            action.allowResponses("Place random cards from opponent's hand in Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutRandomCardsFromHandOnUsedPileEffect(action, playerId, opponent, 8));
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.hasHand(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 4)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Reveal opponent's hand");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 4));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RevealOpponentsHandEffect(action, playerId) {
                                        @Override
                                        protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                                            action.appendEffect(
                                                    new LoseCardsFromHandEffect(action, opponent, Filters.and(Filters.duplicatesOfInHand(opponent), Filters.canBeTargetedBy(self))));
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}