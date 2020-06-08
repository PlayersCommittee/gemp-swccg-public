package com.gempukku.swccgo.league;

import com.gempukku.swccgo.cache.Cached;
import com.gempukku.swccgo.db.LeagueParticipationDAO;
import com.gempukku.swccgo.game.Player;
import org.apache.commons.collections.map.LRUMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CachedLeagueParticipationDAO implements LeagueParticipationDAO, Cached {
    private LeagueParticipationDAO _leagueParticipationDAO;
    private ReadWriteLock _readWriteLock = new ReentrantReadWriteLock();

    private Map<String, Set<String>> _cachedParticipants = Collections.synchronizedMap(new LRUMap(5));

    public CachedLeagueParticipationDAO(LeagueParticipationDAO leagueParticipationDAO) {
        _leagueParticipationDAO = leagueParticipationDAO;
    }

    @Override
    public void userJoinsLeague(String leagueId, Player player, String remoteAddr) {
        _readWriteLock.writeLock().lock();
        try {
            getLeagueParticipantsInWriteLock(leagueId).add(player.getName());
            _leagueParticipationDAO.userJoinsLeague(leagueId, player, remoteAddr);
        } finally {
            _readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Collection<String> getUsersParticipating(String leagueId) {
        _readWriteLock.readLock().lock();
        try {
            Collection<String> leagueParticipants = _cachedParticipants.get(leagueId);
            if (leagueParticipants == null) {
                _readWriteLock.readLock().unlock();
                _readWriteLock.writeLock().lock();
                try {
                    leagueParticipants = getLeagueParticipantsInWriteLock(leagueId);
                } finally {
                    _readWriteLock.readLock().lock();
                    _readWriteLock.writeLock().unlock();
                }
            }
            return Collections.unmodifiableCollection(leagueParticipants);
        } finally {
            _readWriteLock.readLock().unlock();
        }
    }

    private Collection<String> getLeagueParticipantsInWriteLock(String leagueId) {
        Set<String> leagueParticipants;
        leagueParticipants = _cachedParticipants.get(leagueId);
        if (leagueParticipants == null) {
            leagueParticipants = new CopyOnWriteArraySet<String>(_leagueParticipationDAO.getUsersParticipating(leagueId));
            _cachedParticipants.put(leagueId, leagueParticipants);
        }
        return leagueParticipants;
    }

    @Override
    public int getItemCount() {
        return _cachedParticipants.size();
    }

    @Override
    public void clearCache() {
        _readWriteLock.writeLock().lock();
        try {
            _cachedParticipants.clear();
        } finally {
            _readWriteLock.writeLock().unlock();
        }
    }
}
