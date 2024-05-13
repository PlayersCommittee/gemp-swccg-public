package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.RecordCardsBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.results.RemovedFromCardPileResult;

import java.util.Collection;
import java.util.Collections;

/**
 * This action plays an Epic Event.
 */
public class PlayEpicEventAction extends AbstractPlayCardAction implements GameTextAction {
    private GameTextActionId _gameTextActionId;
    private boolean _actionInitiated;
    private boolean _actionComplete;
    private boolean _beginEpicEventState;
    private EpicEventState _epicEventState;
    private String _initiationMessage;
    private boolean _sentInitiationMessage;
    private String _actionMessage;
    private boolean _recordedCardPlayed;
    private Zone _playedFromZone;
    private String _playedFromZoneOwner;
    private boolean _removedFromZone;
    private boolean _cardPlayed;
    private RespondablePlayCardEffect _respondableEffect;

    /**
     * Creates an action the plays an Epic Event.
     * @param card the Epic Event card
     */
    public PlayEpicEventAction(PhysicalCard card) {
        this(card, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Creates an action the plays an Epic Event.
     * @param card the Epic Event card
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     */
    public PlayEpicEventAction(PhysicalCard card, GameTextActionId gameTextActionId) {
        super(card, card);
        setPerformingPlayer(card.getOwner());
        _gameTextActionId = gameTextActionId;
        _playedFromZone = card.getZone();
        _playedFromZoneOwner = card.getZoneOwner();
        _text = "Play";
        _initiationMessage = getPerformingPlayer() + " initiates playing " + GameUtils.getCardLink(_cardToPlay) + " from " + _playedFromZone.getHumanReadable();
        setActionMsg(null);
        allowResponses(null);
    }

    /**
     * Gets the card id of the card the game text is originally from
     * @return the card id
     */
    @Override
    public int getGameTextSourceCardId() {
        return _actionSource.getCardId();
    }

    /**
     * Gets the game text action id.
     * @return the game text action id
     */
    @Override
    public GameTextActionId getGameTextActionId() {
        return _gameTextActionId;
    }

    /**
     * Sets the text shown for the action selection on the User Interface
     * @param text the text to show for the action selection
     */
    @Override
    public void setText(String text) {
        _text = text;
        setActionMsg(text);
    }

    /**
     * This method set a state that will last until the end of this Epic Event process.
     */
    public final void setEpicEventState(EpicEventState epicEventState) {
        _epicEventState = epicEventState;
    }

    /**
     * Sets the message shown in the User Interface when the action reaches the response step.
     * @param actionMsg the message to show
     */
    public void setActionMsg(String actionMsg) {
        StringBuilder actionMsgToUse = new StringBuilder();
        actionMsgToUse.append(_cardToPlay.getOwner()).append(" plays ").append(GameUtils.getCardLink(_cardToPlay)).append(" from ").append(_playedFromZone.getHumanReadable());

        // Make sure the first two characters are lower case since this is being inserted into a sentence
        if (actionMsg != null && !actionMsg.isEmpty()) {
            String actionMsgFromInput = actionMsg.length() > 2 ? (actionMsg.substring(0, 2).toLowerCase() + actionMsg.substring(2)) : actionMsg.toLowerCase();
            actionMsgToUse.append(" to ").append(actionMsgFromInput);
        }

        _actionMessage = actionMsgToUse.toString();
    }

    /**
     * Sets the respondable effect that will be performed in order to trigger other cards to respond to this action.
     * This typically is for when other cards need to respond to cancel/re-target "targeting" or cancel "playing" a card.
     * @param respondableEffect the respondable effect
     */
    public void allowResponses(RespondablePlayCardEffect respondableEffect) {
        _respondableEffect = respondableEffect;
    }

    /**
     * Sets action message to show when this action reaches the response step and sets the respondable effect that will
     * be performed in order to trigger other cards to respond to this action.
     * This typically is for when other cards need to respond to cancel/re-target "targeting" or cancel "playing" a card.
     * @param actionMsg the message to show
     * @param respondableEffect the respondable effect
     */
    public void allowResponses(String actionMsg, RespondablePlayCardEffect respondableEffect) {
        setActionMsg(actionMsg);
        allowResponses(respondableEffect);
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        // Begin Epic Event
        if (!_beginEpicEventState) {
            _beginEpicEventState = true;
            if (_epicEventState == null) {
                throw new UnsupportedOperationException(GameUtils.getFullName(_cardToPlay) + " does not have set Epic Event state");
            }
            gameState.beginEpicEvent(_epicEventState);
        }

        // Send message that action is being initiated
        if ((isChoosingTargetsComplete() || _respondableEffect != null) && !_sentInitiationMessage) {
            _sentInitiationMessage = true;
            if (_initiationMessage != null && _cardToPlay != null)
                gameState.sendMessage(_initiationMessage);
        }


        if (!_actionInitiated) {
            _actionInitiated = true;
            gameState.beginPlayCard(this);
        }

        // Verify no costs have failed
        if (!isAnyCostFailed()) {
            // Initiation
            // 1a) Check limits
            // 1b) Choose targets
            // 1c) Pay costs
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            // Record the card being played
            if (!_recordedCardPlayed) {
                _recordedCardPlayed = true;
                return new RecordCardsBeingPlayedEffect(this, Collections.singleton(_cardToPlay));
            }

            // Remove from zone
            if (!_removedFromZone) {
                _removedFromZone = true;

                // Put Epic Event in void while it is being played
                gameState.removeCardsFromZone(Collections.singleton(_cardToPlay));
                gameState.addCardToZone(_cardToPlay, Zone.VOID, _cardToPlay.getOwner());

                // Shuffle card pile
                if (_playedFromZone.isCardPile()) {
                    if (_reshuffle) {
                        return new ShufflePileEffect(this, null, getPerformingPlayer(), _playedFromZoneOwner, _playedFromZone, true);
                    }
                    else {
                        return new TriggeringResultEffect(this, new RemovedFromCardPileResult(this));
                    }
                }
            }

            // Responses
            // 2) Perform a RespondableEffect to allow other responses to the action
            if (!_cardPlayed) {
                _cardPlayed = true;

                // Send message and show animation of targets now that action initiation is complete
                if (_actionMessage != null) {
                    gameState.sendMessage(_actionMessage);
                }

                // Show the Epic Event being played
                gameState.epicEventPlayed(_cardToPlay);

                // Animate the card groups in order
                for (Collection<PhysicalCard> cardsToAnimate : _animationGroupList) {
                    gameState.cardAffectsCards(getPerformingPlayer(), _cardToPlay, cardsToAnimate);
                }

                if (_respondableEffect == null)
                    throw new UnsupportedOperationException(GameUtils.getFullName(_cardToPlay) + " does not have RespondableEffect set");

                // Perform the RespondablePlayCardEffect
                return _respondableEffect;
            }

            // Result
            // 3) Carry out the results of the action
            if (!_respondableEffect.isCanceled()) {
                Effect effect = getNextEffect();
                if (effect != null)
                    return effect;
            }
        }

        // Finish Epic Event
        gameState.finishEpicEvent();

        // Check if card is no longer in void (which means it was already moved somewhere else)
        if (_cardToPlay.getZone() != Zone.VOID) {
            _actionComplete = true;
            gameState.endPlayCard();
        }

        if (!_actionComplete) {
            _actionComplete = true;
            gameState.endPlayCard();

            gameState.removeCardsFromZone(Collections.singleton(_cardToPlay));
            gameState.addCardToTopOfZone(_cardToPlay, Zone.LOST_PILE, _cardToPlay.getOwner());
        }

        return null;
    }
}
