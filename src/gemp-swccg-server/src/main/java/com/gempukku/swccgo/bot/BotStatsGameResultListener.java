package com.gempukku.swccgo.bot;

import com.gempukku.swccgo.PrivateInformationException;
import com.gempukku.swccgo.chat.ChatCommandErrorException;
import com.gempukku.swccgo.chat.ChatRoomMediator;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.db.BotStatsDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.db.vo.BotPlayerStats;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.game.SwccgGameParticipant;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.GameResultListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * GameResultListener that records bot game statistics and checks achievements.
 *
 * This listener is added to games where one player is a bot (AI).
 * It creates a BotStatsGameStateListener for real-time achievement checking
 * and handles game-end statistics and achievements.
 *
 * Real-time achievements are checked as cards enter play, move, or are removed.
 * Game-end achievements are checked when the game finishes (route score, meta achievements).
 */
public class BotStatsGameResultListener implements GameResultListener {

    private static final Logger LOG = LogManager.getLogger(BotStatsGameResultListener.class);

    private final BotStatsDAO _botStatsDAO;
    private final PlayerDAO _playerDAO;
    private final SwccgGameMediator _gameMediator;
    private final String _botPlayerId;
    private final long _gameStartTime;
    private final AchievementChecker _achievementChecker;
    private final BotStatsGameStateListener _gameStateListener;
    private final String _humanPlayerId;
    private final int _humanPlayerDbId;
    private boolean _initialized = false;
    private ChatRoomMediator _chatRoom = null;

    /**
     * Create a new bot stats listener.
     *
     * @param botStatsDAO the bot stats DAO
     * @param playerDAO the player DAO (for looking up player IDs)
     * @param gameMediator the game mediator (for accessing game state)
     * @param botPlayerId the bot's player ID (e.g., "~Rando_Cal")
     */
    public BotStatsGameResultListener(BotStatsDAO botStatsDAO, PlayerDAO playerDAO,
                                      SwccgGameMediator gameMediator, String botPlayerId) {
        _botStatsDAO = botStatsDAO;
        _playerDAO = playerDAO;
        _gameMediator = gameMediator;
        _botPlayerId = botPlayerId;
        _gameStartTime = System.currentTimeMillis();
        _achievementChecker = new AchievementChecker(botStatsDAO);

        // Find the human player from the game participants
        String humanPlayerId = null;
        int humanPlayerDbId = -1;
        for (SwccgGameParticipant participant : gameMediator.getPlayersPlaying()) {
            String participantId = participant.getPlayerId();
            if (!botPlayerId.equals(participantId)) {
                humanPlayerId = participantId;
                // Look up the player's database ID
                Player player = playerDAO.getPlayer(participantId);
                if (player != null) {
                    humanPlayerDbId = player.getId();
                    // Ensure the player has a stats record
                    botStatsDAO.createPlayerStats(humanPlayerDbId);
                }
                break;
            }
        }
        _humanPlayerId = humanPlayerId;
        _humanPlayerDbId = humanPlayerDbId;

        // Initialize the achievement checker with player info for real-time checking
        if (_humanPlayerId != null && _humanPlayerDbId >= 0) {
            _achievementChecker.initializeForGame(_humanPlayerDbId, _humanPlayerId, _botPlayerId);
            _initialized = true;
            LOG.info("Bot stats listener initialized: bot={}, human={} (ID={})",
                     botPlayerId, humanPlayerId, humanPlayerDbId);
        } else {
            LOG.warn("Could not initialize bot stats listener: human player not found");
        }

        // Create the game state listener for real-time achievement checking
        _gameStateListener = new BotStatsGameStateListener(
            _achievementChecker,
            _botPlayerId,
            _humanPlayerId,
            this::sendChatMessage  // Pass the chat message sender as callback
        );

        // Provide DAO access for battle damage record tracking
        _gameStateListener.setBotStatsDAO(botStatsDAO, humanPlayerDbId);

        // Detect human player's side (dark/light) for battle winner tracking
        // This needs to be done after game state is available, so we do it lazily
        initializeHumanPlayerSide();
    }

