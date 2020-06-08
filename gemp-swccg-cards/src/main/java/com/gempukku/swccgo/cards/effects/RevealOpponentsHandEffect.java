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
 * An effect that reveals the opponent's hand.
 */
public class RevealOpponentsHandEffect extends AbstractStandardEffect implements LookAtCardsInOpponentsHandEffect {
    private String _playerId;

    /**
     * Creates an effect that reveals opponent's hand.
     * @param action the action performing this effect
     * @param playerId the player revealing cards from opponent's hand
     */
    public RevealOpponentsHandEffect(Action action, String playerId) {
        super(action);
        _playerId = playerId;
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
        return "Reveal opponent's hand";
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        String opponent = game.getOpponent(_playerId);
        return game.getGameState().getHand(opponent).isEmpty();
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        String opponent = game.getOpponent(_playerId);

        final List<PhysicalCard> cards = game.getGameState().getHand(opponent);
        if (!cards.isEmpty()) {
            game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " revealed " + GameUtils.getAppendedNames(cards) + " from " + opponent + "'s hand");
        }

        game.getUserFeedback().sendAwaitingDecision(_playerId,
                new ArbitraryCardsSelectionDecision("Opponent's hand", cards, Collections.<PhysicalCard>emptyList(), 0, 0) {
                    @Override
                    public void decisionMade(String result) throws DecisionResultInvalidException {
                        cardsRevealed(cards);
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
