package com.gempukku.swccgo.cards.effects.takeandputcards;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collection;

/**
 * An effect to stack cards from hand.
 */
class StackCardsFromHandEffect extends AbstractSubActionEffect {
    private String _playerId;
    private int _minimum;
    private int _maximum;
    private PhysicalCard _stackOn;
    private boolean _faceDown;
    private boolean _hidden;
    private Filterable _filters;
    private int _stackedSoFar;
    private StackCardsFromHandEffect _that;

    /**
     * Creates an effect that causes the player to stack cards from hand on the specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to stack
     * @param maximum the maximum number of cards to stack
     * @param stackOn the card to stack the cards on
     * @param faceDown true if cards are to be stacked face down, otherwise false
     */
    protected StackCardsFromHandEffect(Action action, String playerId, int minimum, int maximum, PhysicalCard stackOn, boolean faceDown) {
        this(action, playerId, minimum, maximum, stackOn, faceDown, Filters.any);
        _hidden = !faceDown;
    }

    /**
     * Creates an effect that causes the player to stack cards accepted by the specified filter from hand on the specified
     * card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to stack
     * @param maximum the maximum number of cards to stack
     * @param stackOn the card to stack the cards on
     * @param faceDown true if cards are to be stacked face down, otherwise false
     * @param filters the filter
     */
    protected StackCardsFromHandEffect(Action action, String playerId, int minimum, int maximum, PhysicalCard stackOn, boolean faceDown, Filterable filters) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _stackOn = stackOn;
        _faceDown = faceDown;
        _filters = filters;
        _hidden = false;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getHand(_playerId).isEmpty();
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(getChooseOneCardToStackEffect(subAction));
        return subAction;
    }

    private StandardEffect getChooseOneCardToStackEffect(final SubAction subAction) {
        return new ChooseCardsFromHandEffect(subAction, _playerId, _stackedSoFar < _minimum ? 1 : 0, 1, _filters) {
            @Override
            public String getChoiceText(int numCardsToChoose) {
                String facing = _faceDown ? "face down " : "";
                return "Choose card" + GameUtils.s(numCardsToChoose) + " to stack " + facing + "on " + GameUtils.getCardLink(_stackOn);
            }
            @Override
            protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                if (!cards.isEmpty()) {
                    PhysicalCard card = cards.iterator().next();
                    String cardInfo = _hidden ? "a card" : GameUtils.getCardLink(card);
                    String facing = _faceDown ? "face down " : "";
                    String msgText = _playerId + " stacks " + cardInfo + " from hand " + facing + "on " + GameUtils.getCardLink(_stackOn);
                    subAction.appendEffect(
                            new StackOneCardFromHandEffect(subAction, card, _stackOn, _faceDown, msgText) {
                                @Override
                                protected void afterCardStacked() {
                                    _stackedSoFar++;
                                    if (_stackedSoFar < _maximum
                                            && _that.isPlayableInFull(game)) {
                                        subAction.appendEffect(
                                                getChooseOneCardToStackEffect(subAction));
                                    }
                                }
                            });
                }
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _stackedSoFar >= _minimum;
    }
}
