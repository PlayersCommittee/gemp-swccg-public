package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealOpponentsHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Grimtaash
 */
public class Card2_049 extends AbstractUsedOrLostInterrupt {
    public Card2_049() {
        super(Side.LIGHT, 2, Title.Grimtaash, Uniqueness.UNIQUE);
        setLore("Dejarik representation of mythical Molator guardian. The spirit of Grimtaash is said to protect Alderaanian royalty from corruption and betrayal.");
        setGameText("USED: If opponent has 13 or more cards in hand, place all but 8 (random selection) in Used Pile. LOST: Cancel Molator (even at a holosite). OR Use 4 Force to reveal opponent's hand. All cards opponent has two or more of in hand are lost.");
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
        if (GameConditions.canTargetToCancel(game, self, Filters.Molator)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Molator, Title.Molator);
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
                            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.GRIMTAASH__PUT_TWO_CARDS_IN_USED)) {
                                action.appendEffect(
                                        new PutCardsFromHandOnUsedPileEffect(action, opponent, 0, 2)
                                );
                            }
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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Molator)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}