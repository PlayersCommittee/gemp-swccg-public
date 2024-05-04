package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.StackOneCardFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.StackOneCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * An effect that causes the specified player to choose and stack a card from hand or Force Pile on a specified card.
 */
public class StackCardFromHandOrForcePileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _stackOn;
    private boolean _faceDown;

    /**
     * Creates an effect that causes the specified player to choose and stack a card from hand or Force Pile on a specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackOn the card to stack a card on
     */
    public StackCardFromHandOrForcePileEffect(Action action, String playerId, PhysicalCard stackOn) {
        this(action, playerId, stackOn, false);
    }

    /**
     * Creates an effect that causes the specified player to choose and stack a card from hand or Force Pile on a specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackOn the card to stack a card on
     * @param faceDown true if stacked face down, otherwise false
     */
    public StackCardFromHandOrForcePileEffect(Action action, String playerId, PhysicalCard stackOn, boolean faceDown) {
        super(action);
        _playerId = playerId;
        _stackOn = stackOn;
        _faceDown = faceDown;
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
                        List<PhysicalCard> handAndForcePile = new ArrayList<PhysicalCard>(gameState.getHand(_playerId));
                        PhysicalCard topOfForcePile = game.getGameState().getTopOfForcePile(_playerId);
                        if (topOfForcePile != null) {
                            handAndForcePile.add(topOfForcePile);
                        }

                        if (!handAndForcePile.isEmpty()) {
                            if (handAndForcePile.size() == 1) {
                                PhysicalCard card = handAndForcePile.get(0);
                                if (card.getZone() == Zone.HAND)
                                    subAction.appendEffect(new StackOneCardFromHandEffect(subAction, card, _stackOn, _faceDown));
                                else
                                    subAction.appendEffect(new StackOneCardFromForcePileEffect(subAction, card, _stackOn, _faceDown));
                            }
                            else {
                                game.getUserFeedback().sendAwaitingDecision(_playerId,
                                        new CardsSelectionDecision("Choose card to stack", handAndForcePile, 1, 1) {
                                            @Override
                                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                                List<PhysicalCard> cards = getSelectedCardsByResponse(result);
                                                for (PhysicalCard card : cards) {
                                                    if (card.getZone() == Zone.HAND)
                                                        subAction.appendEffect(new StackOneCardFromHandEffect(subAction, card, _stackOn, _faceDown));
                                                    else
                                                        subAction.appendEffect(new StackOneCardFromForcePileEffect(subAction, card, _stackOn, _faceDown));
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