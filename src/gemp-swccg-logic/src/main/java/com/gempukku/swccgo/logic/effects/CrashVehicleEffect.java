package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.VehicleCrashedResult;


public class CrashVehicleEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _vehicleCrashed;
    private PhysicalCard _crashedByCard;

    public CrashVehicleEffect(Action action, PhysicalCard vehicleCrashed, PhysicalCard crashedByCard) {
        super(action);
        _vehicleCrashed = vehicleCrashed;
        _crashedByCard = crashedByCard;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (!_vehicleCrashed.isCrashed() || _vehicleCrashed.getBlueprint().getCardCategory() == CardCategory.VEHICLE) {
            GameState gameState = game.getGameState();

            gameState.sendMessage(GameUtils.getCardLink(_vehicleCrashed) + " is 'crashed' by " + GameUtils.getCardLink(_crashedByCard));
            gameState.cardAffectsCard(_crashedByCard.getOwner(), _crashedByCard, _vehicleCrashed);

            _vehicleCrashed.setCrashed(true);
            if (!_vehicleCrashed.isSideways()) {
                gameState.turnCardSideways(game, _vehicleCrashed, false);
            }

            game.getActionsEnvironment().emitEffectResult(new VehicleCrashedResult(_vehicleCrashed, _crashedByCard));
        }
    }
}
