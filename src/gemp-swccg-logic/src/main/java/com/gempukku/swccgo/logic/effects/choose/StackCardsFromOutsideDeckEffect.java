package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An effect that stacks cards from outside of deck on a card.
 */
public class StackCardsFromOutsideDeckEffect extends AbstractSubActionEffect {
    private String _playerId;
    private int _min;
    private int _max;
    private PhysicalCard _stackOn;
    private boolean _faceDown;
    private Filterable _cardFilter;
    private Collection<PhysicalCard> _cardsStacked = new ArrayList<PhysicalCard>();
    private boolean _stackAsManyAsPossible;
    private StackCardsFromOutsideDeckEffect _that;

    /**
     * Creates an effect that stacks cards from outside of deck on a specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param min the minimum cards to stack
     * @param max the maximum cards to stack
     * @param stackOn the card to stack on
     */
    public StackCardsFromOutsideDeckEffect(Action action, String playerId, int min, int max, PhysicalCard stackOn) {
        this(action, playerId, min, max, stackOn, Filters.any);
    }

    /**
     * Creates an effect that stacks cards from outside of deck accepted by the filter on a specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param min the minimum cards to stack
     * @param max the maximum cards to stack
     * @param stackOn the card to stack on
     * @param cardFilter the filter for cards to be stacked
     */
    public StackCardsFromOutsideDeckEffect(Action action, String playerId, int min, int max, PhysicalCard stackOn, Filterable cardFilter) {
        this(action, playerId, min, max, stackOn, true, cardFilter);
    }

    /**
     * Creates an effect that stacks cards from outside of deck accepted by the filter on a specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param min the minimum cards to stack
     * @param max the maximum cards to stack
     * @param stackOn the card to stack on
     * @param faceDown true if the card will be stacked face down
     * @param cardFilter the filter for cards to be stacked
     */
    public StackCardsFromOutsideDeckEffect(Action action, String playerId, int min, int max, PhysicalCard stackOn, boolean faceDown, Filterable cardFilter) {
        super(action);
        _playerId = playerId;
        _min = min;
        _max = max;
        _stackOn = stackOn;
        _faceDown = faceDown;
        _cardFilter = cardFilter;
        _stackAsManyAsPossible = true;
        _that = this;
    }

    public String getChoiceText(int numCardsToChoose) {
        return "Choose card" + GameUtils.s(numCardsToChoose) + " to stack under " + GameUtils.getCardLink(_stackOn);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action, _playerId);

        final Collection<PhysicalCard> outsideOfDeck = Filters.filter(game.getGameState().getOutsideOfDeck(_playerId), game, _cardFilter);
        if (outsideOfDeck.size() >= _min) {
            final int numCardsToStack = Math.min(_max, outsideOfDeck.size());
            if (_stackAsManyAsPossible && numCardsToStack >= outsideOfDeck.size()) {
                // Just stack them all
                subAction.appendEffect(
                        new PassthruEffect(subAction) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                for (PhysicalCard card : outsideOfDeck) {
                                    gameState.removeCardFromZone(card);
                                    gameState.stackCard(card, _stackOn, _faceDown, false, false);
                                    _cardsStacked.add(card);
                                }
                                gameState.sendMessage(_playerId + " stacks " + _cardsStacked.size() + " card" + GameUtils.s(_cardsStacked.size()) + " from outside of deck " + (_faceDown ? " face down" : "") + " under " + GameUtils.getCardLink(_stackOn));
                            }
                        }
                );
            }
            else {
                subAction.appendEffect(
                        new PlayoutDecisionEffect(subAction, _playerId,
                                new ArbitraryCardsSelectionDecision(_that.getChoiceText(_max), outsideOfDeck, _min, numCardsToStack) {
                                    @Override
                                    public void decisionMade(String result) throws DecisionResultInvalidException {
                                        List<PhysicalCard> cards = getSelectedCardsByResponse(result);
                                        for (PhysicalCard card : cards) {
                                            gameState.removeCardFromZone(card);
                                            gameState.stackCard(card, _stackOn, _faceDown, false, false);
                                            _cardsStacked.add(card);
                                        }
                                        gameState.sendMessage(_playerId + " stacks " + _cardsStacked.size() + " card" + GameUtils.s(_cardsStacked.size()) + " from outside of deck " + (_faceDown ? " face down" : "") + " under " + GameUtils.getCardLink(_stackOn));
                                    }
                                }
                        )
                );
            }
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _cardsStacked.size() >= _min;
    }
}