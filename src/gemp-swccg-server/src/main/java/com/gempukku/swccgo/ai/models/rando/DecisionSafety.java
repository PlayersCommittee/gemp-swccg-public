package com.gempukku.swccgo.ai.models.rando;

import com.gempukku.swccgo.logic.decisions.AwaitingDecision;

import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Decision Safety Module
 *
 * Provides guaranteed decision responses to ensure the bot NEVER hangs.
 * This is the "last line of defense" - if all evaluators and fallbacks fail,
 * this module ensures we still send a valid response to the server.
 *
 * Design Philosophy:
 * - EVERY decision must get a response - NEVER return without posting a decision
 * - A bad decision is better than no decision (game continues vs hangs)
 * - If noPass=true OR min>=1, we MUST return a valid choice (not empty string)
 * - Log everything for debugging, but never fail silently
 *
 * Ported from Python decision_safety.py
 */
public class DecisionSafety {
    private static final Logger LOG = RandoLogger.getSafetyLogger();
    private static final Random RANDOM = new Random();

    // Known decision types we can handle
    public static final Set<String> KNOWN_TYPES = Set.of(
        "MULTIPLE_CHOICE",
        "CARD_SELECTION",
        "CARD_ACTION_CHOICE",
        "ACTION_CHOICE",
        "INTEGER",
        "ARBITRARY_CARDS"
    );

    /**
     * A guaranteed safe decision response.
     */
    public static class SafetyDecision {
        public final String decisionId;
        public final String value;
        public final String reason;
        public final boolean wasEmergency;

        public SafetyDecision(String decisionId, String value, String reason, boolean wasEmergency) {
            this.decisionId = decisionId;
            this.value = value;
            this.reason = reason;
            this.wasEmergency = wasEmergency;
        }
    }

