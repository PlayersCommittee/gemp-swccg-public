package com.gempukku.swccgo.league;

import com.gempukku.swccgo.cache.Cached;
import com.gempukku.swccgo.db.LeagueMatchDAO;
import com.gempukku.swccgo.db.vo.LeagueMatchResult;
import org.apache.commons.collections4.map.LRUMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CachedLeagueMatchDAO implements LeagueMatchDAO, Cached {
    private LeagueMatchDAO _leagueMatchDAO;
    private ReadWriteLock _readWriteLock = new ReentrantReadWriteLock();

    private Map<String, Collection<LeagueMatchResult>> _cachedMatches = Collections.synchronizedMap(new LRUMap(5));

    public CachedLeagueMatchDAO(LeagueMatchDAO leagueMatchDAO) {
        _leagueMatchDAO = leagueMatchDAO;
    }

    @Override
    public void clearCache() {
        _readWriteLock.writeLock().lock();
        try {
            _cachedMatches.clear();
        } finally {
            _readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public int getItemCount() {
        return _cachedMatches.size();
    }

    @Override
    public Collection<LeagueMatchResult> getLeagueMatches(String leagueId) {
        _readWriteLock.readLock().lock();
        try {
            Collection<LeagueMatchResult> leagueMatches = _cachedMatches.get(leagueId);
            if (leagueMatches == null) {
                _readWriteLock.readLock().unlock();
                _readWriteLock.writeLock().lock();
                try {
                    leagueMatches = getLeagueMatchesInWriteLock(leagueId);
                } finally {
                    _readWriteLock.readLock().lock();
                    _readWriteLock.writeLock().unlock();
                }
            }
            return Collections.unmodifiableCollection(leagueMatches);
        } finally {
            _readWriteLock.readLock().unlock();
        }
    }

    private Collection<LeagueMatchResult> getLeagueMatchesInWriteLock(String leagueId) {
        Collection<LeagueMatchResult> leagueMatches;
        leagueMatches = _cachedMatches.get(leagueId);
        if (leagueMatches == null) {
            leagueMatches = new CopyOnWriteArraySet<LeagueMatchResult>(_leagueMatchDAO.getLeagueMatches(leagueId));
            _cachedMatches.put(leagueId, leagueMatches);
        }
        return leagueMatches;
    }

    @Override
    public void addPlayedMatch(String leagueId, String serieId, String winner, String loser, String winnerSide, String loserSide) {
        _readWriteLock.writeLock().lock();
        try {
            LeagueMatchResult match = new LeagueMatchResult(serieId, winner, loser, winnerSide, loserSide);

            getLeagueMatchesInWriteLock(leagueId).add(match);
            _leagueMatchDAO.addPlayedMatch(leagueId, serieId, winner, loser, winnerSide, loserSide);
        } finally {
            _readWriteLock.writeLock().unlock();
        }
    }
}
