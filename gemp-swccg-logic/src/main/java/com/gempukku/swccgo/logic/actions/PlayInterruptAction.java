package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.UsageEffect;
import com.gempukku.swccgo.logic.timing.results.RemovedFromCardPileResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This action plays an Interrupt.
 */
public class PlayInterruptAction extends AbstractPlayCardAction implements GameTextAction {
    private SwccgGame _game;
    private boolean _actionInitiated;
    private boolean _actionComplete;
    private GameTextActionId _gameTextActionId;
    private String _initiationMessage;
    private boolean _sentInitiationMessage;
    private String _actionMessage;
    private boolean _extraCostAdded;
    private boolean _recordedCardPlayed;
    private Zone _playedFromZone;
    private String _playedFromText;
    private String _playedFromZoneOwner;
    private boolean _removedFromZone;
    private boolean _cardPlayed;
    private RespondablePlayCardEffect _respondableEffect;
    private CardSubtype _playedAsSubtype;
    private Set<String> _immuneToTitle = new HashSet<String>();
    private boolean _multipleSubtype;
    private boolean _needDecisionReturnToHandWhenResolving;
    private UsageEffect _returnToHandUsageEffect;
    private boolean _returnToHand;
    private boolean _needToUseReturnToHandUsageEffect;

    /**
     * Creates an action that plays an Interrupt.
     * @param game the game
     * @param card the Interrupt card
     */
    public PlayInterruptAction(SwccgGame game, PhysicalCard card) {
        this(game, card, card.getBlueprint().getCardSubtype());
    }

    /**
     * Creates an action that plays an Interrupt.
     * @param game the game
     * @param card the Interrupt card
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     */
    public PlayInterruptAction(SwccgGame game, PhysicalCard card, GameTextActionId gameTextActionId) {
        this(game, card, gameTextActionId, card.getBlueprint().getCardSubtype());
    }

    /**
     * Creates an action the plays an Interrupt.
     * @param game the game
     * @param card the Interrupt card
     * @param playedAsSubtype the Interrupt subtype to play as
     */
    public PlayInterruptAction(SwccgGame game, PhysicalCard card, CardSubtype playedAsSubtype) {
        this(game, card, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, playedAsSubtype);
    }

    /**
     * Creates an action the plays an Interrupt.
     * @param game the game
     * @param card the Interrupt card
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     * @param playedAsSubtype the Interrupt subtype to play as
     */
    public PlayInterruptAction(SwccgGame game, PhysicalCard card, GameTextActionId gameTextActionId, CardSubtype playedAsSubtype) {
        super(card, card);
        setPerformingPlayer(card.getOwner());
        _game = game;
        _gameTextActionId = gameTextActionId;
        _playedFromZone = card.getZone();
        _playedFromZoneOwner = card.getZoneOwner();
        _text = "Play";
        _playedFromText = _cardToPlay.getStackedOn() != null ? ("stacked on " + GameUtils.getCardLink(_cardToPlay.getStackedOn())) : _playedFromZone.getHumanReadable();
        _initiationMessage = getPerformingPlayer() + " initiates playing " + GameUtils.getCardLink(_cardToPlay) + " from " + _playedFromText;
        _needDecisionReturnToHandWhenResolving = false;
        _returnToHand = false;
        _needToUseReturnToHandUsageEffect = false;

        _playedAsSubtype = game.getModifiersQuerying().getInterruptType(game.getGameState(), card);
        if (_playedAsSubtype==CardSubtype.USED_OR_LOST || _playedAsSubtype==CardSubtype.USED_OR_STARTING || _playedAsSubtype==CardSubtype.LOST_OR_STARTING) {
            _multipleSubtype = true;
            _playedAsSubtype = playedAsSubtype;
        }
        setActionMsg(null);
        allowResponses(null);
    }

    /**
     * Sets the played as subtype for Interrupts that can be played as multiple subtypes.
     * @param playedAsSubtype the subtype to play as
     */
    public void setPlayedAsSubtype(CardSubtype playedAsSubtype) {
        _playedAsSubtype = playedAsSubtype;
    }

    /**
     * Returns the played as subtype
     * @return the subtype
     */
    public CardSubtype getPlayedAsSubtype() {
        return _playedAsSubtype;
    }

    /**
     * Indicates if the player should be given the option to return the interrupt to hand after resolving
     * @param optionalReturnToHandWhenResolving true if the player should be given the option to return it to hand after resolving
     * @param usageEffect the usage effect if once per game, etc., otherwise null
     */
    public void setNeedsDecisionReturnToHandWhenResolving(boolean optionalReturnToHandWhenResolving, UsageEffect usageEffect) {
        _needDecisionReturnToHandWhenResolving = optionalReturnToHandWhenResolving;
        _returnToHandUsageEffect = usageEffect;
    }


