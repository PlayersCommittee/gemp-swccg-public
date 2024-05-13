package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An effect that causes the specified player to choose specific cards (using card selection).
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the cards selected or any targeting
 * reasons. This is just choosing cards, and calling the cardsSelected method with the card chosen.
 */
public abstract class ChooseCardsEffect extends AbstractStandardEffect implements TargetingEffect {
    private String _playerId;
    private int _minimum;
    private int _maximum;
    private Collection<PhysicalCard> _cards;
    private String _choiceText;
    private boolean cardSelectionFailed;

    /**
     * Creates an effect that causes the player to choose cards from the specified collection of cards (using card selection).
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the cards
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param cards the cards to choose from
     */
    public ChooseCardsEffect(Action action, String playerId, String choiceText, int minimum, int maximum, Collection<PhysicalCard> cards) {
        super(action);
        _playerId = playerId;
        _choiceText = choiceText;
        _minimum = minimum;
        _maximum = maximum;
        _cards = cards;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return _cards.size() >= _minimum;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        // Determine the cards to choose from
        Collection<PhysicalCard> selectableCards = _cards;

        // Make sure at least the minimum number of cards can be found
        if (selectableCards.size() < _minimum) {
            return new FullEffectResult(false);
        }

        // Adjust the min and max card counts
        int maximum = Math.min(_maximum, selectableCards.size());
        final int minimum = _minimum;

        if (maximum == 0) {
            cardsSelected(Collections.<PhysicalCard>emptySet());
        }
        else if (selectableCards.size() == minimum && !_action.isAllowAbort()) {
            cardsSelected(selectableCards);
        }
        else {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new CardsSelectionDecision(_choiceText + (_action.isAllowAbort() ? ", or click 'Done' to cancel" : ""), selectableCards, _action.isAllowAbort() ? 0 : minimum, maximum) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                            cardSelectionFailed = selectedCards.size() < minimum;
                            if (cardSelectionFailed && _action.isAllowAbort()) {
                                return;
                            }

                            cardsSelected(selectedCards);
                        }
                    });
        }

        return new FullEffectResult(true);
    }

    @Override
    public boolean wasCarriedOut() {
        return super.wasCarriedOut() && !cardSelectionFailed;
    }

    /**
     * This method is called when cards have been selected.
     * @param selectedCards the selected cards
     */
    protected abstract void cardsSelected(Collection<PhysicalCard> selectedCards);
}
