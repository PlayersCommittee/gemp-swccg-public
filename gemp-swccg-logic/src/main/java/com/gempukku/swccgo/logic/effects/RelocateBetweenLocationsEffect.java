package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.ReleaseOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ReleaseCaptiveResult;
import com.gempukku.swccgo.logic.timing.results.RelocatedBetweenLocationsResult;
import com.gempukku.swccgo.logic.timing.results.RelocatingBetweenLocationsResult;

import java.util.Collection;
import java.util.Collections;

/**
 * An effect to relocate cards between locations.
 */
public class RelocateBetweenLocationsEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Collection<PhysicalCard> _relocatedCards;
    private PhysicalCard _fromLocation;
    private PhysicalCard _toLocation;
    private boolean _asRegularMove;

    /**
     * Creates an effect to relocate a card between locations.
     * @param action the action performing this effect
     * @param cardToMove the card to move
     * @param toLocation the location to relocate to
     */
    public RelocateBetweenLocationsEffect(Action action, PhysicalCard cardToMove, PhysicalCard toLocation) {
        this(action, cardToMove, toLocation, false);
    }

    /**
     * Creates an effect to relocate a card between locations.
     * @param action the action performing this effect
     * @param cardToMove the card to move
     * @param toLocation the location to relocate to
     * @param asRegularMove true if treated as a regular move, otherwise false
     */
    public RelocateBetweenLocationsEffect(Action action, PhysicalCard cardToMove, PhysicalCard toLocation, boolean asRegularMove) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _relocatedCards = Collections.singleton(cardToMove);
        _fromLocation = cardToMove.getAtLocation() != null ? cardToMove.getAtLocation() : cardToMove.getAttachedTo();
        _toLocation = toLocation;
        _asRegularMove = asRegularMove;
    }

    /**
     * Creates an effect to relocate cards between locations.
     * @param action the action performing this effect
     * @param cardsToMove the cards to move
     * @param toLocation the location to relocate to
     */
    public RelocateBetweenLocationsEffect(Action action, Collection<PhysicalCard> cardsToMove, PhysicalCard toLocation) {
        this(action, cardsToMove, toLocation, false);
    }

    /**
     * Creates an effect to relocate cards between locations.
     * @param action the action performing this effect
     * @param cardsToMove the cards to move
     * @param toLocation the location to relocate to
     * @param asRegularMove true if treated as a regular move, otherwise false
     */
    public RelocateBetweenLocationsEffect(Action action, Collection<PhysicalCard> cardsToMove, PhysicalCard toLocation, boolean asRegularMove) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _relocatedCards = cardsToMove;
        PhysicalCard cardToMove = cardsToMove.iterator().next();
        _fromLocation = cardToMove.getAtLocation() != null ? cardToMove.getAtLocation() : cardToMove.getAttachedTo();
        _toLocation = toLocation;
        _asRegularMove = asRegularMove;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final PhysicalCard actionSource = _action.getActionSource();
        final PhysicalCard fromLocation = game.getModifiersQuerying().getLocationHere(gameState, _fromLocation);

        SubAction subAction = new SubAction(_action);

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_asRegularMove) {
                            // Record that regular move was performed
                            for (PhysicalCard movedCard : _relocatedCards) {
                                game.getModifiersQuerying().regularMovePerformed(movedCard);
                            }
                        }
                    }
                }
        );

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Emit effect result that card is beginning to move
                        for (PhysicalCard movedCard : _relocatedCards) {
                            game.getActionsEnvironment().emitEffectResult(new RelocatingBetweenLocationsResult(movedCard, actionSource, _playerId, fromLocation, _toLocation));
                        }
                    }
                }
        );

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.sendMessage(_playerId + " relocates " + GameUtils.getAppendedNames(_relocatedCards) + " from " + GameUtils.getCardLink(_fromLocation) + " to " + GameUtils.getCardLink(_toLocation));
                        for (PhysicalCard movedCard : _relocatedCards) {
                            if (movedCard.isFrozen()) {
                                gameState.moveCardToLocation(movedCard, _toLocation, movedCard.getOwner().equals(game.getDarkPlayer()));
                            }
                            else if (movedCard.isCaptive()) {
                                gameState.moveCardToLocation(movedCard, _toLocation, true);
                                game.getActionsEnvironment().emitEffectResult(new ReleaseCaptiveResult(_playerId, movedCard, ReleaseOption.RALLY));
                            }
                            else {
                                gameState.moveCardToLocation(movedCard, _toLocation);
                            }
                        }
                        game.getActionsEnvironment().emitEffectResult(new RelocatedBetweenLocationsResult(_relocatedCards, actionSource, _playerId, fromLocation, _toLocation));
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
