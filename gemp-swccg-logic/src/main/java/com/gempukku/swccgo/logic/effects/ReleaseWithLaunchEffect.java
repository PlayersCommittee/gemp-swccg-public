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
 * An effect that releases the specified starship and has the starship 'launch'.
 */
public class ReleaseWithLaunchEffect extends AbstractSuccessfulEffect {
    private String _performingPlayer;
    private PhysicalCard _captive;
    private PhysicalCard _launchPoint;
    private boolean _inStarfighterCapacity;
    private boolean _inVehicleCapacity;

    /**
     * Creates an effect that releases the specified starship and has the starship 'launch'.
     * @param action the action performing this effect
     * @param captive the captive to release
     * @param launchPoint the launch point
     */
    public ReleaseWithLaunchEffect(Action action, PhysicalCard captive, PhysicalCard launchPoint) {
        this(action, captive, launchPoint, false, false);
    }

    /**
     * Creates an effect that releases the specified starship and has the character 'launch'.
     * @param action the action performing this effect
     * @param captive the captive to release
     * @param launchPoint the launch point
     */
    public ReleaseWithLaunchEffect(Action action, PhysicalCard captive, PhysicalCard launchPoint, boolean inStarfighterCapacity, boolean inVehicleCapacity) {
        super(action);
        _performingPlayer = action.getPerformingPlayer();
        _captive = captive;
        _launchPoint = launchPoint;
        _inStarfighterCapacity = inStarfighterCapacity;
        _inVehicleCapacity = inVehicleCapacity;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        PhysicalCard source = _action.getActionSource();
        GameState gameState = game.getGameState();
        boolean rallyToLocation = _launchPoint.getBlueprint().getCardCategory() == CardCategory.LOCATION;

        gameState.sendMessage(GameUtils.getCardLink(_captive) + " is released and 'launches' to " + GameUtils.getCardLink(_launchPoint));
        if (source != null) {
            gameState.cardAffectsCard(_performingPlayer, source, _captive);
        }

        _captive.setCapturedStarship(false);
        game.getModifiersEnvironment().removeEndOfCaptivity(_captive);


        if (rallyToLocation)
            gameState.moveCardToLocation(_captive, _launchPoint, true);
        else if (_inStarfighterCapacity)
            gameState.moveCardToAttachedInStarfighterOrTIECapacitySlot(_captive, _launchPoint);
        else if (_inVehicleCapacity)
            gameState.moveCardToAttachedInVehicleCapacitySlot(_captive, _launchPoint);
        else
            gameState.moveCardToAttachedInCapitalStarshipCapacitySlot(_captive, _launchPoint);

        // Emit effect result that captive was released
        game.getActionsEnvironment().emitEffectResult(new ReleaseCaptiveResult(_performingPlayer, _captive, ReleaseOption.LAUNCH));
    }
}
