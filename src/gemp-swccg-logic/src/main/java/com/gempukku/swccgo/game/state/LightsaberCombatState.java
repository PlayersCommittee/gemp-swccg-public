package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.LightsaberCombatDirections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// This class contains the state information for
// lightsaber combat within a game of Gemp-Swccg.
//
public class LightsaberCombatState {
    private SwccgGame _game;
    private String _playerInitiatedLightsaberCombat;
    private PhysicalCard _location;
    private Collection<PhysicalCard> _lightsaberCombatParticipants = new ArrayList<PhysicalCard>();
    private PhysicalCard _darkSideCharacter;
    private PhysicalCard _lightSideCharacter;
    private Map<String, Float> _totalLightsaberCombatDestinyFromDraws = new HashMap<String, Float>();
    private Map<String, Integer> _lightsaberCombatDestinyDrawsMade = new HashMap<String, Integer>();
    private Map<String, Float> _finalLightsaberCombatTotal = new HashMap<String, Float>();
    private LightsaberCombatDirections _lightsaberCombatDirections;
    private boolean _reachedResults;
    private boolean _resultsComplete;
    private boolean _canceled;
    private String _winner;
    private String _loser;

    public LightsaberCombatState(SwccgGame game, String playerId, PhysicalCard location, PhysicalCard darkSideCharacter, PhysicalCard lightSideCharacter, LightsaberCombatDirections lightsaberCombatDirections) {
        _game = game;
        _playerInitiatedLightsaberCombat = playerId;
        _location = location;
        _darkSideCharacter = darkSideCharacter;
        _lightSideCharacter = lightSideCharacter;
        _lightsaberCombatParticipants.add(darkSideCharacter);
        _lightsaberCombatParticipants.add(lightSideCharacter);
        _lightsaberCombatDirections = lightsaberCombatDirections;
    }

    public String getPlayerInitiatedLightsaberCombat() {
        return _playerInitiatedLightsaberCombat;
    }

    public PhysicalCard getLocation() {
        return _location;
    }

    public Collection<PhysicalCard> getLightsaberCombatParticipants() {
        return _lightsaberCombatParticipants;
    }

    public PhysicalCard getCharacter(String playerId) {
        if (playerId.equals(_game.getDarkPlayer()))
            return _darkSideCharacter;
        else
            return _lightSideCharacter;
    }

    public float getBaseLightsaberCombatTotal(String playerId) {
        float total = 0;
        if (playerId.equals(_game.getDarkPlayer()))
            total += _lightsaberCombatDirections.getBaseLightsaberCombatTotal(_game.getDarkPlayer(), this).evaluateExpression(_game.getGameState(), _game.getModifiersQuerying(), _darkSideCharacter);
        else
            total += _lightsaberCombatDirections.getBaseLightsaberCombatTotal(_game.getLightPlayer(), this).evaluateExpression(_game.getGameState(), _game.getModifiersQuerying(), _lightSideCharacter);

        return total + getTotalLightsaberCombatDestinyFromDraws(playerId);
    }

    public float getFinalLightsaberCombatTotal(String playerId) {
        return _finalLightsaberCombatTotal.get(playerId);
    }

    public void setFinalLightsaberCombatTotal(String playerId, float total) {
        _finalLightsaberCombatTotal.put(playerId, total);
    }

    public void reachedResults() {
        _reachedResults = true;

        float darkSideTotal = getFinalLightsaberCombatTotal(_game.getDarkPlayer());
        float lightSideTotal = getFinalLightsaberCombatTotal(_game.getLightPlayer());

        // Set winner and loser
        if (darkSideTotal > lightSideTotal) {
            setWinner(_game.getDarkPlayer());
            setLoser(_game.getLightPlayer());
        }
        else if (lightSideTotal > darkSideTotal) {
            setWinner(_game.getLightPlayer());
            setLoser(_game.getDarkPlayer());
        }
    }

    public boolean isReachedResults() {
        return _reachedResults;
    }

    public void resultsComplete() {
        _resultsComplete = true;
    }

    public boolean isResultsComplete() {
        return _resultsComplete;
    }

    public int getBaseNumDuelDestinyDraws(String playerId) {
        return _lightsaberCombatDirections.getBaseNumLightsaberCombatDestinyDraws(playerId, this);
    }

    public void increaseTotalLightsaberCombatDestinyFromDraws(String player, float destiny, int numDraws) {
        final Float previousValue = _totalLightsaberCombatDestinyFromDraws.get(player);
        if (previousValue == null)
            _totalLightsaberCombatDestinyFromDraws.put(player, destiny);
        else
            _totalLightsaberCombatDestinyFromDraws.put(player, previousValue + destiny);

        final Integer previousNumDraws = _lightsaberCombatDestinyDrawsMade.get(player);
        if (previousNumDraws == null)
            _lightsaberCombatDestinyDrawsMade.put(player, numDraws);
        else
            _lightsaberCombatDestinyDrawsMade.put(player, previousNumDraws + numDraws);
    }

    public float getTotalLightsaberCombatDestinyFromDraws(String player) {
        final Float value = _totalLightsaberCombatDestinyFromDraws.get(player);
        if (value == null)
            return 0;
        else
            return value;
    }

    public int getNumLightsaberCombatDestinyDrawn(String player) {
        final Integer value = _lightsaberCombatDestinyDrawsMade.get(player);
        if (value == null)
            return 0;
        else
            return value;
    }

    public void cancel() {
        _canceled = true;
    }

    public boolean isCanceled() {
        return _canceled;
    }

    public boolean canContinue(SwccgGame game) {
        if (_canceled)
            return false;

        if (isReachedResults())
            return true;

        return Filters.present(_location).accepts(game, _darkSideCharacter)
                && Filters.present(_location).accepts(game, _lightSideCharacter);
    }

    public void setWinner(String winner) {
        _winner = winner;
    }

    public void setLoser(String loser) {
        _loser = loser;
    }

    public String getWinner() {
        return _winner;
    }

    public PhysicalCard getWinningCharacter() {
        if (_winner != null)
            return getCharacter(_winner);
        else
            return null;
    }

    public String getLoser() {
        return _loser;
    }

    public PhysicalCard getLosingCharacter() {
        if (_loser != null)
            return getCharacter(_loser);
        else
            return null;
    }
}
