package com.gempukku.swccgo.ai.common;

import java.util.LinkedList;
import java.util.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages chat message generation and delivery for AI.
 *
 * Features:
 * - Message queueing with throttling
 * - Rate limiting to prevent spam (min 3 seconds between messages)
 * - Max 1 message per turn option
 *
 * Any AI can use this to add personality via chat messages.
 */
public class AiChatManager {

    private static final Logger LOG = LogManager.getLogger(AiChatManager.class);

    // Minimum milliseconds between chat messages (throttling)
    private static final long MIN_MESSAGE_INTERVAL_MS = 3000;

    // Maximum queued messages before we start dropping old ones
    private static final int MAX_QUEUE_SIZE = 10;

    // Message queue
    private final Queue<String> pendingMessages = new LinkedList<>();

    // Throttling
    private long lastMessageTime = 0;

    // Turn-based limiting
    private int lastMessageTurn = -1;
    private boolean limitOnePerTurn = true;

    // Game state
    private String gameId;
    private int currentTurn = 0;

    // Critical message flag - bypasses rate limiting
    private boolean hasCriticalMessage = false;

    /**
     * Create a new chat manager.
     */
    public AiChatManager() {
    }

    /**
     * Reset for a new game.
     * @param gameId the game ID
     */
    public void resetForGame(String gameId) {
        this.gameId = gameId;
        this.currentTurn = 0;
        this.lastMessageTurn = -1;
        this.pendingMessages.clear();
        LOG.debug("ChatManager reset for game {}", gameId);
    }

    /**
     * Update the current turn number.
     * @param turn the turn number
     */
    public void setCurrentTurn(int turn) {
        this.currentTurn = turn;
    }

    /**
     * Set whether to limit to one message per turn.
     * @param limit true to limit, false for no limit
     */
    public void setLimitOnePerTurn(boolean limit) {
        this.limitOnePerTurn = limit;
    }

    /**
     * Queue a message to be sent.
     * The message will be returned by getNextMessage() when appropriate.
     *
     * @param message the message to queue
     */
    public void queueMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        // Prevent queue from growing too large
        if (pendingMessages.size() >= MAX_QUEUE_SIZE) {
            pendingMessages.poll();  // Remove oldest
            LOG.debug("Chat queue full, dropped oldest message");
        }

        pendingMessages.offer(message);
        LOG.debug("Queued chat message: {}...", message.substring(0, Math.min(50, message.length())));
    }

    /**
     * Get the next message to send, respecting rate limiting.
     *
     * Call this after each AI decision to check if there's a message to send.
     * Critical messages (game end) bypass rate limiting.
     *
     * @return the message to send, or null if none available or rate limited
     */
    public String getNextMessage() {
        if (pendingMessages.isEmpty()) {
            return null;
        }

        long now = System.currentTimeMillis();

        // Critical messages bypass rate limiting (game end, etc.)
        if (!hasCriticalMessage) {
            // Check rate limit
            if (now - lastMessageTime < MIN_MESSAGE_INTERVAL_MS) {
                return null;
            }

            // Check one-per-turn limit
            if (limitOnePerTurn && lastMessageTurn == currentTurn) {
                return null;
            }
        }

        String message = pendingMessages.poll();
        if (message != null) {
            lastMessageTime = now;
            lastMessageTurn = currentTurn;
            hasCriticalMessage = false;  // Reset after delivering
            LOG.debug("Returning chat message: {}...", message.substring(0, Math.min(50, message.length())));
        }
        return message;
    }

    /**
     * Check if there are pending messages.
     * @return true if messages are queued
     */
    public boolean hasPendingMessages() {
        return !pendingMessages.isEmpty();
    }

    /**
     * Get the number of pending messages.
     * @return count of queued messages
     */
    public int getPendingMessageCount() {
        return pendingMessages.size();
    }

    /**
     * Clear all pending messages.
     */
    public void clearPendingMessages() {
        pendingMessages.clear();
    }

    // =========================================================================
    // Convenience methods for common message types
    // =========================================================================

    /**
     * Queue a welcome message for game start.
     * @param message the welcome message
     */
    public void queueWelcome(String message) {
        queueMessage(message);
    }

    /**
     * Queue a turn commentary message.
     * @param message the turn message
     */
    public void queueTurnMessage(String message) {
        queueMessage(message);
    }

    /**
     * Queue a battle commentary message.
     * @param message the battle message
     */
    public void queueBattleMessage(String message) {
        queueMessage(message);
    }

    /**
     * Queue a game end message.
     * This bypasses ALL rate limiting since the game is ending.
     *
     * @param message the end game message
     */
    public void queueGameEndMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        // Game end messages are important, put at front of queue
        LinkedList<String> temp = new LinkedList<>();
        temp.add(message);
        temp.addAll(pendingMessages);
        pendingMessages.clear();
        pendingMessages.addAll(temp);
        // Mark as critical to bypass ALL rate limiting
        hasCriticalMessage = true;
        lastMessageTurn = -1;
        LOG.info("Queued critical game end message - will bypass rate limiting");
    }

    /**
     * Force send a message immediately, bypassing rate limits.
     * Use sparingly - only for critical messages like game end.
     *
     * @return the message to send immediately, or null if none queued
     */
    public String getMessageImmediate() {
        if (pendingMessages.isEmpty()) {
            return null;
        }
        String message = pendingMessages.poll();
        if (message != null) {
            lastMessageTime = System.currentTimeMillis();
            lastMessageTurn = currentTurn;
        }
        return message;
    }
}
