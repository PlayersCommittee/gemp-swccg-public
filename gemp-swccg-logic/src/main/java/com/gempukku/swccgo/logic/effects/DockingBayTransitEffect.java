package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.DockingBayTransitedResult;
import com.gempukku.swccgo.logic.timing.results.DockingBayTransitingResult;

import java.util.Collection;

/**
 * An effect to move a card using docking bay transit.
 */
public class DockingBayTransitEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Collection<PhysicalCard> _movedCards;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;

    /**
     * Creates an effect to move cards using docking bay transit.
     * @param action the action performing this effect
     * @param cardsMoved the cards to move
     * @param fromDockingBay the docking bay to transit from
     * @param toDockingBay the docking bay to transit to
     */
    public DockingBayTransitEffect(Action action, Collection<PhysicalCard> cardsMoved, PhysicalCard fromDockingBay, PhysicalCard toDockingBay) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _movedCards = cardsMoved;
        _movedFrom = fromDockingBay;
        _movedTo = toDockingBay;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();

        SubAction subAction = new SubAction(_action);

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Record that regular move was performed
                        for (PhysicalCard cardMoved : _movedCards) {
                            game.getModifiersQuerying().regularMovePerformed(cardMoved);
                        }
                    }
                }
        );

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Emit effect result that card is beginning to move
                        for (PhysicalCard movedCard : _movedCards) {
                            game.getActionsEnvironment().emitEffectResult(new DockingBayTransitingResult(movedCard, _playerId, _movedFrom, _movedTo));
                        }
                    }
                }
        );

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.sendMessage(_playerId + " docking bay transits " + GameUtils.getAppendedNames(_movedCards) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo));
                        for (PhysicalCard movedCard : _movedCards) {
                            gameState.moveCardToLocation(movedCard, _movedTo);
                        }
                        game.getActionsEnvironment().emitEffectResult(new DockingBayTransitedResult(_movedCards, _playerId, _movedFrom, _movedTo));
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
