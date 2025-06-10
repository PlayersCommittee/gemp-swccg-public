package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.LocationPlacementDirection;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.layout.LocationPlacement;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ConvertLocationResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;

/**
 * An effect to play a location.
 */
public class DeployLocationEffect extends AbstractSubActionEffect implements PlayCardEffect {
    private String _performingPlayerId;
    private PhysicalCard _cardToPlay;
    private Zone _playedFromZone;
    private String _playedFromZoneOwner;
    private LocationPlacement _placement;
    private boolean _reshuffle;
    private boolean _cardWasPlayed;

    /**
     * Creates an effect that plays a location.
     * @param action the action performing this effect
     * @param cardToPlay the card to play
     * @param placement the placement of the location
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    public DeployLocationEffect(Action action, PhysicalCard cardToPlay, LocationPlacement placement, boolean reshuffle) {
        super(action);
        _performingPlayerId = _action.getPerformingPlayer();
        _cardToPlay = cardToPlay;
        _playedFromZone = GameUtils.getZoneFromZoneTop(cardToPlay.getZone());
        _playedFromZoneOwner = cardToPlay.getZoneOwner();
        _placement = placement;
        _reshuffle = reshuffle;
        if (!_placement.getDirection().isOneDirection())
            throw new UnsupportedOperationException("Multiple location placement options when trying to deploy " + GameUtils.getFullName(cardToPlay));
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
        // Record the card being played
        subAction.appendEffect(
                new RecordCardsBeingPlayedEffect(subAction, Collections.singleton(_cardToPlay)));
        // Perform the process of playing the card
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        String fromText = _playedFromZone.getHumanReadable();

                        // Remember if this is rebuilding a collapsed site
                        boolean isRebuildSite = (_placement.getDirection() == LocationPlacementDirection.REPLACE && _placement.getOtherCard().isCollapsed());

                        // Remove the card from where it is being played from and add to the void
                        gameState.removeCardsFromZone(Collections.singleton(_cardToPlay));
                        gameState.addCardToZone(_cardToPlay, Zone.VOID, _cardToPlay.getZoneOwner());

                        // Shuffle the card pile
                        if (_reshuffle && _playedFromZone.isCardPile())
                            gameState.shufflePile(_playedFromZoneOwner, _playedFromZone);

                        // Put the card where it is being played to and send message.
                        gameState.removeCardsFromZone(Collections.singleton(_cardToPlay));
                        gameState.addLocationToTable(game, _cardToPlay, _placement);

                        String relatedText = "";
                        if (Filters.mobile_system.accepts(gameState, modifiersQuerying, _cardToPlay)
                                && _cardToPlay.getBlueprint().getDeploysOrbitingSystem() != null) {
                            PhysicalCard system = Filters.findFirstActive(game, null, Filters.and(Filters.system, Filters.title(_cardToPlay.getBlueprint().getDeploysOrbitingSystem())));
                            if (system != null) {
                                relatedText = " as orbiting " + GameUtils.getCardLink(system);
                                gameState.cardAffectsCard(_performingPlayerId, _cardToPlay, system);
                            }
                        }
                        else if (_placement.getParentStarshipOrVehicleCard() != null) {
                            relatedText = " as related site to " + GameUtils.getCardLink(_placement.getParentStarshipOrVehicleCard());
                            gameState.cardAffectsCard(_performingPlayerId, _cardToPlay, _placement.getParentStarshipOrVehicleCard());
                        }
                        else if (Filters.asteroid_sector.accepts(gameState, modifiersQuerying, _cardToPlay)
                                && _placement.getParentSystem() != null) {
                            PhysicalCard system = Filters.findFirstActive(game, null, Filters.and(Filters.planet_system, Filters.title(_placement.getParentSystem())));
                            relatedText = " as related asteroid sector to " + GameUtils.getCardLink(system);
                        }

                        boolean converted = false;
                        if (_placement.getDirection() == LocationPlacementDirection.REPLACE) {
                            if (isRebuildSite)
                                gameState.sendMessage(_performingPlayerId + " deploys " + GameUtils.getCardLink(_cardToPlay) + " from " + fromText + " to rebuild collapsed " + GameUtils.getCardLink(_placement.getOtherCard()));
                            else {
                                gameState.sendMessage(_performingPlayerId + " deploys " + GameUtils.getCardLink(_cardToPlay) + " from " + fromText + " to convert " + GameUtils.getCardLink(_placement.getOtherCard()));
                                converted = true;
                            }
                        }
                        else {
                            gameState.sendMessage(_performingPlayerId + " deploys " + GameUtils.getCardLink(_cardToPlay) + " from " + fromText + relatedText);
                        }

                        _cardWasPlayed = true;

                        // Record the card just deployed
                        modifiersQuerying.cardJustDeployed(_cardToPlay);

                        // Emit the effect result
                        EffectResult playCardResult = new PlayCardResult(_performingPlayerId, _cardToPlay, _playedFromZone, null, null, null, true, false);
                        actionsEnvironment.emitEffectResult(playCardResult);

                        if (converted) {
                            EffectResult convertedCardResult = new ConvertLocationResult(_performingPlayerId, _placement.getOtherCard(), _cardToPlay);
                            actionsEnvironment.emitEffectResult(convertedCardResult);
                        }
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _cardWasPlayed;
    }
}