    /**
     * Initialize the human player's side (dark/light) from the game state.
     * Called at construction time but may be called again if game state wasn't ready.
     */
    private void initializeHumanPlayerSide() {
        try {
            GameState gameState = _gameMediator.getGameState();
            if (gameState != null && _humanPlayerId != null) {
                if (_humanPlayerId.equals(gameState.getDarkPlayer())) {
                    _gameStateListener.setHumanPlayerSide("dark");
                    LOG.debug("Human player {} is Dark side", _humanPlayerId);
                } else if (_humanPlayerId.equals(gameState.getLightPlayer())) {
                    _gameStateListener.setHumanPlayerSide("light");
                    LOG.debug("Human player {} is Light side", _humanPlayerId);
                }
            }
        } catch (Exception e) {
            LOG.debug("Could not determine human player side: {}", e.getMessage());
        }
    }

    /**
     * Get the game state listener for real-time achievement checking.
     * This should be registered with the game mediator via addGameStateListener.
     *
     * @return the game state listener
     */
    public BotStatsGameStateListener getGameStateListener() {
        // Try to initialize human player side if not done yet (game state may now be ready)
        initializeHumanPlayerSide();
        return _gameStateListener;
    }

    /**
     * Check if the listener was properly initialized.
     */
    public boolean isInitialized() {
        return _initialized;
    }

    /**
     * Set the chat room for sending bot messages as player chat.
     * When set, bot messages will appear as player chat messages instead of system messages.
     *
     * @param chatRoom the chat room mediator for this game
     */
    public void setChatRoom(ChatRoomMediator chatRoom) {
        _chatRoom = chatRoom;
        LOG.info("Chat room set for bot messages: {}", chatRoom != null ? "available" : "null");
    }

    @Override
    public void gameFinished(String winnerPlayerId, String winReason,
                            Map<String, String> loserPlayerIdsWithReasons,
                            String winnerSide, String loserSide) {
        try {
            // Use the human player we identified at startup
            if (_humanPlayerId == null || _humanPlayerDbId < 0) {
                LOG.warn("Human player was not identified at startup, cannot record stats");
                return;
            }

            // Get game state for calculating stats
            GameState gameState = _gameMediator.getGameState();
            if (gameState == null) {
                LOG.warn("Game state is null, cannot record bot stats");
                return;
            }

            // Calculate game metrics
            boolean humanWon = _humanPlayerId.equals(winnerPlayerId);
            int gameTimeSeconds = (int) ((System.currentTimeMillis() - _gameStartTime) / 1000);
            int turnCount = getTurnCount(gameState);

            // Calculate life force (Reserve + Force Pile + Used Pile)
            int humanLifeForce = calculateLifeForce(gameState, _humanPlayerId);
            int botLifeForce = calculateLifeForce(gameState, _botPlayerId);

            // Calculate route score (only meaningful for wins)
            // Formula: (human_life_force - bot_life_force) - turn_count
            int routeScore = humanWon ? (humanLifeForce - botLifeForce) - turnCount : 0;
            routeScore = Math.max(0, routeScore); // Don't allow negative route scores

            // Calculate damage dealt to bot
            int damage = calculateDamageDealt(gameState, _botPlayerId);

            // Force remaining is the human's force pile size
            int forceRemaining = gameState.getForcePile(_humanPlayerId).size();

            LOG.info("Recording bot game result: player={}, won={}, routeScore={}, damage={}, " +
                     "forceRemaining={}, timeSeconds={}, turns={}",
                     _humanPlayerId, humanWon, routeScore, damage, forceRemaining, gameTimeSeconds, turnCount);

            // Record the game result
            _botStatsDAO.recordGameResult(_humanPlayerDbId, humanWon, routeScore, damage,
                                          forceRemaining, gameTimeSeconds);

            // Check for battle-related achievements (Pacifist: win without battles)
            if (humanWon && _achievementChecker.getBattlesInitiated() == 0) {
                String msg = awardIfNew(Achievement.PACIFIST);
                if (msg != null) {
                    sendChatMessage(msg);
                }
            }

            // Check for game-end achievements
            BotPlayerStats stats = _botStatsDAO.getPlayerStats(_humanPlayerDbId);
            if (stats != null) {
                var achievementMessages = _achievementChecker.checkGameEnd(
                    _humanPlayerDbId, humanWon, routeScore, turnCount, forceRemaining, damage,
                    stats.getGamesPlayed(), stats.getTotalAstScore(), stats.countAchievements()
                );

                // Send achievement messages to game chat
                for (String message : achievementMessages) {
                    sendChatMessage(message);
                }

                // Check battle-related achievements
                var battleMessages = _achievementChecker.checkBattleAchievements(_humanPlayerDbId, humanWon);
                for (String message : battleMessages) {
                    sendChatMessage(message);
                }
            }

            // Send game-end stats message
            sendGameEndMessage(_humanPlayerId, humanWon, routeScore, stats);

        } catch (Exception e) {
            LOG.error("Error recording bot game stats", e);
        }
    }

