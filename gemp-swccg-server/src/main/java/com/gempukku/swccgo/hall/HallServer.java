package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.*;
import com.gempukku.swccgo.chat.ChatCommandCallback;
import com.gempukku.swccgo.chat.ChatCommandErrorException;
import com.gempukku.swccgo.chat.ChatRoomMediator;
import com.gempukku.swccgo.chat.ChatServer;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.ApplicationConfiguration;
import com.gempukku.swccgo.db.GempSettingDAO;
import com.gempukku.swccgo.db.IpBanDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.draft.Draft;
import com.gempukku.swccgo.draft.DraftChannelVisitor;
import com.gempukku.swccgo.draft.DraftFinishedException;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.league.LeagueSeriesData;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.GameResultListener;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import com.gempukku.swccgo.service.AdminService;
import com.gempukku.swccgo.tournament.*;
import com.gempukku.util.SwccgUuid;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HallServer extends AbstractServer {
    private final int _playerInactivityPeriod = 1000 * 60; // 60 seconds
    private final long _scheduledTournamentLoadTime = 1000 * 60 * 60 * 24 * 7; // Week
    private final long _repeatTournaments = 1000 * 60 * 60 * 24 * 2;

    private ChatServer _chatServer;
    private LeagueService _leagueService;
    private TournamentService _tournamentService;
    private SwccgCardBlueprintLibrary _library;
    private SwccgoFormatLibrary _formatLibrary;
    private CollectionsManager _collectionsManager;
    private SwccgoServer _swccgoServer;
    private PairingMechanismRegistry _pairingMechanismRegistry;
    private PlayerDAO _playerDAO;
    private IpBanDAO _ipBanDAO;
    private GempSettingDAO _gempSettingDAO;
    private AdminService _adminService;
    private TournamentPrizeSchemeRegistry _tournamentPrizeSchemeRegistry;

    private CollectionType _allCardsCollectionType = CollectionType.ALL_CARDS;

    private String _motd;

    private boolean _operational;
    private boolean _shutdown;
    private boolean _privateGamesEnabled;
    private boolean _inGameStatisticsEnabled;
    private boolean _bonusAbilitiesEnabled;

    private ReadWriteLock _hallDataAccessLock = new ReentrantReadWriteLock(false);

    private Map<String, AwaitingTable> _awaitingTables = new LinkedHashMap<String, AwaitingTable>();
    private Map<String, RunningTable> _runningTables = new LinkedHashMap<>();

    private Map<Player, HallCommunicationChannel> _playerChannelCommunication = new ConcurrentHashMap<Player, HallCommunicationChannel>();
    private int _nextChannelNumber = 0;

    private Map<String, Tournament> _runningTournaments = new LinkedHashMap<String, Tournament>();

    private Map<String, TournamentQueue> _tournamentQueues = new LinkedHashMap<String, TournamentQueue>();
    private final ChatRoomMediator _hallChat;
    private final GameResultListener _notifyHallListeners = new NotifyHallListenersGameResultListener();

    public HallServer(SwccgoServer swccgoServer, ChatServer chatServer, LeagueService leagueService, TournamentService tournamentService, SwccgCardBlueprintLibrary library,
                      SwccgoFormatLibrary formatLibrary, CollectionsManager collectionsManager,
                      PlayerDAO playerDAO, IpBanDAO ipBanDAO, GempSettingDAO gempSettingDAO,
                      AdminService adminService,
                      TournamentPrizeSchemeRegistry tournamentPrizeSchemeRegistry,
                      PairingMechanismRegistry pairingMechanismRegistry) {
        _swccgoServer = swccgoServer;
        _chatServer = chatServer;
        _leagueService = leagueService;
        _tournamentService = tournamentService;
        _library = library;
        _formatLibrary = formatLibrary;
        _collectionsManager = collectionsManager;
        _playerDAO = playerDAO;
        _ipBanDAO = ipBanDAO;
        _gempSettingDAO = gempSettingDAO;
        _privateGamesEnabled = _gempSettingDAO.privateGamesEnabled();
        _inGameStatisticsEnabled = _gempSettingDAO.inGameStatisticsEnabled();
        _bonusAbilitiesEnabled = _gempSettingDAO.bonusAbilitiesEnabled();
        _adminService = adminService;
        _tournamentPrizeSchemeRegistry = tournamentPrizeSchemeRegistry;
        _pairingMechanismRegistry = pairingMechanismRegistry;
        _hallChat = _chatServer.createChatRoom("Game Hall", true, 15, null, true, false);
        _hallChat.addChatCommandCallback("ban",
                new ChatCommandCallback() {
                    @Override
                    public void commandReceived(String from, String parameters, boolean admin) throws ChatCommandErrorException {
                        if (admin) {
                            _adminService.banUser(parameters.trim());
                        } else {
                            throw new ChatCommandErrorException("Only administrator can ban users");
                        }
                    }
                });
        _hallChat.addChatCommandCallback("banIp",
                new ChatCommandCallback() {
                    @Override
                    public void commandReceived(String from, String parameters, boolean admin) throws ChatCommandErrorException {
                        if (admin) {
                            _adminService.banIp(parameters.trim());
                        } else {
                            throw new ChatCommandErrorException("Only administrator can ban users");
                        }
                    }
                });
        _hallChat.addChatCommandCallback("banIpRange",
                new ChatCommandCallback() {
                    @Override
                    public void commandReceived(String from, String parameters, boolean admin) throws ChatCommandErrorException {
                        if (admin) {
                            _adminService.banIpPrefix(parameters.trim());
                        } else {
                            throw new ChatCommandErrorException("Only administrator can ban users");
                        }
                    }
                });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private void hallChanged() {
        for (HallCommunicationChannel hallCommunicationChannel : _playerChannelCommunication.values())
            hallCommunicationChannel.hallChanged();
    }

    @Override
    protected void doAfterStartup() {
        for (Tournament tournament : _tournamentService.getLiveTournaments())
            _runningTournaments.put(tournament.getTournamentId(), tournament);
    }

    public void setOperational() {
        _hallDataAccessLock.writeLock().lock();
        try {
            if (!_operational || _shutdown) {
                _operational = true;
                _shutdown = false;
                cancelWaitingTables();
                cancelTournamentQueues();
                _chatServer.sendSystemMessageToAllChatRooms("Server is in operational mode and games are now able to be started");
                hallChanged();
            }
        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    public void setShutdown() {
        _hallDataAccessLock.writeLock().lock();
        try {
            if (!_shutdown) {
                _shutdown = true;
                cancelWaitingTables();
                cancelTournamentQueues();
                _chatServer.sendSystemMessageToAllChatRooms("Server is in shutdown mode. No games may be started. Server will be restarted after all games have finished");
                hallChanged();
            }
        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    public void setMOTD(String motd) {
        _hallDataAccessLock.writeLock().lock();
        try {
            _motd = motd;
            hallChanged();
        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    public int getTablesCount() {
        _hallDataAccessLock.readLock().lock();
        try {
             return _runningTables.values().size();
        } finally {
            _hallDataAccessLock.readLock().unlock();
        }
    }

    private void cancelWaitingTables() {
        _awaitingTables.clear();
    }

    private void cancelTournamentQueues() {
        for (TournamentQueue tournamentQueue : _tournamentQueues.values())
            tournamentQueue.leaveAllPlayers(_collectionsManager);
    }



    /**
     * @return If table created, otherwise <code>false</code> (if the user already is sitting at a table or playing).
     */
    public void createNewTable(String type, Player player, String deckName, boolean sampleDeck, String tableDesc, boolean isPrivate, Player librarian) throws HallException {
        if (_shutdown)
            throw new HallException("Server is in shutdown mode. No games may be started. Server will be restarted after all games have finished.");

        if (!_operational)
            throw new HallException("Server is not yet in operational mode. Games may not be started yet.");

        _hallDataAccessLock.writeLock().lock();
        try {
            League league = null;
            LeagueSeriesData leagueSerie = null;
            CollectionType collectionType = _allCardsCollectionType;
            SwccgFormat format = _formatLibrary.getHallFormats().get(type);

            if (format == null) {
                // Maybe it's a league format?
                league = _leagueService.getLeagueByType(type);
                if (league != null) {
                    if (!_leagueService.isPlayerInLeague(league, player))
                        throw new HallException("You're not in that league");

                    leagueSerie = _leagueService.getCurrentLeagueSeries(league);
                    if (leagueSerie == null)
                        throw new HallException("There is no ongoing serie for that league");

                    if (!_leagueService.canPlayRankedGame(league, leagueSerie, player.getName()))
                        throw new HallException("You have already played max games in league");
                    format = _formatLibrary.getFormat(leagueSerie.getFormat());
                    collectionType = leagueSerie.getCollectionType();
               }
            }
            // It's not a normal format and also not a league one
            if (format == null)
                throw new HallException("This format is not supported: " + type);

            verifyNotExceedingMaxTables(player, true);

            SwccgDeck swccgDeck = validateUserAndDeck(format, player, deckName, collectionType, sampleDeck, librarian);

            Side side = swccgDeck.getSide(_library);

            if (league != null) {
                verifyNotPlayingLeagueGame(player, side, league);
            }

            if(isPrivate&&league!=null) {
                throw new HallException("League games cannot be private");
            }
            if(isPrivate&&format.isPlaytesting()) {
                throw new HallException("Playtesting games cannot be private");
            }

            boolean isPrivateGame = isPrivate&&privateGamesAllowed();


            /*
             * Generate a new table ID based on a UUID.
             * Generating the table ID from a UUID means that the previous method of auto-incrememting the tableId
             * is removed from the internal memory of the gemp server.
             */
            String tableId = new SwccgUuid().generateNewTableId();
            AwaitingTable table = new AwaitingTable(format, collectionType, league, leagueSerie, tableDesc, isPrivateGame);
            _awaitingTables.put(tableId, table);

            joinTableInternal(tableId, player.getName(), table, swccgDeck);
            hallChanged();
        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    public void togglePrivateGames() {
        _gempSettingDAO.togglePrivateGamesEnabled();
        _privateGamesEnabled = _gempSettingDAO.privateGamesEnabled();
    }

    public void toggleInGameStatistics() {
        _gempSettingDAO.toggleInGameStatisticsEnabled();
        _inGameStatisticsEnabled = _gempSettingDAO.inGameStatisticsEnabled();
    }


    public void toggleBonusAbilities() {
        _gempSettingDAO.toggleBonusAbilitiesEnabled();
        _bonusAbilitiesEnabled = _gempSettingDAO.bonusAbilitiesEnabled();
    }

    public boolean privateGamesAllowed() {
        return _privateGamesEnabled;
    }

    public boolean inGameStatisticsEnabled() {
        return _inGameStatisticsEnabled;
    }

    public boolean bonusAbilitiesEnabled() {
        return _bonusAbilitiesEnabled;
    }

    public int removeInGameStatisticsListeners() {
        int tableCount = 0;
        for (RunningTable runningTable : _runningTables.values()) {
                SwccgGameMediator game = runningTable.getSwccgoGameMediator();
                game.removeAllInGameStatisticsListeners();
                tableCount++;
        }

        return tableCount;
    }

    private void verifyNotPlayingLeagueGame(Player player, Side side, League league) throws HallException {
        String playerId = player.getName();
        for (AwaitingTable awaitingTable : _awaitingTables.values()) {
            if (awaitingTable.getLeague() == league
                    && awaitingTable.hasPlayer(playerId)) {

                Set<SwccgGameParticipant> players = awaitingTable.getPlayers();
                for (SwccgGameParticipant awaitingTablePlayer : players) {
                    if (playerId.equals(awaitingTablePlayer.getPlayerId())) {
                        Side awaitingTablePlayerSide = awaitingTablePlayer.getDeck().getSide(_library);

                        if (awaitingTablePlayerSide == side) {
                            throw new HallException("You can't host multiple league games on the same side of the force");
                        }
                    }
                }
            }
        }

        for (RunningTable runningTable : _runningTables.values()) {
            if (runningTable.getLeague() == league) {
                SwccgGameMediator game = runningTable.getSwccgoGameMediator();
                if (game != null && !game.isFinished() && game.isPlayerPlaying(player.getName()))
                    throw new HallException("You can't play in multiple league games at the same time");
            }
        }
    }

    private void verifyNotExceedingMaxTables(Player player, boolean forCreatingTable) throws HallException {
        final long MAX_TABLES_PER_PLAYER_FOR_CREATE = 8;
        final long MAX_TABLES_PER_PLAYER_FOR_JOIN = 2;
        int numTables = 0;

        if (forCreatingTable) {
            for (AwaitingTable awaitingTable : _awaitingTables.values()) {
                if (awaitingTable.hasPlayer(player.getName())) {
                    numTables++;
                }
            }
        }

        for (RunningTable runningTable : _runningTables.values()) {
            SwccgGameMediator game = runningTable.getSwccgoGameMediator();
            if (game != null && !game.isFinished() && game.isPlayerPlaying(player.getName())) {
                numTables++;
            }
        }

        if (forCreatingTable) {
            if (numTables >= MAX_TABLES_PER_PLAYER_FOR_CREATE) {
                throw new HallException("You can't create any more tables. You've reach the allowed limit of tables to have open at one time.");
            }
        }
        else {
            if (numTables >= MAX_TABLES_PER_PLAYER_FOR_JOIN) {
                throw new HallException("You can't join any more tables. You've reach the allowed limit to join at one time.");
            }
        }
    }

    public boolean joinQueue(String queueId, Player player, String deckName, boolean sampleDeck, Player librarian) throws HallException {
        if (_shutdown)
            throw new HallException("Server is in shutdown mode. No games may be started. Server will be restarted after all games have finished.");

        if (!_operational)
            throw new HallException("Server is not yet in operational mode. Games may not be started yet.");

        _hallDataAccessLock.writeLock().lock();
        try {
            TournamentQueue tournamentQueue = _tournamentQueues.get(queueId);
            if (tournamentQueue == null)
                throw new HallException("Tournament queue already finished accepting players, try again in a few seconds");
            if (tournamentQueue.isPlayerSignedUp(player.getName()))
                throw new HallException("You have already joined that queue");

            SwccgDeck swccgDeck = null;
            if (tournamentQueue.isRequiresDeck())
                swccgDeck = validateUserAndDeck(_formatLibrary.getFormat(tournamentQueue.getFormat()), player, deckName, tournamentQueue.getCollectionType(), sampleDeck, librarian);

            tournamentQueue.joinPlayer(_collectionsManager, player, swccgDeck);

            hallChanged();

            return true;
        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    /**
     * @return If table joined, otherwise <code>false</code> (if the user already is sitting at a table or playing).
     */
    public boolean joinTableAsPlayer(String tableId, Player player, String deckName, boolean sampleDeck, Player librarian) throws HallException {
        if (_shutdown)
            throw new HallException("Server is in shutdown mode. No games may be started. Server will be restarted after all games have finished.");

        if (!_operational)
            throw new HallException("Server is not yet in operational mode. Games may not be started yet.");

        _hallDataAccessLock.writeLock().lock();
        try {
            AwaitingTable awaitingTable = _awaitingTables.get(tableId);
            if (awaitingTable == null)
                throw new HallException("Table is already taken or was removed");

            if (awaitingTable.hasPlayer(player.getName()))
                throw new HallException("You can't play against yourself");

            if (awaitingTable.hasPlayer("DarkTest1") && !"LightTest1".equals(player.getName()))
                throw new HallException("You are not allowed to play against DarkTest1");

            if (awaitingTable.hasPlayer("LightTest1") && !"DarkTest1".equals(player.getName()))
                throw new HallException("You are not allowed to play against LightTest1");

            if (awaitingTable.getLeague() != null && !_leagueService.isPlayerInLeague(awaitingTable.getLeague(), player))
                throw new HallException("You're not in that league");

            if (awaitingTable.isPrivate() && !awaitingTable.getTableDesc().equals(player.getName()))
                throw new HallException("You may not join this private game");

            verifyNotExceedingMaxTables(player, false);

            SwccgDeck swccgDeck = validateUserAndDeck(awaitingTable.getSwccgoFormat(), player, deckName, awaitingTable.getCollectionType(), sampleDeck, librarian);

            joinTableInternal(tableId, player.getName(), awaitingTable, swccgDeck);

            hallChanged();

            return true;
        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    public void leaveQueue(String queueId, Player player) {
        _hallDataAccessLock.writeLock().lock();
        try {
            TournamentQueue tournamentQueue = _tournamentQueues.get(queueId);
            if (tournamentQueue != null && tournamentQueue.isPlayerSignedUp(player.getName())) {
                tournamentQueue.leavePlayer(_collectionsManager, player);
                hallChanged();
            }
        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    private boolean leaveQueuesForLeavingPlayer(Player player) {
        _hallDataAccessLock.writeLock().lock();
        try {
            boolean result = false;
            for (TournamentQueue tournamentQueue : _tournamentQueues.values()) {
                if (tournamentQueue.isPlayerSignedUp(player.getName())) {
                    tournamentQueue.leavePlayer(_collectionsManager, player);
                    result = true;
                }
            }
            return result;
        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    public void dropFromTournament(String tournamentId, Player player) {
        _hallDataAccessLock.writeLock().lock();
        try {
            Tournament tournament = _runningTournaments.get(tournamentId);
            if (tournament != null) {
                tournament.dropPlayer(player.getName());
                hallChanged();
            }
        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    public void singupForDraft(String tournamentId, Player player, DraftChannelVisitor draftChannelVisitor)
            throws DraftFinishedException {
        _hallDataAccessLock.readLock().lock();
        try {
            Tournament tournament = _runningTournaments.get(tournamentId);
            if (tournament == null)
                throw new DraftFinishedException();
            Draft draft = tournament.getDraft();
            if (draft == null)
                throw new DraftFinishedException();
            draft.signUpForDraft(player.getName(), draftChannelVisitor);
        } finally {
            _hallDataAccessLock.readLock().unlock();
        }
    }

    public Draft getDraft(String tournamentId) throws DraftFinishedException {
        _hallDataAccessLock.readLock().lock();
        try {
            Tournament tournament = _runningTournaments.get(tournamentId);
            if (tournament == null)
                throw new DraftFinishedException();
            Draft draft = tournament.getDraft();
            if (draft == null)
                throw new DraftFinishedException();
            return draft;
        } finally {
            _hallDataAccessLock.readLock().unlock();
        }
    }

    public void submitTournamentDeck(String tournamentId, Player player, SwccgDeck swccgDeck)
            throws HallException {
        _hallDataAccessLock.readLock().lock();
        try {
            Tournament tournament = _runningTournaments.get(tournamentId);
            if (tournament == null)
                throw new HallException("Tournament no longer accepts deck");
            SwccgFormat format = _formatLibrary.getFormat(tournament.getFormat());

            try {
                validateUserAndDeck(format, player, tournament.getCollectionType(), swccgDeck);

                tournament.playerSummittedDeck(player.getName(), swccgDeck);
            } catch (DeckInvalidException exp) {
                throw new HallException("Your deck is not valid in the tournament format: " + exp.getMessage());
            }
        } finally {
            _hallDataAccessLock.readLock().unlock();
        }
    }

    public void leaveAwaitingTable(Player player, String tableId) {
        _hallDataAccessLock.writeLock().lock();
        try {
            AwaitingTable table = _awaitingTables.get(tableId);
            if (table != null && table.hasPlayer(player.getName())) {
                boolean empty = table.removePlayer(player.getName());
                if (empty)
                    _awaitingTables.remove(tableId);
                hallChanged();
            }
        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    public boolean leaveAwaitingTablesForLeavingPlayer(Player player) {
        _hallDataAccessLock.writeLock().lock();
        try {
            boolean result = false;
            Map<String, AwaitingTable> copy = new HashMap<String, AwaitingTable>(_awaitingTables);
            for (Map.Entry<String, AwaitingTable> table : copy.entrySet()) {
                if (table.getValue().hasPlayer(player.getName())) {
                    boolean empty = table.getValue().removePlayer(player.getName());
                    if (empty)
                        _awaitingTables.remove(table.getKey());
                    result = true;
                }
            }
            return result;
        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    public void signupUserForHall(Player player, HallChannelVisitor hallChannelVisitor) {
        _hallDataAccessLock.readLock().lock();
        try {
            HallCommunicationChannel channel = new HallCommunicationChannel(_nextChannelNumber++);
            channel.processCommunicationChannel(this, player, hallChannelVisitor);
            _playerChannelCommunication.put(player, channel);
        } finally {
            _hallDataAccessLock.readLock().unlock();
        }
    }

    public HallCommunicationChannel getCommunicationChannel(Player player, int channelNumber) throws SubscriptionExpiredException, SubscriptionConflictException {
        _hallDataAccessLock.readLock().lock();
        try {
            HallCommunicationChannel communicationChannel = _playerChannelCommunication.get(player);
            if (communicationChannel != null) {
                if (communicationChannel.getChannelNumber() == channelNumber) {
                    return communicationChannel;
                } else {
                    throw new SubscriptionConflictException();
                }
            } else {
                throw new SubscriptionExpiredException();
            }
        } finally {
            _hallDataAccessLock.readLock().unlock();
        }
    }

    protected void processHall(Player player, HallInfoVisitor visitor) {
        _hallDataAccessLock.readLock().lock();
        try {
            visitor.serverTime(DateUtils.getStringDateWithHour());
            if (_shutdown) {
                visitor.motd("Server is in shutdown mode. No games may be started. Server will be restarted after all games have finished.");
            }
            else if (!_operational) {
                visitor.motd("Server is not yet in operational mode. Games may not be started yet.");
            }
            else if (_motd != null) {
                visitor.motd(_motd);
            }
            else {
                visitor.motd("Follow the PC on Twitter @swccg to stay informed of Star Wars CCG news and events.");
            }

            // Only show playtesting table details if player is a playtester or admin
            boolean playtestingVisible = player.hasType(Player.Type.ADMIN) || player.hasType(Player.Type.PLAY_TESTER);
            boolean visibleToCommentator = player.hasType(Player.Type.ADMIN) || player.hasType(Player.Type.COMMENTATOR);

            // First waiting
            for (Map.Entry<String, AwaitingTable> tableInformation : _awaitingTables.entrySet()) {
                final AwaitingTable table = tableInformation.getValue();
                List<SwccgGameParticipant> players = new LinkedList<SwccgGameParticipant>(table.getPlayers());

                boolean hidePlayerId = table.getLeague() != null && !table.getLeague().getShowPlayerNames();
                visitor.visitTable(tableInformation.getKey(), null, false, HallInfoVisitor.TableStatus.WAITING, "Waiting", table.getSwccgoFormat().getName(), getTournamentName(table), table.getLeague() != null ? null : table.getTableDesc(), players, null, table.getPlayerNames().contains(player.getName()), null, hidePlayerId, _library, table.getSwccgoFormat().isPlaytesting() && !playtestingVisible, true, true);
            }

            // Then non-finished
            Map<String, RunningTable> finishedTables = new HashMap<String, RunningTable>();

            for (Map.Entry<String, RunningTable> runningGame : _runningTables.entrySet()) {
                final RunningTable runningTable = runningGame.getValue();
                SwccgGameMediator swccgGameMediator = runningTable.getSwccgoGameMediator();
                if (swccgGameMediator != null) {
                    if (!swccgGameMediator.isFinished()) {
                        Map<String, String> deckArchetypeMap = new HashMap<String, String>();
                        for (SwccgGameParticipant participant : swccgGameMediator.getPlayersPlaying()) {
                            deckArchetypeMap.put(participant.getPlayerId(), swccgGameMediator.getDeckArchetypeLabel(participant.getPlayerId()));
                        }
                        visitor.visitTable(runningGame.getKey(), swccgGameMediator.getGameId(), !swccgGameMediator.isPrivate()&&(player.hasType(Player.Type.ADMIN)|| (swccgGameMediator.isAllowSpectators() && (!swccgGameMediator.getFormat().isPlaytesting() || playtestingVisible)) || (!swccgGameMediator.getFormat().isPlaytesting()&& visibleToCommentator)), HallInfoVisitor.TableStatus.PLAYING, swccgGameMediator.getGameStatus(), runningTable.getFormatName(), runningTable.getTournamentName(), runningTable.getTableDesc(), swccgGameMediator.getPlayersPlaying(), deckArchetypeMap, swccgGameMediator.isPlayerPlaying(player.getName()), swccgGameMediator.getWinner(), false, _library, swccgGameMediator.getFormat().isPlaytesting() && !playtestingVisible, swccgGameMediator.isPrivate()||(swccgGameMediator.getFormat().isPlaytesting() && !playtestingVisible), swccgGameMediator.isPrivate());
                    }
                    else {
                        finishedTables.put(runningGame.getKey(), runningTable);
                    }
                    if (!swccgGameMediator.isFinished() && swccgGameMediator.isPlayerPlaying(player.getName()))
                        visitor.runningPlayerGame(swccgGameMediator.getGameId());
                }
            }

            // Then rest
            for (Map.Entry<String, RunningTable> nonPlayingGame : finishedTables.entrySet()) {
                final RunningTable runningTable = nonPlayingGame.getValue();
                SwccgGameMediator swccgGameMediator = runningTable.getSwccgoGameMediator();
                if (swccgGameMediator != null) {
                    Map<String, String> deckArchetypeMap = new HashMap<String, String>();
                    for (SwccgGameParticipant participant : swccgGameMediator.getPlayersPlaying()) {
                        deckArchetypeMap.put(participant.getPlayerId(), swccgGameMediator.getDeckArchetypeLabel(participant.getPlayerId()));
                    }
                    visitor.visitTable(nonPlayingGame.getKey(), swccgGameMediator.getGameId(), false, HallInfoVisitor.TableStatus.FINISHED, swccgGameMediator.getGameStatus(), runningTable.getFormatName(), runningTable.getTournamentName(), runningTable.getTableDesc(), swccgGameMediator.getPlayersPlaying(), deckArchetypeMap, swccgGameMediator.isPlayerPlaying(player.getName()), swccgGameMediator.getWinner(), false, _library, swccgGameMediator.getFormat().isPlaytesting() && !playtestingVisible, swccgGameMediator.isPrivate()||(swccgGameMediator.getFormat().isPlaytesting() && !playtestingVisible), swccgGameMediator.isPrivate());
                }
            }

            for (Map.Entry<String, TournamentQueue> tournamentQueueEntry : _tournamentQueues.entrySet()) {
                String tournamentQueueKey = tournamentQueueEntry.getKey();
                TournamentQueue tournamentQueue = tournamentQueueEntry.getValue();
                visitor.visitTournamentQueue(tournamentQueueKey, tournamentQueue.getCost(), tournamentQueue.getCollectionType().getFullName(),
                        _formatLibrary.getFormat(tournamentQueue.getFormat()).getName(), tournamentQueue.getTournamentQueueName(),
                        tournamentQueue.getPrizesDescription(), tournamentQueue.getPairingDescription(), tournamentQueue.getStartCondition(),
                        tournamentQueue.getPlayerCount(), tournamentQueue.isPlayerSignedUp(player.getName()), tournamentQueue.isJoinable());
            }

            for (Map.Entry<String, Tournament> tournamentEntry : _runningTournaments.entrySet()) {
                String tournamentKey = tournamentEntry.getKey();
                Tournament tournament = tournamentEntry.getValue();
                visitor.visitTournament(tournamentKey, tournament.getCollectionType().getFullName(),
                        _formatLibrary.getFormat(tournament.getFormat()).getName(), tournament.getTournamentName(), tournament.getPlayOffSystem(),
                        tournament.getTournamentStage().getHumanReadable(),
                        tournament.getCurrentRound(), tournament.getPlayersInCompetitionCount(), tournament.isPlayerInCompetition(player.getName()));
            }
        } finally {
            _hallDataAccessLock.readLock().unlock();
        }
    }

    private SwccgDeck validateUserAndDeck(SwccgFormat format, Player player, String deckName, CollectionType collectionType, boolean sampleDeck, Player librarian) throws HallException {

        /*
         * Only show playtesting formats if player is a playtester or admin.
         */
        if (format.isPlaytesting()
                && !(player.hasType(Player.Type.ADMIN)
                || player.hasType(Player.Type.PLAY_TESTER))) {
            throw new HallException("You are not allowed to participate in a playtesting format");
        }

        SwccgDeck swccgDeck;
        if (sampleDeck)
            /*
             * Sample decks come from the Librarian user.
             */
            swccgDeck = _swccgoServer.getParticipantDeck(librarian, deckName);
        else
            /*
             * All other decks come from the logged in user.
             */
            swccgDeck = _swccgoServer.getParticipantDeck(player, deckName);

        if (swccgDeck == null)
            throw new HallException("You don't have a deck registered yet");

        /*
         * Pull playtestingNoLimitDeckLength from properties file.
         * The properties file will pull the value from the environment variable: playtesting_no_limit_deck_length
         * Or it will use the default value set in the file for parameter: playtesting.noLimitDeckLength
         */
        Boolean playtestingNoLimitDeckLength = Boolean.parseBoolean(ApplicationConfiguration.getProperty("playtesting.noLimitDeckLength"));
        if (playtestingNoLimitDeckLength) {
            System.out.println("Playtesting has no Deck Length Limit");
        } else {
            System.out.println("Playtesting deck length is restricted to the format limit");
        }

        /*
         * If playtestingNoLimitDeckLength is true:
         *   Allow playtesters and admins to have decks with no limit.
         *   This feature allows Playtesters, and developers, to create decks composed exclusively of the cards they are testing.
         * If the playtestingNoLimitDeckLength is false:
         *   Then the deck length limit is respected.
         */
        if (! (playtestingNoLimitDeckLength && (player.hasType(Player.Type.ADMIN) || player.hasType(Player.Type.PLAY_TESTER))) ) {

            try {
                swccgDeck = validateUserAndDeck(format, player, collectionType, swccgDeck);
            } catch (DeckInvalidException e) {
                throw new HallException("Your selected deck is not valid for this format: " + e.getMessage());
            }

        } // validate deck

        return swccgDeck;
    }

    private SwccgDeck validateUserAndDeck(SwccgFormat format, Player player, CollectionType collectionType, SwccgDeck swccgDeck) throws HallException, DeckInvalidException {
        format.validateDeck(swccgDeck);

        // Now check if player owns all the cards
        if ("default".equals(collectionType.getCode())) {
            CardCollection ownedCollection = _collectionsManager.getPlayerCollection(player, "permanent");

            // Replace any special cards (foils, etc.) not owned with the base card
            SwccgDeck filteredDeck = new SwccgDeck(swccgDeck.getDeckName());
            Map<String, Integer> cardCountSoFar = new HashMap<String, Integer>();

            // Look through the cards in order to keep the deck order the same
            for (int i=0; i<swccgDeck.getCards().size(); ++i) {
                String blueprintId = swccgDeck.getCards().get(i);
                Integer countSoFar = cardCountSoFar.get(blueprintId);
                if (countSoFar == null) {
                    countSoFar = 0;
                }
                countSoFar++;
                cardCountSoFar.put(blueprintId, countSoFar);

                int owned = 0;
                if (ownedCollection != null) {
                    owned = ownedCollection.getItemCount(blueprintId);
                }

                if (owned < countSoFar) {
                    blueprintId = _library.getBaseBlueprintId(blueprintId);
                }
                filteredDeck.addCard(blueprintId);
            }

            for (int i=0; i<swccgDeck.getCardsOutsideDeck().size(); ++i) {
                String blueprintId = swccgDeck.getCardsOutsideDeck().get(i);
                Integer countSoFar = cardCountSoFar.get(blueprintId);
                if (countSoFar == null) {
                    countSoFar = 0;
                }
                countSoFar++;
                cardCountSoFar.put(blueprintId, countSoFar);

                int owned = 0;
                if (ownedCollection != null) {
                    owned = ownedCollection.getItemCount(blueprintId);
                }

                if (owned < countSoFar) {
                    blueprintId = _library.getBaseBlueprintId(blueprintId);
                }
                filteredDeck.addCardOutsideDeck(blueprintId);
            }
            swccgDeck = filteredDeck;

        } else {
            CardCollection collection = _collectionsManager.getPlayerCollection(player, collectionType.getCode());
            if (collection == null)
                throw new HallException("You don't have cards in the required collection to play in this format");

            Map<String, Integer> deckCardCounts = CollectionUtils.getTotalCardCountForDeck(swccgDeck);

            for (Map.Entry<String, Integer> cardCount : deckCardCounts.entrySet()) {
                final int collectionCount = collection.getItemCount(cardCount.getKey());
                if (collectionCount < cardCount.getValue()) {
                    String cardName = GameUtils.getFullName(_library.getSwccgoCardBlueprint(cardCount.getKey()));
                    throw new HallException("You don't have the required cards in collection: " + cardName + " required " + cardCount.getValue() + ", owned " + collectionCount);
                }
            }
        }
        return swccgDeck;
    }

    private String filterCard(String blueprintId, CardCollection ownedCollection) {
        if (ownedCollection == null || ownedCollection.getItemCount(blueprintId) == 0)
            return _library.getBaseBlueprintId(blueprintId);
        return blueprintId;
    }

    private String getTournamentName(AwaitingTable table) {
        String tournamentName = (table.getSwccgoFormat().isPlaytesting() ? "Playtesting" : "Casual");
        if(table.isPrivate())
            tournamentName += " (Private)";

        final League league = table.getLeague();
        if (league != null) {
            tournamentName = league.getName() + " - " + table.getLeagueSeries().getName();
        }
        return tournamentName;
    }

    private void createGameFromAwaitingTable(String tableId, AwaitingTable awaitingTable) {
        Set<SwccgGameParticipant> players = awaitingTable.getPlayers();
        SwccgGameParticipant[] participants = players.toArray(new SwccgGameParticipant[players.size()]);
        final League league = awaitingTable.getLeague();
        final LeagueSeriesData leagueSerie = awaitingTable.getLeagueSeries();

        GameResultListener listener = null;
        if (league != null) {
            listener = new GameResultListener() {
                @Override
                public void gameFinished(String winnerPlayerId, String winReason, Map<String, String> loserPlayerIdsWithReasons, String winnerSide, String loserSide) {
                    _leagueService.reportLeagueGameResult(league, leagueSerie, winnerPlayerId, loserPlayerIdsWithReasons.keySet().iterator().next(), winnerSide, loserSide);
                }

                @Override
                public void gameCancelled() {
                    // Do nothing...
                }
            };
        }


        int decisionTimeoutSeconds = 300; // 5 minutes;
        boolean allowSpectators = !awaitingTable.isPrivate();
        boolean allowTimerExtensions = true;
        int timePerPlayerMinutes = 60;
        if (league != null) {
            decisionTimeoutSeconds = league.getDecisionTimeoutSeconds();
            allowSpectators = league.getAllowSpectators();
            allowTimerExtensions = league.getAllowTimeExtensions();
            timePerPlayerMinutes = league.getTimePerPlayerMinutes();
        }
        createGame(league, leagueSerie, tableId, participants, listener, awaitingTable.getSwccgoFormat(), getTournamentName(awaitingTable), league != null ? null : awaitingTable.getTableDesc(), allowSpectators, true, !awaitingTable.isPrivate(), (league == null)&&!awaitingTable.isPrivate(), allowTimerExtensions, decisionTimeoutSeconds, timePerPlayerMinutes, awaitingTable.isPrivate());
        _awaitingTables.remove(tableId);
        removeWaitingTablesWithPlayers(players);
    }

    /**
     * Removes all waiting tables with the specified players. This is to avoid having extra tables left around for when
     * a player creates several tables in the hall, and then a game with the player starts. Players often forget to close
     * the unused tables.
     * @param participants the game participants
     */
    private void removeWaitingTablesWithPlayers(Set<SwccgGameParticipant> participants) {
        // Figure out table ids to be removed
        List<String> idsToRemove = new ArrayList<String>();
        for (String tableId : _awaitingTables.keySet()) {
            AwaitingTable awaitingTable = _awaitingTables.get(tableId);
            for (SwccgGameParticipant participant : participants) {
                if (awaitingTable.hasPlayer(participant.getPlayerId())) {
                    idsToRemove.add(tableId);
                    break;
                }
            }
        }

        // Remove the waiting tables with the ids
        for (String idToRemove : idsToRemove) {
            _awaitingTables.remove(idToRemove);
        }
    }

    private void createGame(League league, LeagueSeriesData leagueSerie, String tableId, SwccgGameParticipant[] participants, GameResultListener listener, SwccgFormat swccgFormat, String tournamentName, String tableDesc, boolean allowSpectators, boolean allowCancelling, boolean allowSpectatorsToViewChat, boolean allowSpectatorsToChat, boolean allowExtendGameTimer, int decisionTimeoutSeconds, int timePerPlayerMinutes, boolean isPrivate) {
        SwccgGameMediator swccgGameMediator = _swccgoServer.createNewGame(swccgFormat, league, tournamentName, participants, allowSpectators, league == null, allowCancelling, allowSpectatorsToViewChat, allowSpectatorsToChat, allowExtendGameTimer, decisionTimeoutSeconds, timePerPlayerMinutes, isPrivate, _inGameStatisticsEnabled, _bonusAbilitiesEnabled);
        if (listener != null) {
            swccgGameMediator.addGameResultListener(listener);
        }
        swccgGameMediator.startGame();
        swccgGameMediator.addGameResultListener(_notifyHallListeners);
        _runningTables.put(tableId, new RunningTable(swccgGameMediator, swccgFormat.getName(), tournamentName, tableDesc, league, leagueSerie));
    }

    private class NotifyHallListenersGameResultListener implements GameResultListener {
        @Override
        public void gameCancelled() {
            hallChanged();
        }

        @Override
        public void gameFinished(String winnerPlayerId, String winReason, Map<String, String> loserPlayerIdsWithReasons, String winnerSide, String loserSide) {
            hallChanged();
        }
    }

    private void joinTableInternal(String tableId, String player, AwaitingTable awaitingTable, SwccgDeck swccgDeck) throws HallException {
        League league = awaitingTable.getLeague();
        Side side = swccgDeck.getSide(_library);
        if (league != null) {
            LeagueSeriesData leagueSerie = awaitingTable.getLeagueSeries();
            if (!_leagueService.canPlayRankedGame(league, leagueSerie, player))
                throw new HallException("You have already played max games in league");
            if (!_leagueService.canPlayRankedGameAsSide(league, leagueSerie, player, side)) {
                Side otherSide = (side== Side.DARK) ? Side.LIGHT : Side.DARK;
                throw new HallException("You have already played max games in league as " + side.getHumanReadable() + ", but you may still play as " + otherSide.getHumanReadable());
            }
            if (!awaitingTable.getPlayerNames().isEmpty()) {
                if (!_leagueService.canPlayRankedGameAgainst(league, leagueSerie, awaitingTable.getPlayerNames().iterator().next(), player, side)) {
                    throw new HallException("You have already played multiple league games against this player");
                }
                Player curPlayer = _playerDAO.getPlayer(player);
                Player awaitingPlayer = _playerDAO.getPlayer(awaitingTable.getPlayerNames().iterator().next());
                //TODO: Add this back. Temporarily removed.
                //See: https://github.com/PlayersCommittee/gemp-swccg/pull/87
                //if (curPlayer != null && awaitingPlayer != null && curPlayer.getLastIp().equals(awaitingPlayer.getLastIp())) {
                //    throw new HallException("You are not allowed to play league games against this player");
                //}
            }
        }

        // Check that game will be Light side vs Dark side
        List<SwccgGameParticipant> participants = new LinkedList<SwccgGameParticipant>(awaitingTable.getPlayers());
        for (SwccgGameParticipant participant : participants) {
            if (participant.getDeck().getSide(_library)==side) {
                throw new HallException("You can't play against an opponent with a deck from the same side of the Force");
            }
        }

        boolean tableFull = awaitingTable.addPlayer(new SwccgGameParticipant(player, swccgDeck));
        if (tableFull)
            createGameFromAwaitingTable(tableId, awaitingTable);
    }

    private int _tickCounter = 60;

    @Override
    protected void cleanup() {
        _hallDataAccessLock.writeLock().lock();
        try {
            // Remove finished games
            HashMap<String, RunningTable> copy = new HashMap<String, RunningTable>(_runningTables);
            for (Map.Entry<String, RunningTable> runningTable : copy.entrySet()) {
                SwccgGameMediator swccgGameMediator = runningTable.getValue().getSwccgoGameMediator();
                if (swccgGameMediator.isDestroyed()) {
                    _runningTables.remove(runningTable.getKey());
                    hallChanged();
                }
            }

            long currentTime = System.currentTimeMillis();
            Map<Player, HallCommunicationChannel> visitCopy = new LinkedHashMap<Player, HallCommunicationChannel>(_playerChannelCommunication);
            for (Map.Entry<Player, HallCommunicationChannel> lastVisitedPlayer : visitCopy.entrySet()) {
                if (currentTime > lastVisitedPlayer.getValue().getLastAccessed() + _playerInactivityPeriod) {
                    Player player = lastVisitedPlayer.getKey();
                    _playerChannelCommunication.remove(player);
                    boolean leftTables = leaveAwaitingTablesForLeavingPlayer(player);
                    boolean leftQueues = leaveQueuesForLeavingPlayer(player);
                    if (leftTables || leftQueues)
                        hallChanged();
                }
            }

            for (Map.Entry<String, TournamentQueue> runningTournamentQueue : new HashMap<String, TournamentQueue>(_tournamentQueues).entrySet()) {
                String tournamentQueueKey = runningTournamentQueue.getKey();
                TournamentQueue tournamentQueue = runningTournamentQueue.getValue();
                HallTournamentQueueCallback queueCallback = new HallTournamentQueueCallback();
                // If it's finished, remove it
                if (tournamentQueue.process(queueCallback, _collectionsManager)) {
                    _tournamentQueues.remove(tournamentQueueKey);
                    hallChanged();
                }
            }

            for (Map.Entry<String, Tournament> tournamentEntry : new HashMap<String, Tournament>(_runningTournaments).entrySet()) {
                Tournament runningTournament = tournamentEntry.getValue();
                boolean changed = runningTournament.advanceTournament(new HallTournamentCallback(runningTournament), _collectionsManager);
                if (runningTournament.getTournamentStage() == Tournament.Stage.FINISHED)
                    _runningTournaments.remove(tournamentEntry.getKey());
                if (changed)
                    hallChanged();
            }

            if (_tickCounter == 60) {
                _tickCounter = 0;
                List<TournamentQueueInfo> unstartedTournamentQueues = _tournamentService.getUnstartedScheduledTournamentQueues(
                        System.currentTimeMillis() + _scheduledTournamentLoadTime);
                for (TournamentQueueInfo unstartedTournamentQueue : unstartedTournamentQueues) {
                    String scheduledTournamentId = unstartedTournamentQueue.getScheduledTournamentId();
                    if (!_tournamentQueues.containsKey(scheduledTournamentId)) {
                        ScheduledTournamentQueue scheduledQueue = new ScheduledTournamentQueue(scheduledTournamentId, unstartedTournamentQueue.getCost(),
                                true, _tournamentService, unstartedTournamentQueue.getStartTime(), unstartedTournamentQueue.getTournamentName(),
                                unstartedTournamentQueue.getFormat(), CollectionType.ALL_CARDS, Tournament.Stage.PLAYING_GAMES,
                                _pairingMechanismRegistry.getPairingMechanism(unstartedTournamentQueue.getPlayOffSystem()),
                                _tournamentPrizeSchemeRegistry.getTournamentPrizes(unstartedTournamentQueue.getPrizeScheme()), unstartedTournamentQueue.getMinimumPlayers());
                        _tournamentQueues.put(scheduledTournamentId, scheduledQueue);
                        hallChanged();
                    }
                }
            }
            _tickCounter++;

        } finally {
            _hallDataAccessLock.writeLock().unlock();
        }
    }

    private class HallTournamentQueueCallback implements TournamentQueueCallback {
        @Override
        public void createTournament(Tournament tournament) {
            _runningTournaments.put(tournament.getTournamentId(), tournament);
        }
    }

    private class HallTournamentCallback implements TournamentCallback {
        private Tournament _tournament;
        private int _decisionTimeoutSeconds = 300; // 5 minutes
        private int _timePerPlayerMinutes = 50;

        private HallTournamentCallback(Tournament tournament) {
            _tournament = tournament;
        }

        @Override
        public void createGame(String playerOne, SwccgDeck deckOne, String playerTwo, SwccgDeck deckTwo, boolean allowSpectators) {
            final SwccgGameParticipant[] participants = new SwccgGameParticipant[2];
            participants[0] = new SwccgGameParticipant(playerOne, deckOne);
            participants[1] = new SwccgGameParticipant(playerTwo, deckTwo);
            createGameInternal(participants, allowSpectators);
        }

        private void createGameInternal(final SwccgGameParticipant[] participants, final boolean allowSpectators) {
            _hallDataAccessLock.writeLock().lock();
            try {
                if (_operational && !_shutdown) {
                    HallServer.this.createGame(null, null, new SwccgUuid().generateNewTableId(), participants,
                            new GameResultListener() {
                                @Override
                                public void gameFinished(String winnerPlayerId, String winReason, Map<String, String> loserPlayerIdsWithReasons, String winnerSide, String loserSide) {
                                    _tournament.reportGameFinished(winnerPlayerId, loserPlayerIdsWithReasons.keySet().iterator().next(), winnerSide, loserSide);
                                }

                                @Override
                                public void gameCancelled() {
                                    createGameInternal(participants, allowSpectators);
                                }
                            }, _formatLibrary.getFormat(_tournament.getFormat()), _tournament.getTournamentName(), null, allowSpectators, false, false, false, false, _decisionTimeoutSeconds, _timePerPlayerMinutes, false);
                }
            } finally {
                _hallDataAccessLock.writeLock().unlock();
            }
        }

        @Override
        public void broadcastMessage(String message) {
            try {
                _hallChat.sendMessage("TournamentSystem", message, true);
            } catch (PrivateInformationException exp) {
                // Ignore, sent as admin
            } catch (ChatCommandErrorException e) {
                // Ignore, no command
            }
        }
    }
}
