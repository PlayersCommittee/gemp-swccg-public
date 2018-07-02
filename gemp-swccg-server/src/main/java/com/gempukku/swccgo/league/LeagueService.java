package com.gempukku.swccgo.league;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.competitive.StandingsProducer;
import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.db.LeagueMatchDAO;
import com.gempukku.swccgo.db.LeagueParticipationDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.db.vo.LeagueMatchResult;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LeagueService {
    private SwccgCardBlueprintLibrary _library;
    private LeagueDAO _leagueDao;

    // Cached on this layer
    private CachedLeagueMatchDAO _leagueMatchDao;
    private CachedLeagueParticipationDAO _leagueParticipationDAO;

    private CollectionsManager _collectionsManager;

    private Map<String, List<PlayerStanding>> _leagueStandings = new ConcurrentHashMap<String, List<PlayerStanding>>();
    private Map<String, List<PlayerStanding>> _leagueSeriesStandings = new ConcurrentHashMap<String, List<PlayerStanding>>();

    private int _activeLeaguesLoadedDate;
    private List<League> _activeLeagues;

    public LeagueService(SwccgCardBlueprintLibrary library, LeagueDAO leagueDao, LeagueMatchDAO leagueMatchDao,
                         LeagueParticipationDAO leagueParticipationDAO, CollectionsManager collectionsManager) {
        _library = library;
        _leagueDao = leagueDao;
        _leagueMatchDao = new CachedLeagueMatchDAO(leagueMatchDao);
        _leagueParticipationDAO = new CachedLeagueParticipationDAO(leagueParticipationDAO);
        _collectionsManager = collectionsManager;
    }

    public synchronized void clearCache() {
        _leagueSeriesStandings.clear();
        _leagueStandings.clear();
        _activeLeaguesLoadedDate = 0;

        _leagueMatchDao.clearCache();
        _leagueParticipationDAO.clearCache();
    }

    private synchronized void ensureLoadedCurrentLeagues() {
        int currentDate = DateUtils.getCurrentDate();
        if (currentDate != _activeLeaguesLoadedDate) {
            _leagueMatchDao.clearCache();
            _leagueParticipationDAO.clearCache();

            try {
                _activeLeagues = _leagueDao.loadActiveLeagues(_library, currentDate);
                _activeLeaguesLoadedDate = currentDate;
                processLoadedLeagues(currentDate);
            } catch (SQLException e) {
                throw new RuntimeException("Unable to load Leagues", e);
            } catch (IOException e) {
                throw new RuntimeException("Unable to load Leagues", e);
            }
        }
    }

    public synchronized List<League> getActiveLeagues() {
        if (DateUtils.getCurrentDate() == _activeLeaguesLoadedDate)
            return Collections.unmodifiableList(_activeLeagues);
        else {
            ensureLoadedCurrentLeagues();
            return Collections.unmodifiableList(_activeLeagues);
        }
    }

    private void processLoadedLeagues(int currentDate) {
        for (League activeLeague : _activeLeagues) {
            int oldStatus = activeLeague.getStatus();
            int newStatus = activeLeague.getLeagueData().process(_collectionsManager, getLeagueStandings(activeLeague), oldStatus, currentDate);
            if (newStatus != oldStatus)
                _leagueDao.setStatus(activeLeague, newStatus);
        }
    }

    public synchronized boolean isPlayerInLeague(League league, Player player) {
        return _leagueParticipationDAO.getUsersParticipating(league.getType()).contains(player.getName());
    }

    public synchronized boolean playerJoinsLeague(League league, Player player, String remoteAddr, boolean skipCost) {
        if (isPlayerInLeague(league, player))
            return false;
        int cost = league.getCost();
        if (skipCost || _collectionsManager.removeCurrencyFromPlayerCollection("Joining "+league.getName()+" league", player, CollectionType.MY_CARDS, cost)) {
            _leagueParticipationDAO.userJoinsLeague(league.getType(), player, remoteAddr);
            league.getLeagueData().joinLeague(_collectionsManager, player, DateUtils.getCurrentDate());

            _leagueStandings.remove(LeagueMapKeys.getLeagueMapKey(league));

            return true;
        } else {
            return false;
        }
    }

    public synchronized League getLeagueByType(String type) {
        for (League league : getActiveLeagues()) {
            if (league.getType().equals(type))
                return league;
        }
        return null;
    }

    public synchronized CollectionType getCollectionTypeByCode(String collectionTypeCode) {
        for (League league : getActiveLeagues()) {
            for (LeagueSeriesData leagueSeriesData : league.getLeagueData().getSeries()) {
                CollectionType collectionType = leagueSeriesData.getCollectionType();
                if (collectionType != null && collectionType.getCode().equals(collectionTypeCode))
                    return collectionType;
            }
        }
        return null;
    }

    public synchronized LeagueSeriesData getCurrentLeagueSeries(League league) {
        final int currentDate = DateUtils.getCurrentDate();

        for (LeagueSeriesData leagueSeriesData : league.getLeagueData().getSeries()) {
            if (currentDate >= leagueSeriesData.getStart() && currentDate <= leagueSeriesData.getEnd())
                return leagueSeriesData;
        }

        return null;
    }

    public synchronized void reportLeagueGameResult(League league, LeagueSeriesData serie, String winner, String loser, String winnerSide, String loserSide) {
        _leagueMatchDao.addPlayedMatch(league.getType(), serie.getName(), winner, loser, winnerSide, loserSide);

        _leagueStandings.remove(LeagueMapKeys.getLeagueMapKey(league));
        _leagueSeriesStandings.remove(LeagueMapKeys.getLeagueSeriesMapKey(league, serie));

        awardPrizesToPlayer(league, serie, winner, true);
        awardPrizesToPlayer(league, serie, loser, false);
    }

    private void awardPrizesToPlayer(League league, LeagueSeriesData serie, String player, boolean winner) {
        int count = 0;
        Collection<LeagueMatchResult> playerMatchesPlayedOn = getPlayerMatchesInSeries(league, serie, player);
        for (LeagueMatchResult leagueMatch : playerMatchesPlayedOn) {
            if (leagueMatch.getWinner().equals(player))
                count++;
        }

        CardCollection prize;
        if (winner)
            prize = serie.getPrizeForLeagueMatchWinner(count, playerMatchesPlayedOn.size());
        else
            prize = serie.getPrizeForLeagueMatchLoser(count, playerMatchesPlayedOn.size());
        if (prize != null)
            _collectionsManager.addItemsToPlayerCollection(true, "Prize for winning league game", player, CollectionType.MY_CARDS, prize.getAll().values());
    }

    public synchronized Collection<LeagueMatchResult> getPlayerMatchesInSeries(League league, LeagueSeriesData serie, String player) {
        final Collection<LeagueMatchResult> allMatches = _leagueMatchDao.getLeagueMatches(league.getType());
        Set<LeagueMatchResult> result = new HashSet<LeagueMatchResult>();
        for (LeagueMatchResult match : allMatches) {
            if (match.getSerieName().equals(serie.getName()) && (match.getWinner().equals(player) || match.getLoser().equals(player)))
                result.add(match);
        }
        return result;
    }

    public synchronized List<PlayerStanding> getLeagueStandings(League league) {
        List<PlayerStanding> leagueStandings = _leagueStandings.get(LeagueMapKeys.getLeagueMapKey(league));
        if (leagueStandings == null) {
            synchronized (this) {
                leagueStandings = createLeagueStandings(league);
                _leagueStandings.put(LeagueMapKeys.getLeagueMapKey(league), leagueStandings);
            }
        }
        return leagueStandings;
    }

    public synchronized List<PlayerStanding> getLeagueSeriesStandings(League league, LeagueSeriesData leagueSerie) {
        List<PlayerStanding> serieStandings = _leagueSeriesStandings.get(LeagueMapKeys.getLeagueSeriesMapKey(league, leagueSerie));
        if (serieStandings == null) {
            synchronized (this) {
                serieStandings = createLeagueSeriesStandings(league, leagueSerie);
                _leagueSeriesStandings.put(LeagueMapKeys.getLeagueSeriesMapKey(league, leagueSerie), serieStandings);
            }
        }
        return serieStandings;
    }

    private List<PlayerStanding> createLeagueSeriesStandings(League league, LeagueSeriesData leagueSerie) {
        final Collection<String> playersParticipating = _leagueParticipationDAO.getUsersParticipating(league.getType());
        final Collection<LeagueMatchResult> matches = _leagueMatchDao.getLeagueMatches(league.getType());

        Set<LeagueMatchResult> matchesInSerie = new HashSet<LeagueMatchResult>();
        for (LeagueMatchResult match : matches) {
            if (match.getSerieName().equals(leagueSerie.getName()))
                matchesInSerie.add(match);
        }

        return createStandingsForMatchesAndPoints(playersParticipating, matchesInSerie);
    }

    private List<PlayerStanding> createLeagueStandings(League league) {
        final Collection<String> playersParticipating = _leagueParticipationDAO.getUsersParticipating(league.getType());
        final Collection<LeagueMatchResult> matches = _leagueMatchDao.getLeagueMatches(league.getType());

        return createStandingsForMatchesAndPoints(playersParticipating, matches);
    }

    private List<PlayerStanding> createStandingsForMatchesAndPoints(Collection<String> playersParticipating, Collection<LeagueMatchResult> matches) {
        return StandingsProducer.produceStandings(playersParticipating, matches, 3, 1, Collections.<String, Integer>emptyMap());
    }

    public synchronized boolean canPlayRankedGame(League league, LeagueSeriesData season, String player) {
        int maxMatches = season.getMaxMatches();
        Collection<LeagueMatchResult> playedInSeason = getPlayerMatchesInSeries(league, season, player);
        if (playedInSeason.size() >= maxMatches)
            return false;
        return true;
    }

    public synchronized boolean canPlayRankedGameAsSide(League league, LeagueSeriesData season, String player, Side deckSide) {
        int numAsSide = 0;
        //int numAsOtherSide = 0;
        Collection<LeagueMatchResult> playedInSeason = getPlayerMatchesInSeries(league, season, player);
        for (LeagueMatchResult leagueMatch : playedInSeason) {
            if ((player.equals(leagueMatch.getWinner()) && deckSide.getHumanReadable().equals(leagueMatch.getWinnerSide()))
                    || (player.equals(leagueMatch.getLoser()) && deckSide.getHumanReadable().equals(leagueMatch.getLoserSide()))) {
                numAsSide++;
            }
//            else if ((player.equals(leagueMatch.getWinner()) && !deckSide.getHumanReadable().equals(leagueMatch.getWinnerSide()))
//                    || (player.equals(leagueMatch.getLoser()) && !deckSide.getHumanReadable().equals(leagueMatch.getLoserSide()))) {
//                numAsOtherSide++;
//            }
        }
        int maxMatches = season.getMaxMatches();
        if (numAsSide >= maxMatches / 2)
            return false;

//        if (numAsSide > numAsOtherSide)
//            return false;

        return true;
    }

    public synchronized boolean canPlayRankedGameAgainst(League league, LeagueSeriesData season, String playerOne, String playerTwo, Side playerTwoSide) {
        Collection<LeagueMatchResult> playedInSeason = getPlayerMatchesInSeries(league, season, playerOne);
        for (LeagueMatchResult leagueMatch : playedInSeason) {
            if ((playerTwo.equals(leagueMatch.getWinner()) && playerTwoSide.getHumanReadable().equals(leagueMatch.getWinnerSide()))
                    || (playerTwo.equals(leagueMatch.getLoser()) && playerTwoSide.getHumanReadable().equals(leagueMatch.getLoserSide()))) {
                return false;
            }
        }
        return true;
    }
}
