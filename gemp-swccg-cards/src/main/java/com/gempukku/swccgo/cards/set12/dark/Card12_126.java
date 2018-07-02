package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Yeb Yeb Adem'thorn
 */
public class Card12_126 extends AbstractRepublic {
    public Card12_126() {
        super(Side.DARK, 3, 2, 1, 2, 4, "Yeb Yeb Adem'thorn", Uniqueness.UNIQUE);
        setPolitics(2);
        setLore("Senator who opposes the taxation of trade routes. Some say that a recent move from his homeworld to a luxurious Coruscant abode is evidence of his corruption.");
        setGameText("Agendas: trade, wealth. While in a senate majority, once during your turn, may peek at top card of opponent's Reserve Deck; return that card or lose 1 Force to make it lost.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.SENATOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.TRADE, Agenda.WEALTH));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInSenateMajority(game, self)
                && GameConditions.hasReserveDeck(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top of opponent's Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, opponent) {
                        @Override
                        protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                            // Ask player about making card lost
                            action.appendCost(
                                    new PlayoutDecisionEffect(action, playerId,
                                            new YesNoDecision("Do you want to lose 1 Force to make " + GameUtils.getCardLink(peekedAtCard) + " lost?") {
                                                @Override
                                                protected void yes() {
                                                    action.appendCost(
                                                            new LoseForceEffect(action, playerId, 1, true));
                                                    action.setActionMsg("Make top card of opponent's Reserve Deck lost");
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new LoseCardFromTopOfReserveDeckEffect(action, opponent, peekedAtCard));
                                                }
                                                protected void no() {
                                                    action.setActionMsg(null);
                                                    game.getGameState().sendMessage(playerId + " chooses to not make top card of opponent's Reserve Deck lost");
                                                }
                                            }
                                    ));
                       }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
