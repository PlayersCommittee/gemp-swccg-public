package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Arrays;

/**
 * This effect records the duel being initiated.
 */
class RecordDuelInitiatedEffect extends AbstractSuccessfulEffect {
    private String _performingPlayerId;
    private PhysicalCard _location;
    private PhysicalCard _darkSideCharacter;
    private PhysicalCard _lightSideCharacter;
    private DuelDirections _duelDirections;

    /**
     * Creates an effect that records the duel being initiated.
     * @param action the action performing this effect
     * @param location the location of the duel
     * @param darkSideCharacter the dark side character to duel
     * @param lightSideCharacter the light side character to duel
     * @param duelDirections the duel directions
     */
    public RecordDuelInitiatedEffect(Action action, PhysicalCard location, PhysicalCard darkSideCharacter, PhysicalCard lightSideCharacter, DuelDirections duelDirections) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _location = location;
        _darkSideCharacter = darkSideCharacter;
        _lightSideCharacter = lightSideCharacter;
        _duelDirections = duelDirections;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        // Begin duel
        game.getGameState().beginDuel(_performingPlayerId, _action.getActionSource(), _location, _darkSideCharacter, _lightSideCharacter, _duelDirections);
        String msgText = _performingPlayerId + " initiates " + (_duelDirections.isEpicDuel() ? "epic " : "") + "duel between " + GameUtils.getCardLink(_darkSideCharacter) + " and " + GameUtils.getCardLink(_lightSideCharacter);
        game.getGameState().sendMessage(msgText);
        game.getGameState().cardAffectsCards(_performingPlayerId, _action.getActionSource(), Arrays.asList(_darkSideCharacter, _lightSideCharacter));
    }
}
