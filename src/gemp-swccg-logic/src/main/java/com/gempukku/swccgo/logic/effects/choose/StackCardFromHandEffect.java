package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.StackOneCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * An effect that causes the specified player to choose and stack a card from hand on a specified card.
 */
public class StackCardFromHandEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _stackOn;
    private Filterable _cardFilter;
    private boolean _faceDown;
    private boolean _isProbeCard;
    private boolean _isBluffCard;
    private boolean _isCombatCard;
    private boolean _isBuriedMine;
    private boolean _hidden;
    private StackCardFromHandEffect _that;

    public StackCardFromHandEffect(Action action, String playerId, PhysicalCard stackOn) {
        this(action, playerId, stackOn, Filters.any);
    }

    public StackCardFromHandEffect(Action action, String playerId, PhysicalCard stackOn, Filterable cardFilter) {
        this(action, playerId, stackOn, cardFilter, false, false, false, false);
    }

    public StackCardFromHandEffect(Action action, String playerId, PhysicalCard stackOn, Filterable cardFilter, boolean faceDown, boolean isProbeCard, boolean isBluffCard, boolean isCombatCard) {
        this(action, playerId, stackOn, cardFilter, faceDown, isProbeCard, isBluffCard, isCombatCard, false, true);
    }

    public StackCardFromHandEffect(Action action, String playerId, PhysicalCard stackOn, Filterable cardFilter, boolean faceDown, boolean isProbeCard, boolean isBluffCard, boolean isCombatCard, boolean hidden) {
        this(action, playerId, stackOn, cardFilter, faceDown, isProbeCard, isBluffCard, isCombatCard, false, hidden);
    }

    public StackCardFromHandEffect(Action action, String playerId, PhysicalCard stackOn, Filterable cardFilter, boolean faceDown, boolean isProbeCard, boolean isBluffCard, boolean isCombatCard, boolean isBuriedMine, boolean hidden) {
        super(action);
        _playerId = playerId;
        _stackOn = stackOn;
        _cardFilter = cardFilter;
        _faceDown = faceDown;
        _isProbeCard = isProbeCard;
        _isBluffCard = isBluffCard;
        _isCombatCard = isCombatCard;
        _isBuriedMine = isBuriedMine;
        _hidden = hidden;
        _that = this;
    }

    public String getChoiceText() {
        return "Choose card to stack";
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        List<PhysicalCard> cardsInHand = new ArrayList<PhysicalCard>(Filters.filter(gameState.getHand(_playerId), game, _cardFilter));

                        if (!cardsInHand.isEmpty()) {
                            if (cardsInHand.size() == 1) {
                                PhysicalCard card = cardsInHand.get(0);
                                subAction.appendEffect(new StackOneCardFromHandEffect(subAction, card, _stackOn, _faceDown, _isProbeCard, _isBluffCard, _isCombatCard, _isBuriedMine, _hidden));
                            }
                            else {
                                game.getUserFeedback().sendAwaitingDecision(_playerId,
                                        new CardsSelectionDecision(_that.getChoiceText(), cardsInHand, 1, 1) {
                                            @Override
                                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                                List<PhysicalCard> cards = getSelectedCardsByResponse(result);
                                                for (PhysicalCard card : cards) {
                                                    subAction.appendEffect(new StackOneCardFromHandEffect(subAction, card, _stackOn, _faceDown, _isProbeCard, _isBluffCard, _isCombatCard, _isBuriedMine, _hidden));
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
