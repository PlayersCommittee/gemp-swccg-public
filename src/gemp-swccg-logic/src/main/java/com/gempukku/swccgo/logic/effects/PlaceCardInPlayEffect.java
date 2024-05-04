package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.PlaceCardInPlayResult;
import com.gempukku.swccgo.logic.timing.results.RemovedFromCardPileResult;

import java.util.Collections;

/**
 * An effect to place (not deploy or play) a card in play.
 */
public class PlaceCardInPlayEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private PhysicalCard _cardToPlace;
    private Zone _placedFromZone;
    private String _placedFromZoneOwner;
    private PhysicalCard _placedFromStackedOn;
    private String _placedToZoneOwner;
    private PhysicalCard _attachTo;
    private boolean _attachAsPilot;
    private boolean _attachAsPassenger;
    private PhysicalCard _placedToLocation;
    private boolean _reshuffle;
    private boolean _cardWasPlaced;

    /**
     * Creates an effect to place a card as attached to the specified card.
     * @param action the action performing this effect
     * @param attachTo the card to attach the played card to
     * @param attachAsPilot true if the card is played as a pilot, otherwise false
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    public PlaceCardInPlayEffect(Action action, PhysicalCard cardToPlace, PhysicalCard attachTo, boolean attachAsPilot, boolean reshuffle) {
        super(action);
        _performingPlayerId = _action.getPerformingPlayer();
        _cardToPlace = cardToPlace;
        _placedFromZone = GameUtils.getZoneFromZoneTop(cardToPlace.getZone());
        if (_placedFromZone == Zone.STACKED) {
            _placedFromStackedOn = cardToPlace.getStackedOn();
        }
        _attachTo = attachTo;
        _attachAsPilot = attachAsPilot;
        _attachAsPassenger = !attachAsPilot && cardToPlace.getBlueprint().getCardCategory() == CardCategory.CHARACTER;
        _reshuffle = reshuffle;
    }

    /**
     * Creates an effect to play a card to the specified location.
     * @param action the action performing this effect
     * @param playedToLocation the location the card is played to
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    public PlaceCardInPlayEffect(Action action, PhysicalCard cardToPlace, PhysicalCard playedToLocation, boolean reshuffle) {
        super(action);
        _performingPlayerId = _action.getPerformingPlayer();
        _cardToPlace = cardToPlace;
        _placedFromZone = GameUtils.getZoneFromZoneTop(cardToPlace.getZone());
        _placedFromZoneOwner = cardToPlace.getZoneOwner();
        if (_placedFromZone == Zone.STACKED) {
            _placedFromStackedOn = cardToPlace.getStackedOn();
        }
        _placedToLocation = playedToLocation;
        _placedToZoneOwner = cardToPlace.getZoneOwner();
        _reshuffle = reshuffle;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();

        final SubAction subAction = new SubAction(_action);
        // Put card being played in the "void", and emit effect result to responses that can cancel it, or re-target it.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        final String fromText = _placedFromStackedOn != null ? GameUtils.getCardLink(_placedFromStackedOn) : _placedFromZone.getHumanReadable();

                        // Remove the card from where it is being played from and add to the void
                        gameState.removeCardsFromZone(Collections.singleton(_cardToPlace));
                        gameState.addCardToZone(_cardToPlace, Zone.VOID, _cardToPlace.getZoneOwner());

                        // Shuffle the card pile
                        if (_placedFromZone.isCardPile()) {
                            if (_reshuffle) {
                                subAction.appendEffect(
                                        new ShufflePileEffect(subAction, null, _performingPlayerId, _placedFromZoneOwner, _placedFromZone, true));
                            }
                            else {
                                actionsEnvironment.emitEffectResult(
                                        new RemovedFromCardPileResult(subAction));
                            }
                        }

                        // Perform the rest of the action
                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(final SwccgGame game) {

                                        // Remove card from void
                                        gameState.removeCardsFromZone(Collections.singleton(_cardToPlace));

                                        final StringBuilder placeCardText = new StringBuilder();

                                        // Played as attached.
                                        if (_attachTo != null) {

                                            if (_attachAsPilot) {
                                                gameState.attachCardInPilotCapacitySlot(_cardToPlace, _attachTo);

                                                if (Filters.transport_vehicle.accepts(gameState, modifiersQuerying, _attachTo))
                                                    placeCardText.append(GameUtils.getCardLink(_cardToPlace)).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(_attachTo)).append(" as driver");
                                                else
                                                    placeCardText.append(GameUtils.getCardLink(_cardToPlace)).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(_attachTo)).append(" as pilot");
                                            }
                                            else if (_attachAsPassenger) {
                                                gameState.attachCardInPassengerCapacitySlot(_cardToPlace, _attachTo);
                                                placeCardText.append(GameUtils.getCardLink(_cardToPlace)).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(_attachTo)).append(" as passenger");
                                            }
                                            else if (Filters.starship.accepts(gameState, modifiersQuerying, _attachTo)
                                                        && !Filters.or(Filters.Effect_of_any_Kind, Filters.starship_weapon).accepts(gameState, modifiersQuerying, _cardToPlace)) {
                                                gameState.attachCard(_cardToPlace, _attachTo);
                                                placeCardText.append(GameUtils.getCardLink(_cardToPlace)).append(" from ").append(fromText).append(" into cargo hold of ").append(GameUtils.getCardLink(_attachTo));
                                            }
                                            else {
                                                gameState.attachCard(_cardToPlace, _attachTo);
                                                placeCardText.append(GameUtils.getCardLink(_cardToPlace)).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(_attachTo));
                                            }
                                        }
                                        // Played to location.
                                        else if (_placedToLocation != null) {
                                            gameState.playCardToLocation(_cardToPlace, _placedToLocation, _placedToZoneOwner);
                                            placeCardText.append(GameUtils.getCardLink(_cardToPlace)).append(" from ").append(fromText).append(" to ").append(GameUtils.getCardLink(_placedToLocation));
                                        }
                                        else {
                                            throw new UnsupportedOperationException(GameUtils.getFullName(_cardToPlace) + " must place as attached or to location");
                                        }

                                        _cardWasPlaced = true;

                                        // Send message
                                        gameState.sendMessage(_performingPlayerId + " places " + placeCardText);

                                        // Emit the effect result
                                        PlaceCardInPlayResult placeCardResult = new PlaceCardInPlayResult(_performingPlayerId, _cardToPlace, _placedFromZone, _attachTo, _placedToLocation);
                                        actionsEnvironment.emitEffectResult(placeCardResult);
                                    }
                                }
                        );
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _cardWasPlaced;
    }
}
