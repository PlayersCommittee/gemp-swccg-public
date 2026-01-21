package com.gempukku.swccgo.game;

import com.gempukku.swccgo.AbstractServer;
import com.gempukku.swccgo.PrivateInformationException;
import com.gempukku.swccgo.ai.AiRegistry;
import com.gempukku.swccgo.chat.ChatCommandErrorException;
import com.gempukku.swccgo.chat.ChatServer;
import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.db.InGameStatisticsDAO;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.logic.timing.GameResultListener;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import com.gempukku.util.SwccgUuid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SwccgoServer extends AbstractServer {
    private static final Logger log = LogManager.getLogger(SwccgoServer.class);

    private SwccgCardBlueprintLibrary _swccgCardBlueprintLibrary;

    private Map<String, SwccgGameMediator> _runningGames = new ConcurrentHashMap<String, SwccgGameMediator>();
    private Set<String> _gameDeathWarningsSent = new HashSet<String>();

    private final Map<String, Date> _finishedGamesTime = Collections.synchronizedMap(new LinkedHashMap<String, Date>());
    private final long _timeToGameDeath = 1000 * 60 * 5; // 5 minutes
    private final long _timeToGameDeathWarning = 1000 * 60 * 4; // 4 minutes

    private DeckDAO _deckDao;
    private InGameStatisticsDAO _inGameStatisticsDAO;

    private ChatServer _chatServer;
    private GameRecorder _gameRecorder;

    private ReadWriteLock _lock = new ReentrantReadWriteLock();

    public SwccgoServer(DeckDAO deckDao, SwccgCardBlueprintLibrary library, ChatServer chatServer, GameRecorder gameRecorder, InGameStatisticsDAO inGameStatisticsDAO) {
        _deckDao = deckDao;
        _swccgCardBlueprintLibrary = library;
        _chatServer = chatServer;
        _gameRecorder = gameRecorder;
        _inGameStatisticsDAO = inGameStatisticsDAO;
    }

    protected void cleanup() {
        _lock.writeLock().lock();
        try {
            long currentTime = System.currentTimeMillis();

            LinkedHashMap<String, Date> copy = new LinkedHashMap<String, Date>(_finishedGamesTime);
            for (Map.Entry<String, Date> finishedGame : copy.entrySet()) {
                String gameId = finishedGame.getKey();
                if (currentTime > finishedGame.getValue().getTime() + _timeToGameDeathWarning
                        && !_gameDeathWarningsSent.contains(gameId)) {
                    try {
                        _chatServer.getChatRoom(getChatRoomName(gameId)).sendMessage("System", "This game is already finished and will be shortly removed, please move to the Game Hall", true);
                    } catch (PrivateInformationException exp) {
                        // Ignore, sent as admin
                    } catch (ChatCommandErrorException e) {
                        // Ignore, no command
                    }
                    _gameDeathWarningsSent.add(gameId);
                }
                if (currentTime > finishedGame.getValue().getTime() + _timeToGameDeath) {
                    _runningGames.get(gameId).destroy();
                    AiRegistry.unregisterGame(gameId);
                    _gameDeathWarningsSent.remove(gameId);
                    _runningGames.remove(gameId);
                    _chatServer.destroyChatRoom(getChatRoomName(gameId));
                    _finishedGamesTime.remove(gameId);
                } else {
                    break;
                }
            }

            for (SwccgGameMediator swccgGameMediator : _runningGames.values())
                swccgGameMediator.cleanup();
        } finally {
            _lock.writeLock().unlock();
        }
    }

    private String getChatRoomName(String gameId) {
        return "Game" + gameId;
    }

    public SwccgGameMediator createNewGame(SwccgFormat swccgFormat, League league, String tournamentName, final SwccgGameParticipant[] participants, boolean allowSpectators, boolean cancelIfNoActions, boolean allowCancelling, boolean allowSpectatorsToViewChat, boolean allowSpectatorsToChat, boolean allowExtendGameTimer, int decisionTimeoutSeconds, int timePerPlayerMinutes, boolean isPrivate, boolean inGameStatisticsOn, boolean bonusAbilitiesEnabled) {
        _lock.writeLock().lock();
        try {
            if (participants.length < 2)
                throw new IllegalArgumentException("There has to be at least two players");
            /*
             * Generate a new table ID based on a UUID.
             * Generating the table ID from a UUID means that the previous method of auto-incrememting the tableId
             * is removed from the internal memory of the gemp server.
             */
            final String gameId = new SwccgUuid().generateNewTableId();


            Set<String> allowedUsers = new HashSet<String>();
            for (SwccgGameParticipant participant : participants) {
                allowedUsers.add(participant.getPlayerId());
            }

            if (!allowSpectatorsToViewChat||isPrivate) {
                _chatServer.createPrivateChatRoom(getChatRoomName(gameId), false, allowedUsers, 30);
            } else {
                _chatServer.createChatRoom(getChatRoomName(gameId), false, 30, allowedUsers, allowSpectatorsToChat, swccgFormat.isPlaytesting());
            }

            /*
             * This is the game timer.
             * A standard game is 60 minutes, or: timePerPlayerMinutes * 60;
             */
            int maxPlayerTime = timePerPlayerMinutes * swccgFormat.getDefaultGameTimerMinutes();
            SwccgGameMediator swccgGameMediator = new SwccgGameMediator(gameId, swccgFormat, league, participants, _swccgCardBlueprintLibrary,
                        maxPlayerTime, allowSpectators, cancelIfNoActions, allowCancelling, allowExtendGameTimer, decisionTimeoutSeconds, isPrivate,
                        bonusAbilitiesEnabled&&(tournamentName==null||tournamentName.equals("Casual")));


            swccgGameMediator.addGameResultListener(
                    new GameResultListener() {
                        @Override
                        public void gameFinished(String winnerPlayerId, String winReason, Map<String, String> loserPlayerIdsWithReasons, String winnerSide, String loserSide) {
                            _finishedGamesTime.put(gameId, new Date());
                        }

                        @Override
                        public void gameCancelled() {
                            _finishedGamesTime.put(gameId, new Date());
                        }
                    });
            swccgGameMediator.sendMessageToPlayers("You're starting a game of " + swccgFormat.getName());

            if (inGameStatisticsOn && !isPrivate)
                swccgGameMediator.addInGameStatisticsListener(new GameStatisticsProcessor(_inGameStatisticsDAO, gameId));

            StringBuilder players = new StringBuilder();
            Map<String, String> deckNames = new HashMap<String, String>();
            for (SwccgGameParticipant participant : participants) {
                deckNames.put(participant.getPlayerId(), participant.getDeck().getDeckName());
                if (players.length() > 0)
                    players.append(", ");
                players.append(participant.getPlayerId());
            }

            swccgGameMediator.sendMessageToPlayers("Players in the game are: " + players);

            final GameRecorder.GameRecordingInProgress gameRecordingInProgress = _gameRecorder.recordGame(swccgGameMediator, swccgFormat.getName(), tournamentName, deckNames);
            swccgGameMediator.addGameResultListener(
                    new GameResultListener() {
                        @Override
                        public void gameFinished(String winnerPlayerId, String winReason, Map<String, String> loserPlayerIdsWithReasons, String winnerSide, String loserSide) {
                            final Map.Entry<String, String> loserEntry = loserPlayerIdsWithReasons.entrySet().iterator().next();

                            gameRecordingInProgress.finishRecording(winnerPlayerId, winReason, loserEntry.getKey(), loserEntry.getValue());
                        }

                        @Override
                        public void gameCancelled() {
                            gameRecordingInProgress.finishRecording(participants[0].getPlayerId(), "Game cancelled due to error", participants[1].getPlayerId(), "Game cancelled due to error");
                        }
                    }
            );

            _runningGames.put(gameId, swccgGameMediator);
            return swccgGameMediator;
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public SwccgDeck getParticipantDeck(Player player, String deckName) {
        return _deckDao.getDeckForPlayer(player, deckName);
    }

    public SwccgDeck createDeckWithValidate(String deckName, String contents) {
        return _deckDao.buildDeckFromContents(deckName, contents);
    }

    public SwccgGameMediator getGameById(String gameId) {
        _lock.readLock().lock();
        try {
            return _runningGames.get(gameId);
        } finally {
            _lock.readLock().unlock();
        }
    }
}
