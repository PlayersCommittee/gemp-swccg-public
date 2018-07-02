package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.*;

/**
 * An effect that causes the specified player to choose cards from hand.
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the cards selected or any targeting
 * reasons. This is just choosing cards, and calling the cardsSelected method with the collection of cards chosen.
 */
public abstract class ChooseCardsFromHandEffect extends AbstractStandardEffect implements TargetingEffect {
    private String _playerId;
    private String _handOwner;
    private int _minimum;
    private int _maximum;
    private int _maximumAcceptsCount;
    private boolean _matchPartialModelType;
    private Filterable _filters;
    private boolean _forDeployment;
    private boolean _skipIfNoneFound;
    private boolean _cardSelectionFailed;

    /**
     * Creates an effect that causes the player to choose cards from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     */
    public ChooseCardsFromHandEffect(Action action, String playerId, int minimum, int maximum) {
        this(action, playerId, minimum, maximum, Filters.any);
    }

    /**
     * Creates an effect that causes the player to choose cards from hand accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseCardsFromHandEffect(Action action, String playerId, int minimum, int maximum, Filterable filters) {
        this(action, playerId, playerId, minimum, maximum, filters, false, false);
    }

    /**
     * Creates an effect that causes the player to choose cards from specified player's hand accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param handOwner the handOwner
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     * @param forDeployment true if for deployment, otherwise false
     * @param skipIfNoneFound true if opponents hand is not shown if no cards match the filter
     */
    public ChooseCardsFromHandEffect(Action action, String playerId, String handOwner, int minimum, int maximum, Filterable filters, boolean forDeployment, boolean skipIfNoneFound) {
        super(action);
        _playerId = playerId;
        _handOwner = handOwner;
        _minimum = minimum;
        _maximum = maximum;
        _maximumAcceptsCount = maximum;
        _matchPartialModelType = false;
        _filters = filters;
        _forDeployment = forDeployment;
        _skipIfNoneFound = skipIfNoneFound;
    }

    public String getChoiceText(int numCardsToChoose) {
        return "Choose card" + GameUtils.s(numCardsToChoose) + " from hand";
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getHand(_handOwner).isEmpty();
    }

    public boolean isPerformedEvenIfMinimumNotReached() {
        return false;
    }

    public boolean isForStealAndDeploy() {
        return false;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        // Determine the cards to choose from
        List<PhysicalCard> cardsInHand = new ArrayList<PhysicalCard>();
        cardsInHand.addAll(game.getGameState().getHand(_handOwner));
        if (_forDeployment) {
            cardsInHand.addAll(Filters.filter(game.getGameState().getAllStackedCards(), game, Filters.and(Filters.owner(_handOwner), Filters.canDeployAsIfFromHand)));
        }

        Collection<PhysicalCard> selectableCards = Filters.filter(cardsInHand, game, _filters);

        // If this is for steal and deploy, then temporarily change the owner of the cards to searching player before checking filters
        if (isForStealAndDeploy()) {
            selectableCards = Filters.filter(selectableCards, game, Filters.canBeTargetedBy(_action.getActionSource(), TargetingReason.TO_BE_STOLEN));
            for (PhysicalCard cardInPile : cardsInHand) {
                cardInPile.setOwner(_playerId);
            }
        }
        selectableCards = Filters.filter(selectableCards, game, _matchPartialModelType, _filters);

        // Filter cards by accounting for cards with multiple classes
        int acceptsCountSoFar = 0;
        List<PhysicalCard> validCards = new LinkedList<PhysicalCard>();
        for (PhysicalCard selectableCard : selectableCards) {
            int acceptsCount = Filters.and(_filters).acceptsCount(game, selectableCard);
            if (acceptsCount > 0 && acceptsCount <= _maximumAcceptsCount) {
                validCards.add(selectableCard);
                acceptsCountSoFar += acceptsCount;
            }
        }
        selectableCards = validCards;

        // If this is for steal and deploy, then change owner back
        if (isForStealAndDeploy()) {
            for (PhysicalCard cardInPile : cardsInHand) {
                cardInPile.setOwner(_handOwner);
            }
        }

        // Adjust the min and max card counts
        int maximum = Math.min(_maximum, selectableCards.size());
        int minimum = Math.min(_minimum, maximum);

        String choiceText = getChoiceText(maximum);

        if (_handOwner.equals(_playerId)) {

            // Make sure at least the minimum number of cards can be found
            if (acceptsCountSoFar < _minimum) {
                return new FullEffectResult(false);
            }

            if (maximum == 0) {
                cardsSelected(game, Collections.<PhysicalCard>emptySet());
            }
            else if (selectableCards.size() == minimum && !_action.isAllowAbort()) {
                cardsSelected(game, selectableCards);
            }
            else {
                game.getUserFeedback().sendAwaitingDecision(_playerId,
                        new CardsSelectionDecision(choiceText + (_action.isAllowAbort() ? ", or click 'Done' to cancel" : ""), selectableCards, _action.isAllowAbort() ? 0 : minimum, maximum) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                                _cardSelectionFailed = selectedCards.size() < _minimum;
                                if (_cardSelectionFailed && _action.isAllowAbort()) {
                                    return;
                                }

                                cardsSelected(game, selectedCards);
                            }
                        }
                );
            }
        }
        else if (!_skipIfNoneFound || !selectableCards.isEmpty()) {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new ArbitraryCardsSelectionDecision(choiceText + (_action.isAllowAbort() ? ", or click 'Done' to cancel" : ""), cardsInHand, selectableCards, minimum, maximum) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                            _cardSelectionFailed = selectedCards.size() < _minimum;
                            if (_cardSelectionFailed && _action.isAllowAbort()) {
                                return;
                            }

                            cardsSelected(game, selectedCards);
                        }
                    }
            );
        }

        return new FullEffectResult(true);
    }

    @Override
    public boolean wasCarriedOut() {
        return super.wasCarriedOut() && !_cardSelectionFailed;
    }

    /**
     * This method is called when cards have been selected.
     * @param game the game
     * @param selectedCards the selected cards
     */
    protected abstract void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards);
}