    /**
     * Check if we MUST choose something (cannot pass).
     * True if there's only one valid option, or decision indicates required choice.
     */
    public static boolean mustChoose(AwaitingDecision decision) {
        // First check the noPass parameter - this is the authoritative source
        Map<String, String[]> params = decision.getDecisionParameters();
        if (params != null) {
            String[] noPassArr = params.get("noPass");
            if (noPassArr != null && noPassArr.length > 0) {
                return Boolean.parseBoolean(noPassArr[0]);
            }
        }

        // For most GEMP decisions, we check the available options
        String[] results = params != null ? params.get("results") : null;
        if (results != null && results.length == 1) {
            return true;  // Only one option means we must choose it
        }

        // Check for typical required decision indicators in the PROMPT text
        // IMPORTANT: Strip out card names (in HTML divs) to avoid false positives
        // e.g., "Playing •We Must Accelerate Our Plans" shouldn't trigger on "must"
        String text = decision.getText();
        if (text != null) {
            // Remove HTML card hints which contain card names
            String cleanText = text.replaceAll("<div[^>]*>.*?</div>", "")
                                   .replaceAll("<[^>]+>", "");
            String lowerText = cleanText.toLowerCase(Locale.ROOT);

            // Only match "must" at the start of a decision prompt like "You must choose..."
            // or explicit "required" indicators
            if (lowerText.startsWith("you must") ||
                lowerText.startsWith("must ") ||
                lowerText.contains(" must choose") ||
                lowerText.contains("required")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if we CAN pass (return empty string or done).
     */
    public static boolean canPass(AwaitingDecision decision) {
        String text = decision.getText();
        if (text != null) {
            String lowerText = text.toLowerCase(Locale.ROOT);
            return lowerText.contains("may") ||
                   lowerText.contains("optional") ||
                   lowerText.contains("done") ||
                   lowerText.contains("cancel");
        }
        return true;  // Default to allowing pass
    }

    /**
     * CRITICAL SAFETY METHOD - ensures response is valid before sending.
     *
     * If response is invalid (empty when must choose), this will force
     * a valid response using random selection.
     *
     * @return array of [correctedResponse, reasonIfCorrected]
     */
    public static String[] ensureValidResponse(AwaitingDecision decision, String response,
                                                String[] availableOptions) {
        boolean mustMakeChoice = mustChoose(decision);

        // If response is empty but we MUST choose, force a selection
        if ((response == null || response.isEmpty()) && mustMakeChoice) {
            if (availableOptions != null && availableOptions.length > 0) {
                String forced = availableOptions[RANDOM.nextInt(availableOptions.length)];
                String reason = "SAFETY FORCED: Empty response but must choose. Picked random: " + forced;
                LOG.error(reason);
                return new String[]{forced, reason};
            } else {
                // Absolute last resort
                LOG.error("SAFETY CRITICAL: Must choose but no options available!");
                return new String[]{"0", "SAFETY CRITICAL: No options, guessing '0'"};
            }
        }

        // Response is valid
        return new String[]{response, ""};
    }

    /**
     * Get an emergency response for any decision.
     *
     * This is the LAST resort - called when all other handlers fail.
     * It will ALWAYS return a valid response.
     */
    public static SafetyDecision getEmergencyResponse(AwaitingDecision decision,
                                                       String[] actionIds,
                                                       String[] cardIds) {
        String decisionType = decision.getDecisionType().name();
        String decisionId = String.valueOf(decision.getAwaitingDecisionId());
        String decisionText = decision.getText() != null ? decision.getText() : "";
        boolean mustMakeChoice = mustChoose(decision);

        LOG.warn("EMERGENCY RESPONSE for {}: '{}'",
            decisionType, truncate(decisionText, 50));
        LOG.warn("   mustChoose={}, actions={}, cards={}",
            mustMakeChoice,
            actionIds != null ? actionIds.length : 0,
            cardIds != null ? cardIds.length : 0);

        String responseValue = "";
        String reason = "";

        // Handle each decision type
        switch (decisionType) {
            case "INTEGER":
                // For INTEGER, use 0 (safer - preserves resources)
                responseValue = "0";
                reason = "Emergency: INTEGER decision, using 0";
                break;

            case "MULTIPLE_CHOICE":
                // For yes/no questions, try to be conservative
                String textLower = decisionText.toLowerCase(Locale.ROOT);
                if (textLower.contains("concede") || textLower.contains("forfeit") ||
                    textLower.contains("surrender")) {
                    responseValue = "1";  // Usually "No" is option 1
                    reason = "Emergency: Detected concede/forfeit, choosing No";
                } else {
                    responseValue = "0";  // Default to first option
                    reason = "Emergency: MULTIPLE_CHOICE, choosing first option";
                }
                break;

            case "CARD_ACTION_CHOICE":
            case "ACTION_CHOICE":
                if (actionIds != null && actionIds.length > 0) {
                    responseValue = actionIds[RANDOM.nextInt(actionIds.length)];
                    reason = "Emergency: Choosing random action (" + responseValue + ")";
                } else if (!mustMakeChoice) {
                    responseValue = "";
                    reason = "Emergency: No actions, passing allowed";
                } else {
                    responseValue = "";
                    reason = "Emergency: No actions but must choose - will likely fail";
                }
                break;

            case "CARD_SELECTION":
            case "ARBITRARY_CARDS":
                if (cardIds != null && cardIds.length > 0) {
                    responseValue = cardIds[RANDOM.nextInt(cardIds.length)];
                    reason = "Emergency: Selecting random card (" + responseValue + ")";
                } else if (!mustMakeChoice) {
                    responseValue = "";
                    reason = "Emergency: No cards, passing allowed";
                } else {
                    responseValue = "";
                    reason = "Emergency: No cards but must choose - will likely fail";
                }
                break;

            default:
                // Unknown decision type
                LOG.error("UNKNOWN DECISION TYPE: {}", decisionType);
                if (actionIds != null && actionIds.length > 0) {
                    responseValue = actionIds[RANDOM.nextInt(actionIds.length)];
                    reason = "Emergency: Unknown type, picking random action";
                } else if (cardIds != null && cardIds.length > 0) {
                    responseValue = cardIds[RANDOM.nextInt(cardIds.length)];
                    reason = "Emergency: Unknown type, picking random card";
                } else {
                    responseValue = "0";
                    reason = "Emergency: Unknown type '" + decisionType + "', guessing '0'";
                }
        }

        // FINAL SAFETY CHECK - if we must choose and response is empty, force a pick
        if (mustMakeChoice && (responseValue == null || responseValue.isEmpty())) {
            String[] allOptions = actionIds != null && actionIds.length > 0 ? actionIds : cardIds;
            if (allOptions != null && allOptions.length > 0) {
                responseValue = allOptions[RANDOM.nextInt(allOptions.length)];
                reason += " -> SAFETY OVERRIDE: forced random pick (" + responseValue + ")";
                LOG.error("SAFETY OVERRIDE: Must choose but had empty response, forcing: {}", responseValue);
            }
        }

        LOG.warn("   -> Response: '{}' ({})", responseValue, reason);

        return new SafetyDecision(decisionId, responseValue, reason, true);
    }

    /**
     * Validate that a response is likely valid for the given decision.
     *
     * @return array of [isValid, warningMessage]
     */
    public static Object[] validateResponse(AwaitingDecision decision, String response,
                                            String[] actionIds, String[] cardIds) {
        String decisionType = decision.getDecisionType().name();
        boolean mustMakeChoice = mustChoose(decision);

        // Validate based on type
        if ((response == null || response.isEmpty()) && mustMakeChoice) {
            return new Object[]{false, "Empty response but must choose - might fail"};
        }

        if ("CARD_ACTION_CHOICE".equals(decisionType) || "ACTION_CHOICE".equals(decisionType)) {
            if (response != null && !response.isEmpty() && actionIds != null) {
                boolean found = false;
                for (String aid : actionIds) {
                    if (aid.equals(response)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return new Object[]{false, "Response '" + response + "' not in action_ids"};
                }
            }
        }

        if ("CARD_SELECTION".equals(decisionType)) {
            if (response != null && !response.isEmpty() && cardIds != null) {
                boolean found = false;
                for (String cid : cardIds) {
                    if (cid.equals(response)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return new Object[]{false, "Response '" + response + "' not in card_ids"};
                }
            }
        }

        return new Object[]{true, ""};
    }

    /**
     * Get a safe "pass" value for the decision, if passing is allowed.
     *
     * @return null if passing is not allowed, empty string if allowed
     */
    public static String getSafePassValue(AwaitingDecision decision) {
        if (canPass(decision) && !mustChoose(decision)) {
            return "";
        }
        return null;
    }

    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }
}
