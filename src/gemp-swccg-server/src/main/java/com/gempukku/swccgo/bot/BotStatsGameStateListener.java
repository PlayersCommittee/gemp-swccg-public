package com.gempukku.swccgo.bot;

import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.db.BotStatsDAO;
import com.gempukku.swccgo.db.vo.BotPlayerStats;
import com.gempukku.swccgo.db.vo.LeaderboardEntry;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.timing.GameStats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * GameStateListener that monitors game events for real-time achievement checking
 * and battle damage commentary.
 *
 * This listener receives callbacks as the game progresses and checks for achievements
 * in real-time, providing immediate feedback to players when they unlock achievements.
 *
 * Events monitored:
 * - cardCreated: When a card enters play (triggers single card and combo achievements)
 * - cardMoved: When a card moves (triggers combo achievements at new location)
 * - cardsRemoved: When cards leave play (triggers card killed achievements)
 * - startBattle/finishBattle: Battle tracking (triggers battle-related achievements)
 * - sendMessage: Parses battle damage from game messages
 * - setCurrentPhase: Phase changes (triggers periodic checks like hand size)
 */
public class BotStatsGameStateListener implements GameStateListener {

    private static final Logger LOG = LogManager.getLogger(BotStatsGameStateListener.class);

    private final AchievementChecker _achievementChecker;
    private final String _botPlayerId;
    private final String _humanPlayerId;
    private final Consumer<String> _chatMessageSender;
    private GameState _gameState;
    private boolean _inBattle = false;

    // Battle tracking state (parsed from messages)
    private String _pendingBattleWinner = null;  // "dark" or "light"
    private int _pendingBattleDamage = 0;
    private String _humanPlayerSide = null;      // "dark" or "light"

    // DAO for record checking (set via setter)
    private BotStatsDAO _botStatsDAO = null;
    private int _humanPlayerDbId = -1;

    /**
     * Create a new game state listener for achievement tracking.
     *
     * @param achievementChecker the shared achievement checker
     * @param botPlayerId the bot's player ID (e.g., "~Rando_Cal")
     * @param humanPlayerId the human player's game ID
     * @param chatMessageSender callback to send chat messages
     */
    public BotStatsGameStateListener(AchievementChecker achievementChecker,
                                      String botPlayerId,
                                      String humanPlayerId,
                                      Consumer<String> chatMessageSender) {
        _achievementChecker = achievementChecker;
        _botPlayerId = botPlayerId;
        _humanPlayerId = humanPlayerId;
        _chatMessageSender = chatMessageSender;
        LOG.debug("BotStatsGameStateListener created for bot {} vs human {}", botPlayerId, humanPlayerId);
    }

    /**
     * Set the game state reference for querying game data.
     */
    public void setGameState(GameState gameState) {
        _gameState = gameState;
    }

    /**
     * Set the human player's side (dark/light) for battle winner detection.
     *
     * @param side "dark" or "light"
     */
    public void setHumanPlayerSide(String side) {
        _humanPlayerSide = side;
        LOG.debug("Human player side set to: {}", side);
    }

    /**
     * Set the DAO and player ID for record checking.
     *
     * @param botStatsDAO the bot stats DAO
     * @param humanPlayerDbId the human player's database ID
     */
    public void setBotStatsDAO(BotStatsDAO botStatsDAO, int humanPlayerDbId) {
        _botStatsDAO = botStatsDAO;
        _humanPlayerDbId = humanPlayerDbId;
    }

    @Override
    public String getPlayerId() {
        // Return the bot's player ID so we receive events for the bot's perspective
        return _botPlayerId;
    }

    @Override
    public void cardCreated(PhysicalCard card, GameState gameState, boolean restoreSnapshot) {
        if (restoreSnapshot) {
            // Don't trigger achievements on snapshot restore
            return;
        }

        // Only check cards that are actually in play
        if (card.getZone() == null || !card.getZone().isInPlay()) {
            return;
        }

        _gameState = gameState;

        try {
            List<String> messages = _achievementChecker.onCardEntersPlay(card, gameState);
            sendAchievementMessages(messages);
        } catch (Exception e) {
            LOG.error("Error checking achievements for card created: {}", card.getTitle(), e);
        }
    }

    @Override
    public void cardMoved(PhysicalCard card, GameState gameState) {
        _gameState = gameState;

        try {
            List<String> messages = _achievementChecker.onCardMoved(card, gameState);
            sendAchievementMessages(messages);
        } catch (Exception e) {
            LOG.error("Error checking achievements for card moved: {}", card.getTitle(), e);
        }
    }

    @Override
    public void cardsRemoved(String playerPerforming, Collection<PhysicalCard> cards) {
        if (_gameState == null) {
            return;
        }

        try {
            List<String> messages = _achievementChecker.onCardsRemoved(cards, _gameState);
            sendAchievementMessages(messages);
        } catch (Exception e) {
            LOG.error("Error checking achievements for cards removed", e);
        }
    }

