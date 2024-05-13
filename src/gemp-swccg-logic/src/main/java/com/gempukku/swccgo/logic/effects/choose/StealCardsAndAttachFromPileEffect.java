package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.RemovedFromCardPileResult;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An effect to steal cards from the opponent's specified card pile and attach them to a card.
 */
abstract class StealCardsAndAttachFromPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _attachTo;
    private int _minimum;
    private int _maximum;
    private boolean _topmost;
    private Filter _filters;
    private Zone _zone;
    private boolean _reshuffle;
    private Collection<PhysicalCard> _cardsStolen = new ArrayList<PhysicalCard>();
    private int _numStolen;
    private StealCardsAndAttachFromPileEffect _that;

    /**
     * Creates an effect that causes the player to steal cards from the specified card pile and attach them to a card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param attachTo the card to attach the stolen cards to
     * @param minimum the minimum number of cards to steal
     * @param maximum the maximum number of cards to steal
     * @param zone the card pile to steal cards from
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected StealCardsAndAttachFromPileEffect(Action action, String playerId, PhysicalCard attachTo, int minimum, int maximum, Zone zone, boolean reshuffle) {
        super(action);
        _playerId = playerId;
        _attachTo = attachTo;
        _minimum = minimum;
        _maximum = maximum;
        _zone = zone;
        _topmost = false;
        _filters = Filters.canBeTargetedBy(_action.getActionSource(), _action.getActionAttachedToCardBuiltIn(), TargetingReason.TO_BE_STOLEN);
        _reshuffle = reshuffle;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to steal cards accepted by the specified filter from the specified card
     * pile and attach them to a card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param attachTo the card to attach the stolen cards to
     * @param minimum the minimum number of cards to steal
     * @param maximum the maximum number of cards to steal
     * @param zone the card pile to steal cards from
     * @param topmost true if only the topmost cards should be chosen from, otherwise false
     * @param filters the filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected StealCardsAndAttachFromPileEffect(Action action, String playerId, PhysicalCard attachTo, int minimum, int maximum, Zone zone, boolean topmost, Filterable filters, boolean reshuffle) {
        super(action);
        _playerId = playerId;
        _attachTo = attachTo;
        _minimum = minimum;
        _maximum = maximum;
        _zone = zone;
        _topmost = topmost;
        _filters = Filters.and(filters, Filters.isUniquenessOnTableNotReached, Filters.canBeTargetedBy(_action.getActionSource(), _action.getActionAttachedToCardBuiltIn(), TargetingReason.TO_BE_STOLEN));
        _reshuffle = reshuffle;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final String cardPileOwner = game.getOpponent(_playerId);
        final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(getChooseOneCardToStealEffect(subAction, cardPileOwner));

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Shuffle the card pile
                        if (_reshuffle) {
                            subAction.insertEffect(
                                    new ShufflePileEffect(subAction, subAction.getActionSource(), _playerId, cardPileOwner, _zone, true));
                        }
                        else if (!_cardsStolen.isEmpty()) {
                            actionsEnvironment.emitEffectResult(
                                    new RemovedFromCardPileResult(subAction));
                        }
                    }
                }
        );
        return subAction;
    }

    private StandardEffect getChooseOneCardToStealEffect(final SubAction subAction, final String cardPileOwner) {
        return new ChooseCardsFromPileEffect(subAction, _playerId, _zone, cardPileOwner, _cardsStolen.size() < _minimum ? 1 : 0, 1, _maximum - _numStolen, false, _topmost, _filters) {
            @Override
            public String getChoiceText(int numCardsToChoose) {
                return "Choose card" + GameUtils.s(numCardsToChoose) + " to steal";
            }
            @Override
            protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                if (!cards.isEmpty()) {
                    final PhysicalCard card = cards.iterator().next();
                    // Increment by acceptsCount since squadrons can count as more than one based on filter
                    final int numToIncrement = _filters.acceptsCount(game, card);
                    subAction.insertEffect(
                            new StealOneCardAndAttachEffect(subAction, card, _attachTo),
                            new PassthruEffect(subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    _cardsStolen.add(card);
                                    _numStolen += numToIncrement;
                                    if (_numStolen < _that._maximum
                                            && _that.isPlayableInFull(game)) {
                                        subAction.insertEffect(
                                                getChooseOneCardToStealEffect(subAction, cardPileOwner));
                                    }
                                }
                            }
                    );
                }
            }
            @Override
            public boolean isSkipTriggerPlayerLookedAtCardsInPile() {
                return _numStolen > 0;
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _numStolen > _minimum;
    }
}
