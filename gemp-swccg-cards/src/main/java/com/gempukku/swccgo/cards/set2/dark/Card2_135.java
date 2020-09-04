package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealOpponentsHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Monnok
 */
public class Card2_135 extends AbstractUsedOrLostInterrupt {
    public Card2_135() {
        super(Side.DARK, 2, Title.Monnok);
        setLore("Dejarik creature. Savage predator from remote deserts of Soccorro. Respected and honored by Soccorran hunters, considered 'good luck' by superstitious smugglers.");
        setGameText("USED: If opponent has 13 or more cards in hand, place all but 8 (random selection) in Used Pile. LOST: Use 4 Force to reveal opponent's hand. All cards opponent has two or more of in hand are lost.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.DEJARIK);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

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
                                            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.MONNOK__PUT_TWO_CARDS_IN_USED)) {
                                                action.appendEffect(
                                                        new PutCardsFromHandOnUsedPileEffect(action, opponent, 0, 2)
                                                );
                                            }
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