package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.SwccgGameParticipant;
import com.gempukku.swccgo.league.LeagueSeriesData;

import java.util.*;

public class AwaitingTable {
    private CollectionType _collectionType;
    private SwccgFormat _swccgFormat;
    private League _league;
    private LeagueSeriesData _leagueSeries;
    private String _tableDesc;
    private Map<String, SwccgGameParticipant> _players = new HashMap<String, SwccgGameParticipant>();

    private int _capacity = 2;

    public AwaitingTable(SwccgFormat swccgFormat, CollectionType collectionType, League league, LeagueSeriesData leagueSeries, String tableDesc) {
        _swccgFormat = swccgFormat;
        _collectionType = collectionType;
        _league = league;
        _leagueSeries = leagueSeries;
        _tableDesc = tableDesc;
    }

    public boolean addPlayer(SwccgGameParticipant player) {
        _players.put(player.getPlayerId(), player);
        return _players.size() == _capacity;
    }

    public boolean removePlayer(String playerId) {
        _players.remove(playerId);
        return _players.isEmpty();
    }

    public boolean hasPlayer(String playerId) {
        return _players.containsKey(playerId);
    }

    public List<String> getPlayerNames() {
        return new LinkedList<String>(_players.keySet());
    }

    public Set<SwccgGameParticipant> getPlayers() {
        return Collections.unmodifiableSet(new HashSet<SwccgGameParticipant>(_players.values()));
    }

    public CollectionType getCollectionType() {
        return _collectionType;
    }

    public SwccgFormat getSwccgoFormat() {
        return _swccgFormat;
    }

    public League getLeague() {
        return _league;
    }

    public LeagueSeriesData getLeagueSeries() {
        return _leagueSeries;
    }

    public String getTableDesc() {
        return _tableDesc;
    }
}
