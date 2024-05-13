package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * This effect records the drawing of asteroid destiny against a starship as been initiated.
 */
class RecordDrawAsteroidDestinyEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _starship;
    private PhysicalCard _location;

    /**
     * Creates an effect that records the drawing of asteroid destiny against a starship as been initiated for the purposes
     * of the game keeping track of which starship have had asteroid destiny drawn against.
     * @param action the action performing this effect
     * @param starship the starship
     * @param location the location
     */
    public RecordDrawAsteroidDestinyEffect(Action action, PhysicalCard starship, PhysicalCard location) {
        super(action);
        _starship = starship;
        _location = location;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        String performingPlayerId = _action.getPerformingPlayer();

        // Begin Asteroid destiny draw and record cards involved
        game.getGameState().beginAsteroidDestinyDraw(_starship, _location);
        game.getGameState().activatedCard(performingPlayerId, _location);
        game.getGameState().sendMessage(performingPlayerId + " targets to draw asteroid destiny against " + GameUtils.getCardLink(_starship) + " at " + GameUtils.getCardLink(_location));
        game.getGameState().cardAffectsCard(performingPlayerId, _location, _starship);
        game.getModifiersQuerying().asteroidDestinyDrawnAgainst(_starship, _location);
    }
}
