package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An effect that causes the specified player to choose cards from a sabacc hand.
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the cards selected or any targeting
 * reasons. This is just choosing cards, and calling the cardsSelected method with the collection of cards chosen.
 */
public abstract class ChooseCardsFromSabaccHandEffect extends AbstractStandardEffect {
    private String _playerId;
    private String _handOwner;
    private int _minimum;
    private int _maximum;
    private Filterable _filters;
    private boolean _shortcut;

    /**
     * Creates an effect that causes the player to choose cards from a sabacc hand.
     * @param action the action performing this effect
     * @param playerId the player choosing cards
     * @param handOwner the owner of the sabacc hand
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseCardsFromSabaccHandEffect(Action action, String playerId, String handOwner, int minimum, int maximum, Filterable filters) {
        this(action, playerId, handOwner, minimum, maximum, true, filters);
    }

    /**
     * Creates an effect that causes the player to choose cards from a sabacc hand accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player choosing cards
     * @param handOwner the owner of the sabacc hand
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param shortcut true if shortcut, otherwise false
     * @param filters the filter
     */
    public ChooseCardsFromSabaccHandEffect(Action action, String playerId, String handOwner, int minimum, int maximum, boolean shortcut, Filterable filters) {
        super(action);
        _playerId = playerId;
        _handOwner = handOwner;
        _minimum = minimum;
        _maximum = maximum;
        _shortcut = shortcut;
        _filters = filters;
    }

    public String getChoiceText(int numCardsToChoose) {
        return "Choose card" + GameUtils.s(numCardsToChoose);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getSabaccHand(_handOwner).isEmpty();
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        // Determine the cards to choose from
        Collection<PhysicalCard> selectableCards = Filters.filter(game.getGameState().getSabaccHand(_handOwner), game, _filters);

        // Make sure at least the minimum number of cards can be found
        if (selectableCards.size() < _minimum) {
            return new FullEffectResult(false);
        }

        // Adjust the min and max card counts
        int maximum = Math.min(_maximum, selectableCards.size());
        int minimum = _minimum;

        if (maximum == 0) {
            cardsSelected(game, Collections.<PhysicalCard>emptySet());
        }
        else if (selectableCards.size() == minimum && _shortcut) {
            cardsSelected(game, selectableCards);
        }
        else {
            String choiceText = getChoiceText(maximum);

            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new CardsSelectionDecision(choiceText, selectableCards, minimum, maximum) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                            cardsSelected(game, selectedCards);
                        }
                    });
        }

        return new FullEffectResult(true);
    }

    protected abstract void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards);
}
