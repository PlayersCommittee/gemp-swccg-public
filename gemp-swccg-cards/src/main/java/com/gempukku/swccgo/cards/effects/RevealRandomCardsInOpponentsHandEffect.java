package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.LookAtCardsInOpponentsHandEffect;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * An effect that reveals random cards from the opponent's hand.
 */
public class RevealRandomCardsInOpponentsHandEffect extends AbstractStandardEffect implements LookAtCardsInOpponentsHandEffect {
    private String _playerId;
    private int _count;

    /**
     * Creates an effect that reveals random cards from the opponent's hand.
     * @param action the action performing this effect
     * @param playerId the player revealing cards from opponent's hand
     * @param count the number of cards to reveal
     */
    public RevealRandomCardsInOpponentsHandEffect(Action action, String playerId, int count) {
        super(action);
        _playerId = playerId;
        _count = count;
    }

    @Override
    public Action getAction() {
        return _action;
    }

    @Override
    public PhysicalCard getCardAllowingScan() {
        return _action.getActionSource();
    }

    @Override
    public Type getType() {
        return Type.BEFORE_LOOKING_AT_OPPONENTS_HAND;
    }

    @Override
    public String getText(SwccgGame game) {
        if (_count == 1)
            return "Reveal a random card in opponent's hand";
        else
            return "Reveal " + _count + " random cards from opponent's hand";
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        String opponent = game.getOpponent(_playerId);
        return game.getGameState().getHand(opponent).isEmpty();
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        String opponent = game.getOpponent(_playerId);

        final List<PhysicalCard> randomCards = GameUtils.getRandomCards(game.getGameState().getHand(opponent), _count);
        if (!randomCards.isEmpty()) {
            game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " revealed " + GameUtils.getAppendedNames(randomCards) + " from " + opponent + "'s hand at random");
        }
        String text = "Random card" + GameUtils.s(randomCards.size()) + " from opponent's hand";

        game.getUserFeedback().sendAwaitingDecision(_playerId,
                new ArbitraryCardsSelectionDecision(text, randomCards, Collections.<PhysicalCard>emptyList(), 0, 0) {
                    @Override
                    public void decisionMade(String result) throws DecisionResultInvalidException {
                        cardsRevealed(randomCards);
                    }
                });

        return new FullEffectResult(true);
    }

    /**
     * A callback method for the cards revealed.
     * @param revealedCards the card revealed
     */
    protected void cardsRevealed(List<PhysicalCard> revealedCards) {
    }
}
