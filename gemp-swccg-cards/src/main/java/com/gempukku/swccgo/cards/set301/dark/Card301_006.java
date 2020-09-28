package com.gempukku.swccgo.cards.set301.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Premium Set
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Khurgee (V)
 */
public class Card301_006 extends AbstractImperial {
    public Card301_006() {
        super(Side.DARK, 2, 2, 2, 2, 4, "Captain Khurgee", Uniqueness.UNIQUE);
        setLore("Docking bay security officer. Leader. Honored for bravery aboard the Star Destroyer Thunderflare where he rescued five officers from the wreckage of a shuttle crash.");
        setGameText("While aboard Thunderflare, attrition against you is -2 here. Once during your turn, if at a battleground, may peek at top card of opponent's Reserve Deck. If it is a character, may reveal it and opponent loses 1 Force (if it is a Rebel, may also place it on Used Pile).");
        addIcons(Icon.VIRTUAL_SET_P, Icon.A_NEW_HOPE, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.CAPTAIN);
        setMatchingStarshipFilter(Filters.Thunderflare);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String player = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition aboardThunderflare = new AboardCondition(self, Filters.Thunderflare);
        modifiers.add(new AttritionModifier(self, Filters.here(self), aboardThunderflare, -2, player));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isAtLocation(game, self, Filters.battleground)
                && GameConditions.hasReserveDeck(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top card of opponent's Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, opponent) {
                        @Override
                        protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                            if (Filters.character.accepts(game, peekedAtCard)) {
                                // Ask player about putting revealing card
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Do you want to reveal " + GameUtils.getCardLink(peekedAtCard) + " to make opponent lose 1 force?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.setActionMsg("Reveal " + GameUtils.getCardLink(peekedAtCard) + " to make opponent lose 1 force");
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new RevealTopCardOfReserveDeckEffect(action, playerId, opponent));
                                                        action.appendEffect(
                                                                new LoseForceEffect(action, opponent, 1));
                                                        if (Filters.Rebel.accepts(game, peekedAtCard)) {
                                                            action.appendEffect(
                                                                    new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Place card on used pile?") {
                                                                        @Override
                                                                        protected void yes() {
                                                                            action.appendEffect(
                                                                                    new PutCardFromReserveDeckOnTopOfCardPileEffect(action, peekedAtCard, Zone.USED_PILE, false)
                                                                            );
                                                                        }
                                                                    })
                                                            );

                                                        }
                                                    }

                                                    protected void no() {
                                                        action.setActionMsg(null);
                                                        game.getGameState().sendMessage(playerId + " chooses to not reveal top card of opponent's Reserve Deck");
                                                    }
                                                }
                                        ));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}