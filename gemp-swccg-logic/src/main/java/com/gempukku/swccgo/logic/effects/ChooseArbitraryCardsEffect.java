package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * An effect that involves choosing cards from a pop-up window in the User Interface.
 */
public abstract class ChooseArbitraryCardsEffect extends AbstractStandardEffect {
    private String _playerId;
    private String _choiceText;
    private Collection<PhysicalCard> _cards;
    private Filterable _filter;
    private int _minimum;
    private int _maximum;

    /**
     * Creates an effect that involves choosing cards from a pop-up window in the User Interface.
     * @param action the action performing this effect
     * @param playerId the player choosing cards
     * @param choiceText the text shown to the player
     * @param cards the cards shown
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     */
    public ChooseArbitraryCardsEffect(Action action, String playerId, String choiceText, Collection<? extends PhysicalCard> cards, int minimum, int maximum) {
        this(action, playerId, choiceText, cards, Filters.any, minimum, maximum);
    }

    /**
     * Creates an effect that involves choosing cards from a pop-up window in the User Interface.
     * @param action the action performing this effect
     * @param playerId the player choosing cards
     * @param choiceText the text shown to the player
     * @param cards the cards shown
     * @param filter the filter for which shown cards are selectable
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     */
    public ChooseArbitraryCardsEffect(Action action, String playerId, String choiceText, Collection<? extends PhysicalCard> cards, Filterable filter, int minimum, int maximum) {
        super(action);
        _playerId = playerId;
        _choiceText = choiceText;
        _cards = new HashSet<PhysicalCard>(cards);
        _filter = filter;
        _minimum = minimum;
        _maximum = maximum;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return Filters.filter(_cards, game, _filter).size() >= _minimum;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        Collection<PhysicalCard> possibleCards = Filters.filter(_cards, game, _filter);

        boolean success = possibleCards.size() >= _minimum;

        int minimum = _minimum;

        if (possibleCards.size() < minimum)
            minimum = possibleCards.size();

        if (_maximum == 0) {
            cardsSelected(game, Collections.<PhysicalCard>emptySet());
        } else if (possibleCards.size() == minimum) {
            cardsSelected(game, possibleCards);
        } else {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new ArbitraryCardsSelectionDecision(_choiceText, _cards, possibleCards, _minimum, _maximum) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            cardsSelected(game, getSelectedCardsByResponse(result));
                        }
                    });
        }

        return new FullEffectResult(success);
    }

    /**
     * This method is called with the cards selected.
     * @param game the game
     * @param selectedCards the selected cards
     */
    protected abstract void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards);
}
