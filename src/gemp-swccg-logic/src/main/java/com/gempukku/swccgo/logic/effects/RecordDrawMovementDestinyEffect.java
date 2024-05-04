package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * This effect records the drawing of movement destiny against a starship as been initiated.
 */
class RecordDrawMovementDestinyEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _starship;
    private PhysicalCard _location;

    /**
     * Creates an effect that records the drawing of movement destiny against a starship as been initiated.
     * @param action the action performing this effect
     * @param starship the starship
     * @param location the location
     */
    public RecordDrawMovementDestinyEffect(Action action, PhysicalCard starship, PhysicalCard location) {
        super(action);
        _starship = starship;
        _location = location;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        String performingPlayerId = _action.getPerformingPlayer();

        // Begin movement destiny draw
        game.getGameState().beginMovementDestinyDraw(_starship, _location);
        game.getGameState().sendMessage(performingPlayerId + " targets to draw movement destiny against " + GameUtils.getCardLink(_starship) + " at " + GameUtils.getCardLink(_location));
        game.getGameState().cardAffectsCard(performingPlayerId, _location, _starship);
    }
}
