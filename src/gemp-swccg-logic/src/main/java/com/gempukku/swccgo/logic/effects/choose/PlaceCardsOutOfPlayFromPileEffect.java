package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.RemovedFromCardPileResult;

import java.util.Collection;

/**
 * An effect to place cards out of play from the specified card pile.
 */
abstract class PlaceCardsOutOfPlayFromPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private int _minimum;
    private int _maximum;
    private boolean _topmost;
    private Filterable _filters;
    private Zone _zone;
    private String _cardPileOwner;
    private boolean _reshuffle;
    private int _numPlacedOutOfPlay;
    private PlaceCardsOutOfPlayFromPileEffect _that;

    /**
     * Creates an effect that causes the player to place cards out of play from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to take into hand
     * @param maximum the maximum number of cards to take into hand
     * @param zone the card pile to take cards from
     * @param cardPileOwner the card pile owner
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected PlaceCardsOutOfPlayFromPileEffect(Action action, String playerId, int minimum, int maximum, Zone zone, String cardPileOwner, boolean reshuffle) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _zone = zone;
        _cardPileOwner = cardPileOwner;
        _topmost = false;
        _filters = Filters.any;
        _reshuffle = reshuffle;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to take cards accepted by the specified filter into hand from the specified
     * card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to take into hand
     * @param maximum the maximum number of cards to take into hand
     * @param zone the card pile to take cards from
     * @param cardPileOwner the card pile owner
     * @param topmost true if only the topmost cards should be chosen from, otherwise false
     * @param filters the filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected PlaceCardsOutOfPlayFromPileEffect(Action action, String playerId, int minimum, int maximum, Zone zone, String cardPileOwner, boolean topmost, Filterable filters, boolean reshuffle) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _zone = zone;
        _cardPileOwner = cardPileOwner;
        _topmost = topmost;
        _filters = Filters.and(filters, Filters.canBeTargetedBy(action.getActionSource(), TargetingReason.TO_BE_PLACED_OUT_OF_PLAY));
        _reshuffle = reshuffle;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    public boolean isPerformedEvenIfMinimumNotReached() {
        return false;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        subAction.appendEffect(new ChooseCardsFromPileEffect(subAction, _playerId, _zone, _cardPileOwner, _minimum, _maximum, _maximum, true, _topmost, _filters) {
            @Override
            public String getChoiceText(int numCardsToChoose) {
                return "Choose card" + GameUtils.s(numCardsToChoose) + " to place out of play";
            }
            @Override
            public boolean isPerformedEvenIfMinimumNotReached() {
                return _that.isPerformedEvenIfMinimumNotReached();
            }
            @Override
            protected void cardsSelected(final SwccgGame game, final Collection<PhysicalCard> cards) {
                if (!cards.isEmpty()) {
                    _numPlacedOutOfPlay = cards.size();
                    subAction.insertEffect(
                            new PlaceCardsOutOfPlayFromOffTableEffect(subAction, cards),
                            new PassthruEffect(subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    cardsPlacedOutOfPlay(cards);
                                }
                            }
                    );
                }
            }
        });
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Shuffle the card pile
                        if (_reshuffle) {
                            subAction.insertEffect(
                                    new ShufflePileEffect(subAction, subAction.getActionSource(), _playerId, _playerId, _zone, true));
                        }
                        else {
                            game.getActionsEnvironment().emitEffectResult(
                                    new RemovedFromCardPileResult(subAction));
                        }
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _numPlacedOutOfPlay >= _minimum;
    }

    /**
     * A callback method for the cards placed out of play.
     * @param cards the cards placed out of play
     */
    protected void cardsPlacedOutOfPlay(Collection<PhysicalCard> cards) {
    }
}
