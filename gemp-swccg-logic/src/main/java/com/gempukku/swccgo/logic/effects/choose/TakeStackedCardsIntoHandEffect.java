package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.RemovedFromStackedResult;
import com.gempukku.swccgo.logic.timing.results.StolenResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An effect to take stacked cards into hand.
 */
class TakeStackedCardsIntoHandEffect extends AbstractSubActionEffect {
    private String _playerId;
    private int _minimum;
    private int _maximum;
    private PhysicalCard _stackedOn;
    private Filterable _stackedOnFilters;
    private Filterable _filters;
    private boolean _hidden;
    private int _takenIntoHandSoFar;
    private List<PhysicalCard> _cardsStolen = new ArrayList<PhysicalCard>();
    private TakeStackedCardsIntoHandEffect _that;

    /**
     * Creates an effect that causes the player to take cards stacked on the specified card into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to take into hand
     * @param maximum the maximum number of cards to take into hand
     * @param stackedOn the card that the stacked cards are stacked on
     */
    protected TakeStackedCardsIntoHandEffect(Action action, String playerId, int minimum, int maximum, PhysicalCard stackedOn) {
        this(action, playerId, minimum, maximum, stackedOn, Filters.any);
        _hidden = true;
    }

    /**
     * Creates an effect that causes the player to take cards accepted by the specified filter that are stacked on the specified
     * card into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to take into hand
     * @param maximum the maximum number of cards to take into hand
     * @param stackedOn the card that the stacked cards are stacked on
     * @param filters the filter
     */
    protected TakeStackedCardsIntoHandEffect(Action action, String playerId, int minimum, int maximum, PhysicalCard stackedOn, Filterable filters) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _stackedOn = stackedOn;
        _filters = filters;
        _hidden = false;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to take cards stacked on a card accepted by the specified stackedOn filter
     * into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to take into hand
     * @param maximum the maximum number of cards to take into hand
     * @param stackedOnFilters the stackedOn filter
     */
    protected TakeStackedCardsIntoHandEffect(Action action, String playerId, int minimum, int maximum, Filterable stackedOnFilters) {
        this(action, playerId, minimum, maximum, stackedOnFilters, Filters.any);
        _hidden = true;
    }

    /**
     * Creates an effect that causes the player to take cards accepted by the specified filter that are stacked on the specified
     * card into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to take into hand
     * @param maximum the maximum number of cards to take into hand
     * @param stackedOnFilters the stackedOn filter
     * @param filters the filter
     */
    protected TakeStackedCardsIntoHandEffect(Action action, String playerId, int minimum, int maximum, Filterable stackedOnFilters, Filterable filters) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _stackedOnFilters = stackedOnFilters;
        _filters = filters;
        _hidden = false;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(getChooseOneStackedCardToTakeIntoHandEffect(subAction));
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        game.getActionsEnvironment().emitEffectResult(
                                new RemovedFromStackedResult(subAction));
                        for (PhysicalCard cardStolen : _cardsStolen) {
                            game.getActionsEnvironment().emitEffectResult(
                                    new StolenResult(_playerId, cardStolen, null));
                        }
                    }
                }
        );
        return subAction;
    }

    private StandardEffect getChooseOneStackedCardToTakeIntoHandEffect(final SubAction subAction) {
        if (_stackedOn != null) {
            return new ChooseStackedCardsEffect(_action, _playerId, _stackedOn, _takenIntoHandSoFar < _minimum ? 1 : 0, 1, _filters) {
                @Override
                protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                    if (!cards.isEmpty()) {
                        _that.cardsSelected(subAction, game, cards.iterator().next());
                    }
                }
            };
        }
        else {
            return new ChooseStackedCardsEffect(_action, _playerId, _stackedOnFilters, _takenIntoHandSoFar < _minimum ? 1 : 0, 1, _filters) {
                @Override
                protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                    if (!cards.isEmpty()) {
                        _that.cardsSelected(subAction, game, cards.iterator().next());
                    }
                }
            };
        }
    }

    private void cardsSelected(final SubAction subAction, final SwccgGame game, final PhysicalCard card) {
        String cardInfo = (_hidden && card.getZone().isFaceDown()) ? "a card" : GameUtils.getCardLink(card);
        final boolean isStealing = !card.getOwner().equals(_playerId);
        String takesText = isStealing ? " steals " : " takes ";
        String msgText = _playerId + takesText + cardInfo + " into hand from " + GameUtils.getCardLink(card.getStackedOn());
        subAction.appendEffect(
                new TakeOneCardIntoHandFromOffTableEffect(subAction, _playerId, card, msgText) {
                    @Override
                    protected void afterCardTakenIntoHand() {
                        _takenIntoHandSoFar++;
                        if (isStealing) {
                            _cardsStolen.add(card);
                        }
                        if (_takenIntoHandSoFar < _maximum
                                && _that.isPlayableInFull(game)) {
                            subAction.appendEffect(
                                    getChooseOneStackedCardToTakeIntoHandEffect(subAction));
                        }
                    }
                });
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _takenIntoHandSoFar >= _minimum;
    }
}
