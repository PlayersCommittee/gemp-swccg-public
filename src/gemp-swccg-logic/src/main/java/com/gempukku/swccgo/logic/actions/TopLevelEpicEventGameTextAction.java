package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;

/**
 * An action that is for top-level actions as part of a card's game text.
 */
public class TopLevelEpicEventGameTextAction extends TopLevelGameTextAction {
    private boolean _beginEpicEventState;
    private EpicEventState _epicEventState;

    /**
     * Creates an Epic Event top-level action with the specified card as the source.
     * @param physicalCard the card
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     */
    public TopLevelEpicEventGameTextAction(PhysicalCard physicalCard, int gameTextSourceCardId) {
        super(physicalCard, gameTextSourceCardId);
    }

    /**
     * Creates an Epic Event top-level action with the specified card as the source.
     * @param physicalCard the card
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     */
    public TopLevelEpicEventGameTextAction(PhysicalCard physicalCard, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        super(physicalCard, gameTextSourceCardId, gameTextActionId);
    }

    /**
     * Creates an Epic Event top-level action with the specified card as the source and performed by the specified player.
     * @param physicalCard the card
     * @param performingPlayer the player
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     */
    public TopLevelEpicEventGameTextAction(PhysicalCard physicalCard, String performingPlayer, int gameTextSourceCardId) {
        super(physicalCard, performingPlayer, gameTextSourceCardId);
    }

    /**
     * Creates an Epic Event top-level action with the specified card as the source and performed by the specified player.
     * @param physicalCard the card
     * @param performingPlayer the player
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     */
    public TopLevelEpicEventGameTextAction(PhysicalCard physicalCard, String performingPlayer, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        super(physicalCard, performingPlayer, gameTextSourceCardId, gameTextActionId);
    }

    /**
     * This method set a state that will last until the end of this Epic Event process.
     */
    public final void setEpicEventState(EpicEventState epicEventState) {
        _epicEventState = epicEventState;
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Begin Epic Event
        if (!_beginEpicEventState) {
            _beginEpicEventState = true;
            if (_epicEventState == null) {
                throw new UnsupportedOperationException(GameUtils.getFullName(_physicalCard) + " does not have set Epic Event state");
            }
            game.getGameState().beginEpicEvent(_epicEventState);
        }

        // Send message that action is being initiated
        if ((isChoosingTargetsComplete() || _respondableEffect != null) && !_sentInitiationMessage) {
            _sentInitiationMessage = true;
            if (_physicalCard != null && (_physicalCard.getZone().isInPlay() || _physicalCard.isInsertCardRevealed()))
                game.getGameState().activatedCard(getPerformingPlayer(), _physicalCard);
            if (_initiationMessage != null && _physicalCard != null && !_physicalCard.getZone().isFaceDown())
                game.getGameState().sendMessage(_initiationMessage);
        }

        if (!isAnyCostFailed()) {
            // Initiation
            // 1a) Check limits
            // 1b) Choose targets
            // 1c) Pay costs
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            // Responses
            // 2) Perform a RespondableEffect to allow other responses to the action
            if (!_respondableEffectPerformed) {
                _respondableEffectPerformed = true;

                // Send message and show animation of targets now that action initiation is complete
                if (_actionMessage != null) {
                    game.getGameState().sendMessage(_actionMessage);
                }

                // Assert that RespondableEffect is set (which is required if any cards were targeted and this is not a sub-action)
                if (_respondableEffect == null && _latestTargetGroupId > 0) {
                    if (_physicalCard != null)
                        throw new UnsupportedOperationException(GameUtils.getFullName(_physicalCard) + " does not have RespondableEffect set");
                    else
                        throw new UnsupportedOperationException("No RespondableEffect set");
                }

                // Animate the card groups in order
                if (_physicalCard != null) {
                    for (Collection<PhysicalCard> cardsToAnimate : _animationGroupList) {
                        game.getGameState().cardAffectsCards(getPerformingPlayer(), _physicalCard, cardsToAnimate);
                    }
                }

                // Perform the RespondableEffect if one was provided
                if (_respondableEffect != null) {
                    return _respondableEffect;
                }
            }

            if (_respondableEffect == null || !_respondableEffect.isCanceled()) {
                // Result
                // 3) Carry out the results of the action
                Effect effect = getNextEffect();
                if (effect != null)
                    return effect;
            }
        }

        // Finish Epic Event
        game.getGameState().finishEpicEvent();

        return null;
    }
}
