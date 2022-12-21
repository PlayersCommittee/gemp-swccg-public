package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealRandomCardInOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.CardTypeAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Leesub Sirln
 */
public class Card1_016 extends AbstractAlien {
    public Card1_016() {
        super(Side.LIGHT, 4, 3, 1, 3, 3, "Leesub Sirln", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Leesub is a Qiraash (near-human). Enslaved as child. Limited precognition. Selected by Imperial High Inquisitor Tremayne as Force adept. Escaped and hiding in Mos Eisley.");
        setGameText("Once each turn during your control phase, may use 1 Force to guess a card type and point to a card in opponent's hand. Card must be shown. If guessed correctly, card is lost.");
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.QIRAASH);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasHand(game, game.getOpponent(playerId))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Guess card type in opponent's hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new CardTypeAwaitingDecision(game, "Guess a card type") {
                                @Override
                                protected void cardTypeChosen(final CardType cardType) {
                                    game.getGameState().sendMessage(playerId + " guesses " + cardType.getHumanReadable() + " card type");
                                    action.appendEffect(
                                            new RevealRandomCardInOpponentsHandEffect(action, playerId) {
                                                @Override
                                                protected void cardRevealed(PhysicalCard revealedCard) {
                                                    if (game.getModifiersQuerying().getCardTypes(game.getGameState(), revealedCard).contains(cardType)) {
                                                        action.appendEffect(
                                                                new LoseCardFromHandEffect(action, revealedCard));
                                                    }
                                                }
                                            });
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }
}
