package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Effect
 * Title: Information Exchange
 */
public class Card6_145 extends AbstractNormalEffect {
    public Card6_145() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Information Exchange", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("Chisa nyooda ishaley. Kun Jabba neguda len Malta.' 'Ikkit ui! Yobbit, yobbiy. Nelan tui ke bada.'");
        setGameText("Deploy on your side of table. Whenever you have an information broker at a location where you just initiated a battle, you may examine the top four cards of opponent's Reserve Deck. You may then lose 1 Force to immediately cancel the battle.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        String opponent = game.getOpponent(playerId);
        int numCards = 4;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerId, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.information_broker)))
                && GameConditions.hasReserveDeck(game, opponent)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Examine top cards of opponent's Reserve Deck");
            // Perform result(s)
            action.appendEffect(new PeekAtTopCardsOfReserveDeckEffect(action, playerId, opponent, numCards){
                @Override
                protected void cardsPeekedAt(List<PhysicalCard> peekedAtCards) {
                    if(peekedAtCards.size() == numCards) {
                        action.appendEffect(
                                new PlayoutDecisionEffect(action, playerId,
                                        new YesNoDecision("Lose 1 Force to cancel this battle?") {
                                            @Override
                                            protected void yes() {
                                                final SubAction subAction = new SubAction(action, playerId);
                                                // Pay cost(s)
                                                subAction.appendCost(
                                                        new LoseForceEffect(subAction, playerId, 1, true));
                                                // Perform result(s)
                                                subAction.appendEffect(
                                                        new CancelBattleEffect(subAction));
                                                action.appendEffect(
                                                        new StackActionEffect(action, subAction));
                                            }
                                            @Override
                                            protected void no() {
                                                game.getGameState().sendMessage(playerId + " chooses to not cancel this battle.");
                                            }
                                        }));
                    }
                    else {
                        action.appendEffect(
                                new SendMessageEffect(action, playerId + " failed to peek at four cards."));
                    }
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