    @Override
    public void startBattle(PhysicalCard location, Collection<PhysicalCard> cards) {
        _inBattle = true;
        // Reset damage tracking for new battle - prevents stale values from previous battles
        _pendingBattleDamage = 0;
        _pendingBattleWinner = null;
        _achievementChecker.onBattleStarted();
    }

    @Override
    public void finishBattle() {
        if (!_inBattle) {
            return;
        }
        _inBattle = false;

        LOG.debug("Battle finished - winner: {}, damage: {}, humanSide: {}",
                  _pendingBattleWinner, _pendingBattleDamage, _humanPlayerSide);

        // Determine if human won the battle
        boolean humanWon = _pendingBattleWinner != null &&
                          _humanPlayerSide != null &&
                          _pendingBattleWinner.equals(_humanPlayerSide);

        // Update achievements (Blitzkrieg tracks human battle wins)
        List<String> achMsgs = _achievementChecker.onBattleFinished(humanWon);
        sendAchievementMessages(achMsgs);

        // Send damage commentary (only if damage was dealt)
        if (_pendingBattleDamage > 0) {
            String commentary = generateBattleCommentary();
            if (commentary != null) {
                _chatMessageSender.accept(commentary);
            }
        }

        // Reset for next battle
        _pendingBattleWinner = null;
        _pendingBattleDamage = 0;
    }

    /**
     * Generate battle damage commentary, checking for records if DAO is available.
     */
    private String generateBattleCommentary() {
        if (_pendingBattleDamage <= 0) {
            return null;
        }

        boolean isGlobalRecord = false;
        boolean isPersonalRecord = false;
        String previousRecordHolder = null;
        int previousPersonalBest = 0;
        String currentPlayer = _humanPlayerId;

        // Check for records if we have DAO access
        if (_botStatsDAO != null && _humanPlayerDbId >= 0) {
            try {
                // Check global record
                LeaderboardEntry globalRecord = _botStatsDAO.getBestDamageRecord();
                if (globalRecord != null) {
                    if (_pendingBattleDamage > globalRecord.getValue()) {
                        isGlobalRecord = true;
                        previousRecordHolder = globalRecord.getPlayerName();
                    }
                } else {
                    // No global record exists yet
                    isGlobalRecord = true;
                }

                // Check personal record (if not global record)
                if (!isGlobalRecord) {
                    BotPlayerStats playerStats = _botStatsDAO.getPlayerStats(_humanPlayerDbId);
                    if (playerStats != null) {
                        previousPersonalBest = playerStats.getBestDamage();
                        if (_pendingBattleDamage > previousPersonalBest) {
                            isPersonalRecord = true;
                        }
                    } else {
                        // First game for this player
                        isPersonalRecord = true;
                    }
                }
            } catch (Exception e) {
                LOG.debug("Error checking damage records: {}", e.getMessage());
            }
        }

        return BattleCommentary.getDamageCommentary(
            _pendingBattleDamage,
            isGlobalRecord,
            isPersonalRecord,
            previousRecordHolder,
            previousPersonalBest,
            currentPlayer
        );
    }

    @Override
    public void setCurrentPhase(String phase) {
        if (_gameState == null) {
            return;
        }

        // Check hand size at certain phases (e.g., after draw phase)
        if ("Draw".equals(phase) || "Move".equals(phase)) {
            try {
                // Get human player's hand size
                int handSize = _gameState.getHand(_humanPlayerId).size();
                List<String> messages = _achievementChecker.onHandSizeCheck(handSize);
                sendAchievementMessages(messages);

                // Check location control
                int locationsControlled = countLocationsControlled(_gameState, _humanPlayerId);
                messages = _achievementChecker.onLocationControlCheck(locationsControlled);
                sendAchievementMessages(messages);
            } catch (Exception e) {
                LOG.debug("Error checking phase-based achievements: {}", e.getMessage());
            }
        }
    }

