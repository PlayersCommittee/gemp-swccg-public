package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.RemovedFromStackedResult;

import java.util.Collection;

/**
 * An effect to place stacked cards out of play.
 */
class PlaceStackedCardsOutOfPlayEffect extends AbstractSubActionEffect {
    private String _playerId;
    private int _minimum;
    private int _maximum;
    private PhysicalCard _stackedOn;
    private Filterable _stackedOnFilters;
    private Filterable _filters;

    /**
     * Creates an effect that causes the player to place cards accepted by the specified filter that are stacked on the specified
     * card out of play.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to place out of play
     * @param maximum the maximum number of cards to place out of play
     * @param stackedOn the card that the stacked cards are stacked on
     * @param filters the filter
     */
    protected PlaceStackedCardsOutOfPlayEffect(Action action, String playerId, int minimum, int maximum, PhysicalCard stackedOn, Filterable filters) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _stackedOn = stackedOn;
        _filters = filters;
    }

    /**
     * Creates an effect that causes the player to place cards accepted by the specified filter that are stacked on cards
     * accepted by the stacked on filter out of play.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to place out of play
     * @param maximum the maximum number of cards to place out of play
     * @param stackedOnFilters the stackedOn filter
     * @param filters the filter
     */
    protected PlaceStackedCardsOutOfPlayEffect(Action action, String playerId, int minimum, int maximum, Filterable stackedOnFilters, Filterable filters) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _stackedOnFilters = stackedOnFilters;
        _filters = filters;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(getChooseOneStackedCardToPlaceOutOfPlayEffect(subAction));
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        game.getActionsEnvironment().emitEffectResult(
                                new RemovedFromStackedResult(subAction));
                    }
                }
        );
        return subAction;
    }

    private StandardEffect getChooseOneStackedCardToPlaceOutOfPlayEffect(final SubAction subAction) {
        if (_stackedOn != null) {
            return new ChooseStackedCardsEffect(_action, _playerId, _stackedOn, _minimum, _maximum, _filters) {
                @Override
                protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                    if (!cards.isEmpty()) {
                        subAction.appendEffect(
                                new PlaceCardsOutOfPlayFromOffTableEffect(subAction, cards));
                    }
                }
            };
        }
        else {
            return new ChooseStackedCardsEffect(_action, _playerId, _stackedOnFilters, _minimum, _maximum, _filters) {
                @Override
                protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                    if (!cards.isEmpty()) {
                        subAction.appendEffect(
                                new PlaceCardsOutOfPlayFromOffTableEffect(subAction, cards));
                    }
                }
            };
        }
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