    /**
     * Sets that the card is to be placed in hand
     * @param returnToHand true if the player decided to return the card to hand
     */
    public void setReturnToHand(boolean returnToHand) {
        _returnToHand = returnToHand;
        _needToUseReturnToHandUsageEffect = returnToHand && (_returnToHandUsageEffect != null);
    }

    /**
     * Sets that the card is to be placed out of play when played.
     * @param placeOutOfPlay true if card is to be placed out of play
     */
    @Override
    public void setPlaceOutOfPlay(boolean placeOutOfPlay) {
        _placeOutOfPlay = placeOutOfPlay;
    }

    /**
     * Sets a card title that the Interrupt is immune to while being played.
     * @param immuneTo the card title to be immune to
     */
    public void setImmuneTo(String immuneTo) {
        _immuneToTitle.add(immuneTo);
    }

    /**
     * Checks if the action is immune to the specified title while being played.
     * @param title the card title to check
     * @return true if the action is immune to the title
     */
    @Override
    public boolean isImmuneTo(String title) {
        return _immuneToTitle.contains(title);
    }

    /**
     * Determines if the card is to be placed out of play when played.
     * @return true or false
     */
    @Override
    public boolean isToBePlacedOutOfPlay() {
        return _placeOutOfPlay || _playedAsSubtype == CardSubtype.OUT_OF_PLAY;
    }

    /**
     * Gets the card id of the card the game text is originally from.
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
        if (_multipleSubtype && _playedAsSubtype == CardSubtype.USED)
            _text = "USED: " + text;
        else if (_multipleSubtype && _playedAsSubtype == CardSubtype.LOST)
            _text = "LOST: " + text;
        else
            _text = text;

        setActionMsg(text);
    }

    /**
     * Sets the message shown in the User Interface when the action reaches the response step.
     * @param actionMsg the message to show
     */
    public void setActionMsg(String actionMsg) {
        // Check if the interrupt type has changed
        CardSubtype subtype = _game.getModifiersQuerying().getInterruptType(_game.getGameState(), _cardToPlay);
        if (subtype!=CardSubtype.USED_OR_LOST && subtype!=CardSubtype.USED_OR_STARTING && subtype!=CardSubtype.LOST_OR_STARTING) {
            _playedAsSubtype = subtype;
        }

        StringBuilder actionMsgToUse = new StringBuilder();
        if (_playedAsSubtype==CardSubtype.STARTING)
            actionMsgToUse.append(_cardToPlay.getOwner()).append(" plays ").append(GameUtils.getCardLink(_cardToPlay)).append(" as ").append(_playedAsSubtype.getHumanReadable()).append(" Interrupt");
        else if (_multipleSubtype && _playedAsSubtype!=CardSubtype.USED_OR_LOST && _playedAsSubtype!=CardSubtype.USED_OR_STARTING && _playedAsSubtype!=CardSubtype.LOST_OR_STARTING)
            actionMsgToUse.append(_cardToPlay.getOwner()).append(" plays ").append(GameUtils.getCardLink(_cardToPlay)).append(" from ").append(_playedFromText).append(" as ").append(_playedAsSubtype.getHumanReadable()).append(" Interrupt");
        else
            actionMsgToUse.append(_cardToPlay.getOwner()).append(" plays ").append(GameUtils.getCardLink(_cardToPlay)).append(" from ").append(_playedFromText);

        // Make sure the first two characters are lower case since this is being inserted into a sentence
        if (actionMsg != null && !actionMsg.isEmpty()) {
            String actionMsgFromInput = actionMsg.length() > 2 ? (actionMsg.substring(0, 2).toLowerCase() + actionMsg.substring(2)) : actionMsg.toLowerCase();
            actionMsgToUse.append(" to ").append(actionMsgFromInput);
        }

        _actionMessage = actionMsgToUse.toString();
    }

