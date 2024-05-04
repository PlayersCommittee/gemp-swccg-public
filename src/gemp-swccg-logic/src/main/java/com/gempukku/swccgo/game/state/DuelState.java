package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.DuelDirections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// This class contains the state information for a
// duel within a game of Gemp-Swccg.
//
public class DuelState {
    private SwccgGame _game;
    private String _playerInitiatedDuel;
    private PhysicalCard _cardInitiatedDuel;
    private PhysicalCard _location;
    private Collection<PhysicalCard> _duelParticipants = new ArrayList<PhysicalCard>();
    private PhysicalCard _darkSideCharacter;
    private PhysicalCard _lightSideCharacter;
    private boolean _isCrossOverToDarkSideAttempt;
    private Map<String, Float> _totalDuelDestinyFromDraws = new HashMap<String, Float>();
    private Map<String, Integer> _duelDestinyDrawsMade = new HashMap<String, Integer>();
    private Map<String, Float> _finalDuelTotal = new HashMap<String, Float>();
    private DuelDirections _duelDirections;
    private boolean _isEpicDuel;
    private boolean _reachedResults;
    private boolean _resultsComplete;
    private boolean _canceled;
    private String _winner;
    private String _loser;
    
    public DuelState(SwccgGame game, String playerId, PhysicalCard cardToInitiateDuel, PhysicalCard location, PhysicalCard darkSideCharacter, PhysicalCard lightSideCharacter, DuelDirections duelDirections) {
        _game = game;
        _playerInitiatedDuel = playerId;
        _cardInitiatedDuel = cardToInitiateDuel;
        _location = location;
        _darkSideCharacter = darkSideCharacter;
        _lightSideCharacter = lightSideCharacter;
        _duelParticipants.add(darkSideCharacter);
        _duelParticipants.add(lightSideCharacter);
        _isCrossOverToDarkSideAttempt = duelDirections.isCrossOverToDarkSideAttempt();
        _isEpicDuel = duelDirections.isEpicDuel();
        _duelDirections = duelDirections;
    }

    public String getPlayerInitiatedDuel() {
        return _playerInitiatedDuel;
    }

    public PhysicalCard getCardInitiatedDuel() {
        return _cardInitiatedDuel;
    }

    public PhysicalCard getLocation() {
        return _location;
    }

    public Collection<PhysicalCard> getDuelParticipants() {
        return _duelParticipants;
    }

    public PhysicalCard getCharacter(String playerId) {
        if (playerId.equals(_game.getDarkPlayer()))
            return _darkSideCharacter;
        else
            return _lightSideCharacter;
    }

    public float getBaseDuelTotal(String playerId) {
        float total = 0;
        if (playerId.equals(_game.getDarkPlayer()))
            total += _duelDirections.getBaseDuelTotal(_game.getDarkPlayer(), this).evaluateExpression(_game.getGameState(), _game.getModifiersQuerying(), _darkSideCharacter);
        else
            total += _duelDirections.getBaseDuelTotal(_game.getLightPlayer(), this).evaluateExpression(_game.getGameState(), _game.getModifiersQuerying(), _lightSideCharacter);

        return total + getTotalDuelDestinyFromDraws(playerId);
    }

    public float getFinalDuelTotal(String playerId) {
        return _finalDuelTotal.get(playerId);
    }

    public void setFinalDuelTotal(String playerId, float total) {
        _finalDuelTotal.put(playerId, total);
    }

    public void reachedResults() {
        _reachedResults = true;

        float darkSideTotal = getFinalDuelTotal(_game.getDarkPlayer());
        float lightSideTotal = getFinalDuelTotal(_game.getLightPlayer());

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
        return _duelDirections.getBaseNumDuelDestinyDraws(playerId, this);
    }

    public void increaseTotalDuelDestinyFromDraws(String player, float destiny, int numDraws) {
        final Float previousValue = _totalDuelDestinyFromDraws.get(player);
        if (previousValue == null)
            _totalDuelDestinyFromDraws.put(player, destiny);
        else
            _totalDuelDestinyFromDraws.put(player, previousValue + destiny);

        final Integer previousNumDraws = _duelDestinyDrawsMade.get(player);
        if (previousNumDraws == null)
            _duelDestinyDrawsMade.put(player, numDraws);
        else
            _duelDestinyDrawsMade.put(player, previousNumDraws + numDraws);
    }

    public float getTotalDuelDestinyFromDraws(String player) {
        final Float value = _totalDuelDestinyFromDraws.get(player);
        if (value == null)
            return 0;
        else
            return value;
    }

    public int getNumDuelDestinyDrawn(String player) {
        final Integer value = _duelDestinyDrawsMade.get(player);
        if (value == null)
            return 0;
        else
            return value;
    }

    public boolean isCrossOverToDarkSideAttempt() {
        return _isCrossOverToDarkSideAttempt;
    }

    public boolean isEpicDuel() {
        return _isEpicDuel;
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

        return Filters.at(_location).accepts(game.getGameState(), game.getModifiersQuerying(), _darkSideCharacter)
                && Filters.at(_location).accepts(game.getGameState(), game.getModifiersQuerying(), _lightSideCharacter);
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
