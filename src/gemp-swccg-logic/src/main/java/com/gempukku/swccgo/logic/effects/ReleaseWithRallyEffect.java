package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.ReleaseOption;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ReleaseCaptiveResult;

/**
 * An effect that releases the specified character and has the character 'rally'.
 */
public class ReleaseWithRallyEffect extends AbstractSuccessfulEffect {
    private String _performingPlayer;
    private PhysicalCard _captive;
    private PhysicalCard _rallyPoint;
    private boolean _rallyAsPilot;

    /**
     * Creates an effect that releases the specified character and has the character 'rally'.
     * @param action the action performing this effect
     * @param captive the captive to release
     * @param rallyPoint the rally point
     * @param rallyAsPilot true if character 'rallies' to pilot capacity slot, otherwise false
     */
    public ReleaseWithRallyEffect(Action action, PhysicalCard captive, PhysicalCard rallyPoint, boolean rallyAsPilot) {
        super(action);
        _performingPlayer = action.getPerformingPlayer();
        _captive = captive;
        _rallyPoint = rallyPoint;
        _rallyAsPilot = rallyAsPilot;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        PhysicalCard source = _action.getActionSource();
        GameState gameState = game.getGameState();
        boolean rallyToLocation = _rallyPoint.getBlueprint().getCardCategory() == CardCategory.LOCATION;

        String seatText = "";
        if (!rallyToLocation) {
            if (_rallyAsPilot) {
                if (Filters.transport_vehicle.accepts(game.getGameState(), game.getModifiersQuerying(), _rallyPoint))
                    seatText = " as driver";
                else
                    seatText = " as pilot";
            }
            else {
                seatText = " as passenger";
            }
        }

        gameState.sendMessage(GameUtils.getCardLink(_captive) + " is released and 'rallies' to " + GameUtils.getCardLink(_rallyPoint) + seatText);
        if (source != null) {
            gameState.cardAffectsCard(_performingPlayer, source, _captive);
        }

        _captive.setCaptiveEscort(null);
        _captive.setImprisoned(false);
        _captive.setFrozen(false);
        //Something like this needs to be done to ensure that captives who have marked their escort as a
        // target are no longer displaying that stale target in the UI.
        //_captive.clearTargetedCards();
        game.getModifiersEnvironment().removeEndOfCaptivity(_captive);

        if (rallyToLocation)
            gameState.moveCardToLocation(_captive, _rallyPoint, true);
        else if (_rallyAsPilot)
            gameState.moveCardToAttachedInPilotCapacitySlot(_captive, _rallyPoint);
        else
            gameState.moveCardToAttachedInPassengerCapacitySlot(_captive, _rallyPoint);

        // Emit effect result that captive was released
        game.getActionsEnvironment().emitEffectResult(new ReleaseCaptiveResult(_performingPlayer, _captive, ReleaseOption.RALLY));
    }
}
