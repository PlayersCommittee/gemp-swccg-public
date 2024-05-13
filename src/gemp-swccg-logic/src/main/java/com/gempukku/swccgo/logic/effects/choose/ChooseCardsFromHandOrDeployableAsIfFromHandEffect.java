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
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An effect that causes the specified player to choose cards accepted by the specified filter that are in hand or stacked
 * on another card and deployable as if from hand.
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the cards selected or any targeting
 * reasons. This is just choosing cards, and calling the cardsSelected method with the collection of cards chosen.
 */
public abstract class ChooseCardsFromHandOrDeployableAsIfFromHandEffect extends AbstractStandardEffect implements TargetingEffect {
    private String _playerId;
    private int _minimum;
    private int _maximum;
    private Filterable _filters;

    /**
     * Creates an effect that causes the player to choose cards from hand or deployable as if from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     */
    public ChooseCardsFromHandOrDeployableAsIfFromHandEffect(Action action, String playerId, int minimum, int maximum) {
        this(action, playerId, minimum, maximum, Filters.any);
    }

    /**
     * Creates an effect that causes the player to choose cards from hand or deployable as if from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseCardsFromHandOrDeployableAsIfFromHandEffect(Action action, String playerId, int minimum, int maximum, Filterable filters) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _filters = filters;
    }

    public String getChoiceText(int numCardsToChoose) {
        return "Choose card" + GameUtils.s(numCardsToChoose);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getHand(_playerId).isEmpty()
                && !Filters.filterCount(game.getGameState().getAllStackedCards(), game,
                1, Filters.and(Filters.owner(_playerId), Filters.canDeployAsIfFromHand)).isEmpty();
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        // Determine the cards to choose from
        List<PhysicalCard> selectableCards = new ArrayList<PhysicalCard>();
        selectableCards.addAll(Filters.filter(game.getGameState().getHand(_playerId), game, _filters));
        selectableCards.addAll(Filters.filter(game.getGameState().getAllStackedCards(), game,
                Filters.and(_filters, Filters.owner(_playerId), Filters.canDeployAsIfFromHand)));

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
        else if (selectableCards.size() == minimum) {
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
