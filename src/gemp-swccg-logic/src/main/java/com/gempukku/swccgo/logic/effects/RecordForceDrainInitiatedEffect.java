package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;

/**
 * This effect records the Force drain being initiated at a location.
 */
class RecordForceDrainInitiatedEffect extends AbstractSuccessfulEffect {
    private String _performingPlayerId;
    private PhysicalCard _location;

    /**
     * Creates an effect that records the Force drain being initiated at a location for the purposes of the game keeping
     * track of which cards were involved in the Force drain.
     * @param action the action performing this effect
     * @param location the Force drain location
     */
    public RecordForceDrainInitiatedEffect(Action action, PhysicalCard location) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _location = location;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        // Begin Force drain and record cards involved
        game.getGameState().beginForceDrain(_performingPlayerId, _location);
        float amountToDrain = game.getModifiersQuerying().getForceDrainAmount(game.getGameState(), _location, _performingPlayerId);
        game.getGameState().activatedCard(_performingPlayerId, _location);
        game.getGameState().sendMessage(_performingPlayerId + " initiates Force drain of " + GuiUtils.formatAsString(Math.max(0, amountToDrain)) + " at " + GameUtils.getCardLink(_location));
        game.getModifiersQuerying().forceDrainAttempted(_location);

        Collection<PhysicalCard> participatedInDrain = Filters.filterAllOnTable(game, Filters.and(Filters.owner(_performingPlayerId), Filters.at(_location)));
        for (PhysicalCard card : participatedInDrain) {
            game.getModifiersQuerying().participatedInForceDrain(card);
        }
    }
}
