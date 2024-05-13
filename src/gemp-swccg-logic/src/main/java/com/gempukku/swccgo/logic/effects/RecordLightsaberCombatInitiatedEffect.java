package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Arrays;

/**
 * This effect records the lightsaber combat being initiated.
 */
class RecordLightsaberCombatInitiatedEffect extends AbstractSuccessfulEffect {
    private String _performingPlayerId;
    private PhysicalCard _location;
    private PhysicalCard _darkSideCharacter;
    private PhysicalCard _lightSideCharacter;
    private LightsaberCombatDirections _lightsaberCombatDirections;

    /**
     * Creates an effect that records the lightsaber combat being initiated.
     * @param action the action performing this effect
     * @param location the location of lightsaber combat
     * @param darkSideCharacter the dark side character to participate in lightsaber combat
     * @param lightSideCharacter the light side character to participate in lightsaber combat
     * @param lightsaberCombatDirections the lightsaber combat directions
     */
    public RecordLightsaberCombatInitiatedEffect(Action action, PhysicalCard location, PhysicalCard darkSideCharacter, PhysicalCard lightSideCharacter, LightsaberCombatDirections lightsaberCombatDirections) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _location = location;
        _darkSideCharacter = darkSideCharacter;
        _lightSideCharacter = lightSideCharacter;
        _lightsaberCombatDirections = lightsaberCombatDirections;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        // Begin lightsaber combat
        game.getGameState().beginLightsaberCombat(_performingPlayerId, _location, _darkSideCharacter, _lightSideCharacter, _lightsaberCombatDirections);
        String msgText = _performingPlayerId + " initiates lightsaber combat between " + GameUtils.getCardLink(_darkSideCharacter) + " and " + GameUtils.getCardLink(_lightSideCharacter);
        game.getGameState().sendMessage(msgText);
        game.getGameState().cardAffectsCards(_performingPlayerId, _action.getActionSource(), Arrays.asList(_darkSideCharacter, _lightSideCharacter));
    }
}
