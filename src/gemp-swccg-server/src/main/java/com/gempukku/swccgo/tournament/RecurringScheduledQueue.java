package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.vo.CollectionType;

import java.util.Date;

public class RecurringScheduledQueue extends AbstractTournamentQueue implements TournamentQueue {
    private static final long _signupTimeBeforeStart = 1000 * 60 * 60; // 60 minutes before start

    private long _repeatEvery;
    private long _nextStart;
    private String _nextStartText;

    private String _tournamentIdPrefix;
    private String _tournamentQueueName;

    private int _minimumPlayers;
    private TournamentService _tournamentService;

    public RecurringScheduledQueue(long originalStart, long repeatEvery, String tournamentIdPrefix,
                                              String tournamentQueueName, int cost, boolean requiresDeck,
                                              CollectionType collectionType,
                                              TournamentService tournamentService, TournamentPrizes tournamentPrizes, PairingMechanism pairingMechanism, String format, int minimumPlayers) {
        super(cost, requiresDeck, collectionType, tournamentPrizes, pairingMechanism, format);
        _repeatEvery = repeatEvery;
        _tournamentIdPrefix = tournamentIdPrefix;
        _tournamentQueueName = tournamentQueueName;
        _tournamentService = tournamentService;
        _minimumPlayers = minimumPlayers;
        long number = (System.currentTimeMillis() - originalStart) / repeatEvery;

        _nextStart = originalStart + (number + 1) * repeatEvery;
        _nextStartText = DateUtils.formatDateWithHour(new Date(_nextStart));
    }

    @Override
    public String getStartCondition() {
        return _nextStartText;
    }

    @Override
    public String getTournamentQueueName() {
        return _tournamentQueueName;
    }

    @Override
    public String getPairingDescription() {
        return _pairingMechanism.getPlayOffSystem() + ", minimum players: " + _minimumPlayers;
    }

    @Override
    public boolean isJoinable() {
        return System.currentTimeMillis() >= _nextStart - _signupTimeBeforeStart;
    }

    @Override
    public boolean process(TournamentQueueCallback tournamentQueueCallback, CollectionsManager collectionsManager) {
        long now = System.currentTimeMillis();
        if (now > _nextStart) {
            if (_players.size() >= _minimumPlayers) {
                String tournamentId = _tournamentIdPrefix+System.currentTimeMillis();
                String tournamentName = _tournamentQueueName + " - " + DateUtils.getStringDateWithHour();

                for (String player : _players)
                    _tournamentService.addPlayer(tournamentId, player, _playerDecks.get(player));

                Tournament tournament = _tournamentService.addTournament(tournamentId, null, tournamentName, _format, _collectionType, Tournament.Stage.PLAYING_GAMES,
                        _pairingMechanism.getRegistryRepresentation(), _tournamentPrizes.getRegistryRepresentation(), new Date());
                tournamentQueueCallback.createTournament(tournament);

                _players.clear();
                _playerDecks.clear();
            } else {
                leaveAllPlayers(collectionsManager);
            }
            _nextStart+=_repeatEvery;
            _nextStartText = DateUtils.formatDateWithHour(new Date(_nextStart));
        }
        return false;
    }
}