    @Override
    public void appendCost(StandardEffect cost) {
        if (cost.getType() != Effect.Type.BEFORE_USE_FORCE
                || !_game.getModifiersQuerying().isInterruptPlayForFree(_game.getGameState(), _cardToPlay)) {
            super.appendCost(cost);
        }
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

        // Send message that action is being initiated
        if ((isChoosingTargetsComplete() || _respondableEffect != null) && !_sentInitiationMessage) {
            _sentInitiationMessage = true;
            if (_initiationMessage != null && _cardToPlay != null)
                gameState.sendMessage(_initiationMessage);
        }

        if (!_actionInitiated) {
            _actionInitiated = true;
            gameState.beginPlayCard(this);

            // Add any immunity while the interrupt is being played
            for (String immuneTo : _immuneToTitle) {
                game.getModifiersEnvironment().addUntilEndOfCardPlayedModifier(
                        new ImmuneToTitleModifier(_cardToPlay, Filters.samePermanentCardId(_cardToPlay), immuneTo), _cardToPlay);
            }
        }

        // Verify no costs have failed
        if (!isAnyCostFailed()) {
            // Initiation
            // 1a) Check limits
            // 1b) Choose targets
            // 1c) Pay costs

            // Add any extra cost to play the interrupt
            if (!_extraCostAdded) {
                _extraCostAdded = true;
                appendBeforeCost(new PayExtraCostToPlayInterruptEffect(this, _cardToPlay));
                return getNextCost();
            }

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

                // Put interrupt in void while it is being played
                gameState.removeCardsFromZone(Collections.singleton(_cardToPlay));
                gameState.addCardToZone(_cardToPlay, Zone.VOID, _cardToPlay.getOwner());

                // Shuffle card pile
                if (_playedFromZone.isCardPile() && _playedAsSubtype != CardSubtype.STARTING) {
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

                // Show the interrupt being played
                gameState.interruptPlayed(_cardToPlay);

                // Animate the card groups in order
                for (Collection<PhysicalCard> cardsToAnimate : _animationGroupList) {
                    gameState.cardAffectsCards(getPerformingPlayer(), _cardToPlay, cardsToAnimate);
                }

                if (_respondableEffect == null)
                    throw new UnsupportedOperationException(GameUtils.getFullName(_cardToPlay) + " does not have RespondableEffect set");

                // Perform the RespondableInterruptEffect
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

        // check usage of returning the interrupt to hand instead of placing it in a different pile
        if (_needToUseReturnToHandUsageEffect) {
            _needToUseReturnToHandUsageEffect = false;
            return _returnToHandUsageEffect;
        }

        if (!_actionComplete) {
            // check if the interrupt allows the player to return it to hand instead of placing it in a different pile and give the player the choice if so
            if (_needDecisionReturnToHandWhenResolving
                    && _cardToPlay.getZone() == Zone.VOID
                    && !isToBePlacedOutOfPlay()
                    && !game.getModifiersQuerying().isPlacedOutOfPlayWhenPlayedAsSubtype(gameState, _cardToPlay, _playedAsSubtype)) {

                _needDecisionReturnToHandWhenResolving = false;
                final PlayInterruptAction _this = this;
                return new PlayoutDecisionEffect(this, getPerformingPlayer(), new YesNoDecision("Return " + GameUtils.getCardLink(_cardToPlay) + " to hand?") {
                    @Override
                    protected void yes() {
                        _this.setReturnToHand(true);
                    }
                });
            }

            _actionComplete = true;
            gameState.endPlayCard();

            // Check if card is no longer in void (which means it was already moved somewhere else)
            if (_cardToPlay.getZone() == Zone.VOID) {

                if (_playedAsSubtype == CardSubtype.USED_OR_LOST || _playedAsSubtype == CardSubtype.USED_OR_STARTING || _playedAsSubtype == CardSubtype.LOST_OR_STARTING)
                    throw new UnsupportedOperationException(GameUtils.getFullName(_cardToPlay) + " had Interrupt subtype set to " + _playedAsSubtype);

                if (_playedAsSubtype == CardSubtype.STARTING)
                    throw new UnsupportedOperationException("Starting interrupt " + GameUtils.getFullName(_cardToPlay) + " still in the void");

                if (isToBePlacedOutOfPlay() || game.getModifiersQuerying().isPlacedOutOfPlayWhenPlayedAsSubtype(gameState, _cardToPlay, _playedAsSubtype)) {
                    return new PlaceCardFromVoidOutOfPlayEffect(this, _cardToPlay);
                } else if (_returnToHand) {
                    return new TakeCardFromVoidIntoHandEffect(this, getPerformingPlayer(), _cardToPlay);
                }
                else if (_playedAsSubtype == CardSubtype.USED) {
                    return new PutCardFromVoidInUsedPileEffect(this, getPerformingPlayer(), _cardToPlay);
                }
                else {
                    return new PutCardFromVoidInLostPileEffect(this, getPerformingPlayer(), _cardToPlay);
                }
            }
        }

        return null;
    }
}
