package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * This effect records the battle being initiated at a location.
 */
class RecordBattleInitiatedEffect extends AbstractSuccessfulEffect {
    private String _performingPlayerId;
    private PhysicalCard _location;
    private boolean _isLocalTrouble;
    private Collection<PhysicalCard> _localTroubleParticipants;
    private Collection<Modifier> _extraModifiers;

    /**
     * Creates an effect that records the battle being initiated at a location for the purposes of the game keeping
     * track of which cards were involved in the battle.
     * @param action the action performing this effect
     * @param location the battle location
     * @param isLocalTrouble true if battle is a Local Trouble battle, otherwise false
     * @param localTroubleParticipants the Local Trouble battle participants, or null if not a Local Trouble battle
     */
    public RecordBattleInitiatedEffect(Action action, PhysicalCard location, boolean isLocalTrouble, Collection<PhysicalCard> localTroubleParticipants, Collection<Modifier> extraModifiers) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _location = location;
        _isLocalTrouble = isLocalTrouble;
        _localTroubleParticipants = localTroubleParticipants;
        _extraModifiers = extraModifiers;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        // Begin battle
        String msgText = _performingPlayerId + " initiates " + (_isLocalTrouble ? "Local Trouble " : "") + "battle at " + GameUtils.getCardLink(_location);
        gameState.sendMessage(msgText);
        gameState.beginBattle(_performingPlayerId, _location, _isLocalTrouble, _localTroubleParticipants, _extraModifiers);
    }
}