    /**
     * Count the number of locations the player controls.
     */
    private int countLocationsControlled(GameState gameState, String playerId) {
        int count = 0;
        for (PhysicalCard card : gameState.getAllPermanentCards()) {
            if (card.getBlueprint().getCardCategory() == com.gempukku.swccgo.common.CardCategory.LOCATION) {
                // A simple heuristic: player controls a location if they have cards there and opponent doesn't
                // This is a simplification - actual control rules are more complex
                boolean hasOwnCards = false;
                boolean hasOpponentCards = false;

                for (PhysicalCard atLoc : gameState.getAllPermanentCards()) {
                    if (atLoc.getZone() != null && atLoc.getZone().isInPlay()) {
                        PhysicalCard location = atLoc.getAtLocation();
                        if (location != null && location.getCardId() == card.getCardId()) {
                            if (playerId.equals(atLoc.getOwner())) {
                                hasOwnCards = true;
                            } else {
                                hasOpponentCards = true;
                            }
                        }
                    }
                }

                if (hasOwnCards && !hasOpponentCards) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Send achievement messages to the game chat.
     */
    private void sendAchievementMessages(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (String message : messages) {
            try {
                _chatMessageSender.accept(message);
            } catch (Exception e) {
                LOG.debug("Could not send achievement message: {}", e.getMessage());
            }
        }
    }

    // =========================================================================
    // Other GameStateListener methods - not used for achievement tracking
    // =========================================================================

    @Override
    public void cardReplaced(PhysicalCard card, GameState gameState) {
        // Not used for achievements
    }

    @Override
    public void locationsRemoved(Collection<Integer> locationIndexes) {
        // Not used for achievements
    }

    @Override
    public void cardRotated(PhysicalCard card, GameState gameState) {
        // Not used for achievements
    }

    @Override
    public void cardFlipped(PhysicalCard card, GameState gameState) {
        // Not used for achievements
    }

    @Override
    public void cardTurnedOver(PhysicalCard card, GameState gameState) {
        // Not used for achievements
    }

    @Override
    public void setPlayerOrder(List<String> playerIds) {
        // Not used for achievements
    }

    @Override
    public void addToBattle(PhysicalCard card, GameState gameState) {
        // Not used for achievements
    }

    @Override
    public void removeFromBattle(PhysicalCard card, GameState gameState) {
        // Not used for achievements
    }

    @Override
    public void startAttack(PhysicalCard location, String playerAttacking, String playerDefending,
                           Collection<PhysicalCard> attackingCards, Collection<PhysicalCard> defendingCards) {
        // Not used for achievements
    }

    @Override
    public void finishAttack() {
        // Not used for achievements
    }

    @Override
    public void startDuel(PhysicalCard location, Collection<PhysicalCard> cards) {
        // Not used for achievements
    }

    @Override
    public void finishDuel() {
        // Not used for achievements
    }

    @Override
    public void startLightsaberCombat(PhysicalCard location, Collection<PhysicalCard> cards) {
        // Not used for achievements
    }

    @Override
    public void finishLightsaberCombat() {
        // Not used for achievements
    }

    @Override
    public void startSabacc() {
        // Not used for achievements
    }

    @Override
    public void revealSabaccHands() {
        // Not used for achievements
    }

    @Override
    public void finishSabacc() {
        // Not used for achievements
    }

    @Override
    public void setCurrentPlayerId(String playerId) {
        // Not used for achievements
    }

    @Override
    public void sendMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        // Parse battle winner
        if (message.contains("Dark side wins battle")) {
            _pendingBattleWinner = "dark";
            LOG.debug("Battle winner detected: dark");
        } else if (message.contains("Light side wins battle")) {
            _pendingBattleWinner = "light";
            LOG.debug("Battle winner detected: light");
        }

        // Parse battle damage from messages like "10 battle damage remaining"
        String msgLower = message.toLowerCase();
        if (msgLower.contains(" battle")) {
            parseBattleDamage(message);
        }
    }

    /**
     * Parse battle damage from message text.
     *
     * Looks for pattern: "{number} battle" where number precedes the word "battle".
     * Examples:
     * - "10 battle damage remaining to satisfy" -> 10
     * - "5.5 battle damage" -> 5
     *
     * Tracks the highest damage seen during a battle (multiple messages may be sent
     * as destiny draws occur and damage gets satisfied).
     */
    private void parseBattleDamage(String message) {
        String[] tokens = message.split("\\s+");
        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i].toLowerCase();
            // Look for "battle" word (might be "battle," or just "battle")
            if (token.startsWith("battle")) {
                String previous = tokens[i - 1];
                try {
                    // Parse as float first (damage can be fractional like "2.5")
                    float damage = Float.parseFloat(previous);
                    int damageInt = (int) damage;
                    if (damageInt > 0 && damageInt > _pendingBattleDamage) {
                        LOG.debug("Battle damage updated: {} -> {}", _pendingBattleDamage, damageInt);
                        _pendingBattleDamage = damageInt;
                    }
                } catch (NumberFormatException ignored) {
                    // Previous token wasn't a number, continue searching
                }
            }
        }
    }

    @Override
    public void sendGameStats(GameStats gameStats) {
        // Not used for achievements
    }

    @Override
    public void cardAffectedByCard(String playerPerforming, PhysicalCard card,
                                   Collection<PhysicalCard> affectedCard, GameState gameState) {
        // Not used for achievements
    }

    @Override
    public void interruptPlayed(PhysicalCard card, GameState gameState) {
        // Could be used for interrupt-based achievements in the future
    }

    @Override
    public void destinyDrawn(PhysicalCard card, GameState gameState, String destinyText) {
        // Not used for achievements
    }

    @Override
    public void cardActivated(String playerPerforming, PhysicalCard card, GameState gameState) {
        // Not used for achievements
    }

    @Override
    public void decisionRequired(String playerId, AwaitingDecision awaitingDecision) {
        // Not used for achievements
    }
}