    /**
     * Award an achievement if not already unlocked, returning the message or null.
     */
    private String awardIfNew(Achievement ach) {
        if (_botStatsDAO.hasAchievement(_humanPlayerDbId, ach.getBitPosition())) {
            return null;
        }
        boolean newlyUnlocked = _botStatsDAO.unlockAchievement(_humanPlayerDbId, ach.getBitPosition());
        if (newlyUnlocked) {
            int total = _botStatsDAO.getAchievementCount(_humanPlayerDbId);
            return ach.getQuote() + " (" + total + "/" + Achievement.TOTAL_ACHIEVEMENTS + ")";
        }
        return null;
    }

    @Override
    public void gameCancelled() {
        LOG.debug("Bot game cancelled, no stats recorded");
    }

    /**
     * Calculate a player's life force (Reserve Deck + Force Pile + Used Pile).
     */
    private int calculateLifeForce(GameState gameState, String playerId) {
        int reserveDeck = gameState.getReserveDeckSize(playerId);
        int forcePile = gameState.getForcePile(playerId).size();
        int usedPile = gameState.getUsedPile(playerId).size();
        return reserveDeck + forcePile + usedPile;
    }

    /**
     * Calculate damage dealt to a player (their lost pile size).
     */
    private int calculateDamageDealt(GameState gameState, String playerId) {
        return gameState.getLostPile(playerId).size();
    }

    /**
     * Get the current turn count.
     */
    private int getTurnCount(GameState gameState) {
        // Get the higher of the two players' turn numbers
        int darkTurn = gameState.getPlayersLatestTurnNumber(gameState.getDarkPlayer());
        int lightTurn = gameState.getPlayersLatestTurnNumber(gameState.getLightPlayer());
        return Math.max(darkTurn, lightTurn);
    }

    /**
     * Send a chat message from the bot to the game.
     * This version is used as a Consumer<String> callback for the state listener.
     *
     * If a chat room is set, the message is sent as a player chat message.
     * Otherwise, it falls back to game state messages (which appear as system messages).
     *
     * @param message the message to send (bot name is prepended automatically for game state)
     */
    private void sendChatMessage(String message) {
        try {
            // Prefer chat room for proper player message styling
            if (_chatRoom != null) {
                try {
                    LOG.info("Sending bot message via chat room: {}", message);
                    _chatRoom.sendMessage(_botPlayerId, message, true);
                    LOG.info("Bot message sent successfully via chat room");
                    return;
                } catch (PrivateInformationException | ChatCommandErrorException e) {
                    LOG.warn("Could not send via chat room, falling back to game state: {}", e.getMessage());
                }
            } else {
                LOG.info("Chat room is null, using game state fallback for message: {}", message);
            }

            // Fallback to game state (appears as system message)
            GameState gameState = _gameMediator.getGameState();
            if (gameState != null) {
                LOG.info("Sending bot message via game state (will appear as system message)");
                gameState.sendMessage(_botPlayerId + ": " + message);
            }
        } catch (Exception e) {
            LOG.warn("Could not send chat message: {}", e.getMessage(), e);
        }
    }

