package com.gempukku.swccgo.ai.models.rando;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Centralized logging for Rando Cal AI.
 *
 * All Rando AI classes should use this logger instead of creating their own.
 * This allows the Rando AI logging to be configured separately from the rest
 * of GEMP via log4j2.xml configuration.
 *
 * Logger hierarchy:
 * - com.gempukku.swccgo.ai.models.rando (base - defaults to WARN for production)
 *   - com.gempukku.swccgo.ai.models.rando.decision (decision processing)
 *   - com.gempukku.swccgo.ai.models.rando.evaluator (evaluator scoring)
 *   - com.gempukku.swccgo.ai.models.rando.strategy (strategy planning)
 *   - com.gempukku.swccgo.ai.models.rando.safety (loop detection, critical safety)
 *
 * To enable verbose Rando logging, add to log4j2.xml:
 * <Logger name="com.gempukku.swccgo.ai.models.rando" level="DEBUG" additivity="false">
 *     <AppenderRef ref="Console"/>
 * </Logger>
 *
 * To see only safety/critical issues:
 * <Logger name="com.gempukku.swccgo.ai.models.rando" level="WARN" additivity="false">
 *     <AppenderRef ref="Console"/>
 * </Logger>
 */
public final class RandoLogger {

    // Base logger name - all Rando loggers are under this hierarchy
    public static final String BASE_LOGGER_NAME = "com.gempukku.swccgo.ai.models.rando";

    // Sub-category logger names
    public static final String DECISION_LOGGER = BASE_LOGGER_NAME + ".decision";
    public static final String EVALUATOR_LOGGER = BASE_LOGGER_NAME + ".evaluator";
    public static final String STRATEGY_LOGGER = BASE_LOGGER_NAME + ".strategy";
    public static final String SAFETY_LOGGER = BASE_LOGGER_NAME + ".safety";

    // Cached loggers
    private static final Logger BASE = LogManager.getLogger(BASE_LOGGER_NAME);
    private static final Logger DECISION = LogManager.getLogger(DECISION_LOGGER);
    private static final Logger EVALUATOR = LogManager.getLogger(EVALUATOR_LOGGER);
    private static final Logger STRATEGY = LogManager.getLogger(STRATEGY_LOGGER);
    private static final Logger SAFETY = LogManager.getLogger(SAFETY_LOGGER);

    private RandoLogger() {
        // Utility class - no instantiation
    }

    // =========================================================================
    // Logger Access
    // =========================================================================

    /**
     * Get the base Rando logger.
     */
    public static Logger getLogger() {
        return BASE;
    }

    /**
     * Get the decision processing logger.
     */
    public static Logger getDecisionLogger() {
        return DECISION;
    }

    /**
     * Get the evaluator scoring logger.
     */
    public static Logger getEvaluatorLogger() {
        return EVALUATOR;
    }

    /**
     * Get the strategy planning logger.
     */
    public static Logger getStrategyLogger() {
        return STRATEGY;
    }

    /**
     * Get the safety/loop detection logger.
     * This logger defaults to higher visibility since it tracks critical issues.
     */
    public static Logger getSafetyLogger() {
        return SAFETY;
    }

    // =========================================================================
    // Convenience Methods
    // =========================================================================

    /**
     * Log a debug message to the base logger.
     */
    public static void debug(String message, Object... args) {
        BASE.debug(message, args);
    }

    /**
     * Log an info message to the base logger.
     */
    public static void info(String message, Object... args) {
        BASE.info(message, args);
    }

    /**
     * Log a warning message to the base logger.
     */
    public static void warn(String message, Object... args) {
        BASE.warn(message, args);
    }

    /**
     * Log an error message to the base logger.
     */
    public static void error(String message, Object... args) {
        BASE.error(message, args);
    }

    /**
     * Log a critical safety issue - these should always be logged.
     */
    public static void critical(String message, Object... args) {
        SAFETY.error("🚨 [CRITICAL] " + message, args);
    }

    /**
     * Log a loop detection event.
     */
    public static void loopDetected(String message, Object... args) {
        SAFETY.warn("🔄 [LOOP] " + message, args);
    }

    /**
     * Log an evaluator decision with score.
     */
    public static void evaluator(String evaluatorName, String action, float score, String reason) {
        EVALUATOR.debug("🎯 [{}] {} -> score={} ({})", evaluatorName, action, score, reason);
    }

    /**
     * Log a strategy decision.
     */
    public static void strategy(String component, String message, Object... args) {
        STRATEGY.debug("📋 [{}] " + message, prependArg(component, args));
    }

    /**
     * Log a decision being processed.
     */
    public static void decision(String decisionType, String text) {
        DECISION.debug("🤔 [{}] Processing: {}", decisionType, truncate(text, 80));
    }

    /**
     * Log the result of a decision.
     */
    public static void decisionResult(String decisionType, String result, String reason) {
        DECISION.info("✅ [{}] Result: {} ({})", decisionType, result, reason);
    }

    // =========================================================================
    // Emoji-Prefixed Logging (Python style)
    // =========================================================================

    /**
     * Log a successful action.
     */
    public static void success(String message, Object... args) {
        BASE.info("✅ " + message, args);
    }

    /**
     * Log a failed or bad action.
     */
    public static void failure(String message, Object... args) {
        BASE.warn("❌ " + message, args);
    }

    /**
     * Log a warning about a potentially problematic situation.
     */
    public static void caution(String message, Object... args) {
        BASE.warn("⚠️ " + message, args);
    }

    /**
     * Log a blocked action (loop prevention).
     */
    public static void blocked(String message, Object... args) {
        SAFETY.warn("🚫 " + message, args);
    }

    /**
     * Log a target selection or scoring event.
     */
    public static void target(String message, Object... args) {
        EVALUATOR.info("🎯 " + message, args);
    }

    /**
     * Log a search or analysis event.
     */
    public static void search(String message, Object... args) {
        BASE.debug("🔍 " + message, args);
    }

    /**
     * Log a protection or defense event.
     */
    public static void shield(String message, Object... args) {
        STRATEGY.info("🛡️ " + message, args);
    }

    /**
     * Log a battle or combat event.
     */
    public static void battle(String message, Object... args) {
        BASE.info("⚔️ " + message, args);
    }

    /**
     * Log a deploy event.
     */
    public static void deploy(String message, Object... args) {
        BASE.info("📍 " + message, args);
    }

    /**
     * Log a force drain event.
     */
    public static void drain(String message, Object... args) {
        BASE.info("💧 " + message, args);
    }

    /**
     * Log a critical/emergency event.
     */
    public static void emergency(String message, Object... args) {
        SAFETY.error("🔥 [EMERGENCY] " + message, args);
    }

    /**
     * Log a barrier/prevent event.
     */
    public static void barrier(String message, Object... args) {
        STRATEGY.info("🚧 " + message, args);
    }

    /**
     * Log a note/memo for tracking.
     */
    public static void note(String message, Object... args) {
        BASE.debug("📝 " + message, args);
    }

    // =========================================================================
    // Helper Methods
    // =========================================================================

    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }

    private static Object[] prependArg(Object first, Object[] rest) {
        Object[] result = new Object[rest.length + 1];
        result[0] = first;
        System.arraycopy(rest, 0, result, 1, rest.length);
        return result;
    }

    /**
     * Check if debug logging is enabled (to avoid expensive string operations).
     */
    public static boolean isDebugEnabled() {
        return BASE.isDebugEnabled();
    }

    /**
     * Check if evaluator debug logging is enabled.
     */
    public static boolean isEvaluatorDebugEnabled() {
        return EVALUATOR.isDebugEnabled();
    }
}
