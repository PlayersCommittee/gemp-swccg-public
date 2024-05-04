package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.SnapshotData;

import java.util.Collection;

/**
 * An abstract action that has the base implementation for actions that can be responded to.
 */
public abstract class AbstractRespondableAction extends AbstractAction {
    protected PhysicalCard _physicalCard;
    protected String _text;
    protected String _initiationMessage;
    protected boolean _sentInitiationMessage;
    protected String _actionMessage;
    protected boolean _actionInitiated;
    protected boolean _actionComplete;
    protected boolean _respondableEffectPerformed;
    protected RespondableEffect _respondableEffect;

    /**
     * Needed to generate snapshot.
     */
    public AbstractRespondableAction() {
    }

    @Override
    public void generateSnapshot(Action selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        AbstractRespondableAction snapshot = (AbstractRespondableAction) selfSnapshot;

        // Set each field
        snapshot._physicalCard = snapshotData.getDataForSnapshot(_physicalCard);
        snapshot._text = _text;
        snapshot._initiationMessage = _initiationMessage;
        snapshot._sentInitiationMessage = _sentInitiationMessage;
        snapshot._actionMessage = _actionMessage;
        snapshot._actionInitiated = _actionInitiated;
        snapshot._actionComplete = _actionComplete;
        snapshot._respondableEffectPerformed = _respondableEffectPerformed;
        snapshot._respondableEffect = _respondableEffect;
    }

    /**
     * Creates an action with the specified card as the source and the card's owner as the player performing the action
     * that can be responded to.
     * @param physicalCard the card
     */
    public AbstractRespondableAction(PhysicalCard physicalCard) {
        _physicalCard = physicalCard;
        setPerformingPlayer(physicalCard.getOwner());
    }

    /**
     * Creates an action with the specified card as the source and the specified player as the player performing the action
     * that can be responded to.
     * @param physicalCard the card
     * @param performingPlayer the player
     */
    public AbstractRespondableAction(PhysicalCard physicalCard, String performingPlayer) {
        _physicalCard = physicalCard;
        setPerformingPlayer(performingPlayer);
    }

    @Override
    public PhysicalCard getActionSource() {
        return _physicalCard;
    }

    @Override
    public String getText() {
        return _text;
    }

    /**
     * Sets the text shown for the action selection on the User Interface
     * @param text the text to show for the action selection
     */
    public void setText(String text) {
        _text = text;
        setActionMsg(text);
    }

    /**
     * Gets the parent action if this is a sub-action.
     * @return the parent action, or null
     */
    public Action getParentAction() {
        return null;
    }

    /**
     * Sets the initiation message and card animation to be skipped.
     */
    public void skipInitialMessageAndAnimation() {
        _sentInitiationMessage = true;
    }

    /**
     * Sets the message shown in the User Interface when the action reaches the response step.
     * @param actionMsg the message to show
     */
    public void setActionMsg(String actionMsg) {
        // Make sure the first two characters are lower case since this is being inserted into a sentence
        if (actionMsg == null || actionMsg.isEmpty()) {
            _actionMessage = null;
            return;
        }

        String actionMsgToUse = actionMsg.length() > 2 ? (actionMsg.substring(0, 2).toLowerCase() + actionMsg.substring(2)) : actionMsg.toLowerCase();
        if (!actionMsgToUse.startsWith("targets ")) {
            if (actionMsgToUse.startsWith("target ")) {
                actionMsgToUse = " targets " + actionMsgToUse.substring(7);
            }
            else {
                actionMsgToUse = " targets to " + actionMsgToUse;
            }
        }

        String playerId = getPerformingPlayer();
        if (playerId != null) {
            if (_physicalCard != null)
                _actionMessage = playerId + actionMsgToUse + " using " + GameUtils.getCardLink(_physicalCard);
            else
                _actionMessage = playerId + actionMsgToUse;
        }
        else if (_physicalCard != null) {
            _actionMessage = GameUtils.getCardLink(_physicalCard) + actionMsgToUse;
        }
    }

    /**
     * Sets the respondable effect that will be performed in order to trigger other cards to respond to this action.
     * This typically is for when other cards need to respond to cancel/re-target "targeting" or cancel "playing" a card.
     * @param respondableEffect the respondable effect
     */
    public void allowResponses(RespondableEffect respondableEffect) {
        _respondableEffect = respondableEffect;
    }

    /**
     * Sets action message to show when this action reaches the response step and sets the respondable effect that will
     * be performed in order to trigger other cards to respond to this action.
     * This typically is for when other cards need to respond to cancel/re-target "targeting" or cancel "playing" a card.
     * @param actionMsg the message to show
     * @param respondableEffect the respondable effect
     */
    public void allowResponses(String actionMsg, RespondableEffect respondableEffect) {
        setActionMsg(actionMsg);
        allowResponses(respondableEffect);
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        // Send message that action is being initiated
        if ((isChoosingTargetsComplete() || _respondableEffect != null) && !_sentInitiationMessage) {
            _sentInitiationMessage = true;
            if (_physicalCard != null && (_physicalCard.getZone().isInPlay() || _physicalCard.isInsertCardRevealed()))
                gameState.activatedCard(getPerformingPlayer(), _physicalCard);
            if (_initiationMessage != null && _physicalCard != null && !_physicalCard.getZone().isFaceDown())
                gameState.sendMessage(_initiationMessage);
        }

        if (!_actionInitiated) {
            _actionInitiated = true;
            if (isFromGameText() && getParentAction() == null) {
                gameState.beginGameTextAction((AbstractGameTextAction) this);
            }
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
                    gameState.sendMessage(_actionMessage);
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
                        gameState.cardAffectsCards(getPerformingPlayer(), _physicalCard, cardsToAnimate);
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

        if (!_actionComplete) {
            _actionComplete = true;
            if (isFromGameText() && getParentAction() == null) {
                gameState.endGameTextAction();
            }
        }

        return null;
    }
}