    /**
     * Send the game-end stats summary message.
     */
    private void sendGameEndMessage(String humanPlayerId, boolean humanWon, int routeScore,
                                   BotPlayerStats stats) {
        StringBuilder sb = new StringBuilder();

        if (humanWon) {
            sb.append("Route score: ").append(routeScore);
            if (stats != null && routeScore > 0) {
                if (routeScore >= 50) {
                    sb.append(" - EXCELLENT!");
                } else if (routeScore >= 30) {
                    sb.append(" - sellable route!");
                } else if (routeScore >= 10) {
                    sb.append(" - decent route");
                }
            }
        } else {
            sb.append("Better luck next time!");
        }

        if (stats != null) {
            sb.append(" | Record: ").append(stats.getWins()).append("-").append(stats.getLosses());
            if (stats.getTotalAstScore() > 0) {
                sb.append(" | Total score: ").append(stats.getTotalAstScore());
            }
        }

        sendChatMessage(sb.toString());
    }

    /**
     * Get the achievement checker for this game.
     * Can be used to check board-state achievements during the game.
     */
    public AchievementChecker getAchievementChecker() {
        return _achievementChecker;
    }

    /**
     * Check board-state achievements by examining all cards that were in play.
     * This is a fallback safety check at game end for any achievements that might
     * have been missed during real-time checking.
     * This includes cards currently in play AND cards in the lost pile (they were in play).
     */
    private List<String> checkBoardStateAchievements(GameState gameState, int playerId,
                                                      String humanPlayerId) {
        // Build maps of all cards that were in play during the game
        Map<String, String> cardsOnBoard = new HashMap<>();  // title -> type
        Set<String> humanCards = new HashSet<>();
        Set<String> botCards = new HashSet<>();
        Map<String, List<String>> cardsByLocation = new HashMap<>();

        // Collect cards currently in play
        for (PhysicalCard card : gameState.getAllPermanentCards()) {
            if (card.getZone() != null && card.getZone().isInPlay()) {
                addCardToMaps(card, cardsOnBoard, humanCards, botCards, humanPlayerId);
            }
        }

        // Also check lost piles - these cards WERE in play
        for (PhysicalCard card : gameState.getLostPile(humanPlayerId)) {
            addCardToMaps(card, cardsOnBoard, humanCards, botCards, humanPlayerId);
        }
        for (PhysicalCard card : gameState.getLostPile(_botPlayerId)) {
            addCardToMaps(card, cardsOnBoard, humanCards, botCards, humanPlayerId);
        }

        // Build cards by location (only for cards currently in play)
        for (PhysicalCard card : gameState.getAllPermanentCards()) {
            if (card.getZone() != null && card.getZone().isInPlay()) {
                PhysicalCard atLocation = card.getAtLocation();
                if (atLocation != null) {
                    String locationName = atLocation.getTitle();
                    cardsByLocation.computeIfAbsent(locationName, k -> new ArrayList<>())
                                  .add(card.getTitle());
                }
            }
        }

        LOG.debug("Checking board achievements: {} cards total, {} human cards, {} bot cards",
                  cardsOnBoard.size(), humanCards.size(), botCards.size());

        // Check achievements
        return _achievementChecker.checkBoardState(
            playerId, cardsOnBoard, botCards, humanCards, cardsByLocation);
    }

    /**
     * Add a card to the tracking maps.
     */
    private void addCardToMaps(PhysicalCard card, Map<String, String> cardsOnBoard,
                               Set<String> humanCards, Set<String> botCards,
                               String humanPlayerId) {
        String title = card.getTitle();
        if (title == null || title.isEmpty()) {
            return;
        }

        String cardType = card.getBlueprint().getCardCategory().name();
        cardsOnBoard.put(title.toLowerCase(), cardType);

        if (humanPlayerId.equals(card.getOwner())) {
            humanCards.add(title.toLowerCase());
        } else {
            botCards.add(title.toLowerCase());
        }
    }
}
