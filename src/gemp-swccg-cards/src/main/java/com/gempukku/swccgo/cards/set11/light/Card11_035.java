package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealOpponentsHandEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromHandEffect;
import com.gempukku.swccgo.logic.effects.PlaceTopCardOfUsedPileOnTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Eventually You'll Lose
 */
public class Card11_035 extends AbstractUsedOrLostInterrupt {
    public Card11_035() {
        super(Side.LIGHT, 4, "Eventually You'll Lose", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.U);
        setLore("In the end, Watto finally came to understand the agony of defeat.");
        setGameText("USED: If a battle was just initiated, place the top card of your Used Pile on top of your Reserve Deck. LOST: Use 3 Force to cause opponent to reveal hand. All cards opponent has 3 or more of in hand are lost.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.hasUsedPile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place top card of Used Pile on Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlaceTopCardOfUsedPileOnTopOfReserveDeckEffect(action, playerId));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasHand(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Reveal opponent's hand");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
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
                                                    new LoseCardsFromHandEffect(action, opponent, Filters.and(Filters.threeOrMoreOfInHand(opponent), Filters.canBeTargetedBy(self))));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}