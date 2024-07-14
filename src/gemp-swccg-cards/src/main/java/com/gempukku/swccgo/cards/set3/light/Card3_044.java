package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardTitleNotPlayedThisTurnCondition;
import com.gempukku.swccgo.cards.conditions.PlayersNextTurnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardsToHandFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Lost
 * Title: It Can Wait
 */
public class Card3_044 extends AbstractLostInterrupt {
    public Card3_044() {
        super(Side.LIGHT, 2, Title.It_Can_Wait, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.C2);
        setLore("'Sir, might I suggest that you - It can wait.'");
        setGameText("Use 3 Force to place an opponent's just deployed character, starship, vehicle, weapon, or device in opponent's hand. On opponent's next turn, that card (or one card of same title) may deploy for free.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter filter = Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle, Filters.weapon, Filters.device), Filters.canBeTargetedBy(self));

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, filter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {
            final PlayCardResult playCardResult = (PlayCardResult) effectResult;
            final PhysicalCard playedCard = playCardResult.getPlayedCard();
            final List<PhysicalCard> cardsToReturn = new LinkedList<PhysicalCard>();
            cardsToReturn.add(playCardResult.getPlayedCard());
            PhysicalCard otherPlayedCard = playCardResult.getOtherPlayedCard();
            if (otherPlayedCard != null && filter.accepts(game, otherPlayedCard)) {
                cardsToReturn.add(otherPlayedCard);
            }

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Return " + GameUtils.getFullName(playedCard) + " to hand");
            action.addAnimationGroup(cardsToReturn);
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses("Return " + GameUtils.getAppendedNames(cardsToReturn) + " to opponent's hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ReturnCardsToHandFromTableSimultaneouslyEffect(action, cardsToReturn, true));
                            for (PhysicalCard cardReturned : cardsToReturn) {
                                action.appendEffect(
                                        new AddUntilEndOfPlayersNextTurnModifierEffect(action, opponent,
                                                new DeploysFreeModifier(self, Filters.sameTitle(cardReturned),
                                                        new AndCondition(new PlayersNextTurnCondition(opponent, game), new CardTitleNotPlayedThisTurnCondition(cardReturned))), null));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}