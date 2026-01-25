package com.gempukku.swccgo.ai.models;
import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.ai.common.AiCardHelper;
import com.gempukku.swccgo.ai.models.rando.DecisionSafety;
import com.gempukku.swccgo.ai.models.rando.DecisionTracker;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecisionType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// Heuristic AI base that scores actions/choices using simple keyword weights.
public abstract class HeuristicAiBase implements SwccgAiController {
    private static final int BLOCKED_RESPONSE_PENALTY = 500;
    private static final int FAILED_SEARCH_PENALTY = 650;
    private static final int MISSING_RESERVE_DECK_TITLE_PENALTY = 900;
    private static final int SINGLE_DECISION_LOOP_THRESHOLD = 2;
    private static final int RECENT_DECISION_RESPONSE_WINDOW = 6;
    private static final int REASSIGNMENT_BASE_PENALTY = 300;
    private static final int REASSIGNMENT_REPEAT_PENALTY = 800;
    private static final int RECENT_REASSIGNMENT_PENALTY = 700;
    private static final int RECENT_REASSIGNMENT_TURN_MEMORY = 1;
    private static final String[] RESERVE_DECK_ACTION_VERBS = new String[] {
            "deploy ", "take ", "retrieve ", "download ", "play ", "put ", "search ", "find ", "choose ", "pull "
    };
    private static final String[] RESERVE_DECK_STOP_WORDS = new String[] {
            "a", "an", "any", "the", "your", "opponents", "opponent", "their",
            "from", "to", "into", "hand", "reserve", "deck", "choose", "select",
            "take", "deploy", "play", "put", "retrieve", "download", "card", "cards",
            "one", "up", "with", "game", "text", "in", "except"
    };
    private static final Keyword[] RESERVE_DECK_KEYWORDS = buildReserveDeckKeywords();
    private final DecisionTracker decisionTracker = new DecisionTracker();
    private final Set<String> failedSearchActionTexts = new HashSet<String>();
    private final Set<String> failedSearchCardIds = new HashSet<String>();
    private final Set<String> failedSearchBlueprintIds = new HashSet<String>();
    private String lastActionChoiceText = "";
    private String lastActionChoiceCardId = "";
    private String lastActionChoiceBlueprintId = "";
    private final Map<String, Set<String>> localBlockedResponses = new HashMap<String, Set<String>>();
    private final Map<String, ArrayDeque<String>> recentDecisionResponses = new HashMap<String, ArrayDeque<String>>();
    private final Map<String, Integer> recentReassignmentTurns = new HashMap<String, Integer>();
    private final Map<String, Integer> reassignmentCounts = new HashMap<String, Integer>();
    private String currentStateHash = "";
    private String blockStateHash = "";
    private String lastDecisionStateHash = "";
    private String lastDecisionKey = "";
    private String lastDecisionResponse = "";
    private int lastDecisionRepeatCount = 0;
    private int currentTurnNumber = 0;

    @Override
    public String decide(String playerId, AwaitingDecision decision, GameState gameState) {
        Map<String, String[]> params = decision.getDecisionParameters();
        String decisionType = decision.getDecisionType() != null ? decision.getDecisionType().name() : "UNKNOWN";
        String decisionText = decision.getText() != null ? decision.getText() : "";
        Phase phase = gameState != null ? gameState.getCurrentPhase() : null;

        if (phase != null) {
            decisionTracker.onPhaseChange(phase.name());
        }
        updateDecisionTrackerState(gameState, playerId);

        String result;
        if (decision.getDecisionType() == AwaitingDecisionType.EMPTY) {
            result = "pass";
        } else if (params == null || params.isEmpty()) {
            result = "0";
        } else {
            switch (decision.getDecisionType()) {
                case MULTIPLE_CHOICE:
                    result = pickMultipleChoice(playerId, decision, params, gameState);
                    break;
                case ACTION_CHOICE:
                case CARD_ACTION_CHOICE:
                    result = pickActionChoice(playerId, decision, params, gameState);
                    break;
                case CARD_SELECTION:
                    result = pickCardSelection(decision, params);
                    break;
                case ARBITRARY_CARDS:
                    result = pickArbitraryCards(playerId, decision, params, gameState);
                    break;
                case INTEGER:
                    result = pickInteger(decision, params);
                    break;
                default:
                    result = "0";
            }
        }

        if ((result == null || result.isEmpty())
                && ("CARD_SELECTION".equals(decisionType) || "ARBITRARY_CARDS".equals(decisionType))) {
            decisionTracker.blockLastActionOnCancel(decisionType, decisionText);
        }

        String[] actionIds = params != null ? params.get("actionId") : null;
        String[] cardIds = params != null ? params.get("cardId") : null;
        boolean mustChoose = DecisionSafety.mustChoose(decision);
        if (result == null || (result.isEmpty() && mustChoose)) {
            DecisionSafety.SafetyDecision emergency =
                DecisionSafety.getEmergencyResponse(decision, actionIds, cardIds);
            result = emergency.value;
        }

        String[] availableOptions = buildResponseOptions(decision, params);
        String[] validated = DecisionSafety.ensureValidResponse(decision, result, availableOptions);
        result = validated[0];

        updateLastActionChoiceText(decisionType, result, params);
        handleFailedSearchVerification(decision, params, gameState, playerId);
        String trackingResponse = getTrackingResponse(decision, params, result);
        updateSingleDecisionLoop(decision, params, result, trackingResponse);
        recordRecentDecisionResponse(decision, trackingResponse);
        recordRecentReassignment(decision, params, result);

        decisionTracker.recordDecision(decisionType, decisionText,
            String.valueOf(decision.getAwaitingDecisionId()), trackingResponse != null ? trackingResponse : "");
        return result;
    }

    protected int getPassPenalty() {
        return 200;
    }

    protected boolean shouldSkipOptionalResponses() {
        return true;
    }

    protected abstract KeywordWeight[] getActionWeights();

    protected abstract KeywordWeight[] getActionPenalties();

    protected abstract KeywordWeight[] getChoiceWeights();

    protected abstract KeywordWeight[] getChoicePenalties();

    protected abstract String[] getCardHints();

    protected int scoreActionContext(String playerId, GameState gameState, String decisionText, String actionText, Phase phase, Map<String, String[]> params) {
        return 0;
    }

    protected int scoreChoiceContext(String playerId, GameState gameState, String decisionText, String choiceText, String[] results) {
        return 0;
    }

    protected int scoreCardContext(String playerId, GameState gameState, String decisionText, String cardText) {
        return 0;
    }

    private String pickMultipleChoice(String playerId, AwaitingDecision decision, Map<String, String[]> params, GameState gameState) {
        String[] results = firstNonNull(params, "index", "choice", "results");
        if (results == null || results.length == 0) {
            return "0";
        }

        // Respect defaultIndex if present and valid
        int defaultIdx = parseInt(params.get("defaultIndex"), 0);
        String decisionText = lower(decision.getText());
        Set<String> blocked = getBlockedResponses(decision);
        Set<String> recentLoopBlocked = getRecentLoopBlockedResponses(decision);
        if (!recentLoopBlocked.isEmpty()) {
            blocked.addAll(recentLoopBlocked);
        }
        boolean forceDifferent = decisionTracker.shouldForceDifferentChoice();

        int passIdx = findPassIndex(results, false);
        int bestIdx = -1;
        int bestScore = Integer.MIN_VALUE;
        int bestBlockedIdx = -1;
        int bestBlockedScore = Integer.MIN_VALUE;
        for (int i = 0; i < results.length; i++) {
            String choice = lower(results[i]);
            int score = scoreChoice(decisionText, choice, results)
                    + scoreChoiceContext(playerId, gameState, decisionText, choice, results);
            if (i == passIdx) {
                score -= getPassPenalty();
            }
            if (i == defaultIdx) {
                score += 2;
            }
            boolean isBlocked = isBlockedResponse(blocked, String.valueOf(i), choice);
            if (isBlocked) {
                score -= BLOCKED_RESPONSE_PENALTY;
                if (score > bestBlockedScore) {
                    bestBlockedScore = score;
                    bestBlockedIdx = i;
                }
                if (forceDifferent) {
                    continue;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                bestIdx = i;
            }
        }

        if (bestIdx != -1) {
            return String.valueOf(bestIdx);
        }
        if (bestBlockedIdx != -1) {
            return String.valueOf(bestBlockedIdx);
        }

        return "0";
    }

    private String pickFirstIndex(Map<String, String[]> params) {
        String[] results = firstNonNull(params, "index", "choice", "actionId", "results");
        if (results != null && results.length > 0) {
            // Respect defaultIndex if present and valid
            int defaultIdx = parseInt(params.get("defaultIndex"), 0);
            if (defaultIdx >= 0 && defaultIdx < results.length) {
                return String.valueOf(defaultIdx);
            }
            return "0";
        }
        return "0";
    }

    private String pickActionChoice(String playerId, AwaitingDecision decision, Map<String, String[]> params, GameState gameState) {
        String[] actions = params.get("actionId");
        boolean noPass = Boolean.parseBoolean(first(params.get("noPass"), "false"));

        if (actions == null || actions.length == 0) {
            // No available actions -> pass
            return "";
        }

        String decisionText = lower(decision.getText());
        Set<String> blocked = getBlockedResponses(decision);
        Set<String> recentLoopBlocked = getRecentLoopBlockedResponses(decision);
        if (!recentLoopBlocked.isEmpty()) {
            blocked.addAll(recentLoopBlocked);
        }
        boolean forceDifferent = decisionTracker.shouldForceDifferentChoice();
        if (decisionText.contains("optional responses") && shouldSkipOptionalResponses()) {
            // Skip spamming optional responses; let the stack clear
            return "";
        }

        // Prefer to pass move spam loops unless forced
        if (decisionText.contains("move action") || decisionText.contains("move or pass")) {
            if (!noPass) {
                int passIdx = findPassIndex(params.get("actionText"), true);
                if (passIdx >= 0) {
                    return String.valueOf(passIdx);
                }
                return "";
            }
        }

        // Prefer a non-pass action if available
        String[] actionTexts = params.get("actionText");
        int passIdx = findPassIndex(actionTexts, true);

        // Avoid toggling capacity slots back and forth if pass is allowed
        boolean capacityShuffle = false;
        if (actionTexts != null) {
            for (String txt : actionTexts) {
                if (txt == null) continue;
                String lc = txt.toLowerCase(Locale.ROOT);
                if (lc.contains("capacity slot")) {
                    capacityShuffle = true; // avoid seat-shuffling loops
                    break;
                }
            }
        }
        if (capacityShuffle && decisionText.contains("deploy action") && !noPass) {
            if (passIdx >= 0) {
                return String.valueOf(passIdx);
            }
            return "";
        }

        String[] actionCardIds = params.get("cardId");
        String[] actionBlueprintIds = params.get("blueprintId");
        boolean hasPreferredAction = false;
        for (int i = 0; i < actions.length; i++) {
            String actionText = lower(getAtIndex(actionTexts, i));
            String actionCardId = lower(getAtIndex(actionCardIds, i));
            String actionBlueprintId = lower(getAtIndex(actionBlueprintIds, i));
            String blockText = actionText;
            if (blockText.isEmpty()) {
                blockText = actionCardId;
            }
            if (blockText.isEmpty()) {
                blockText = actionBlueprintId;
            }
            if (isPassLike(blockText)) {
                continue;
            }
            if (isBlockedResponse(blocked, String.valueOf(i), blockText)) {
                continue;
            }
            boolean recentReassignment = isRecentlyReassignedAction(actionText, actionCardId, actionBlueprintId);
            boolean priorReassignment = hasPriorReassignment(actionText, actionCardId, actionBlueprintId);
            if (recentReassignment || priorReassignment) {
                continue;
            }
            if (!blockText.isEmpty()) {
                hasPreferredAction = true;
                break;
            }
        }
        if (!hasPreferredAction && !DecisionSafety.mustChoose(decision)) {
            if (passIdx >= 0) {
                return String.valueOf(passIdx);
            }
            return "";
        }

        Phase phase = gameState != null ? gameState.getCurrentPhase() : null;
        ReserveDeckKnowledge reserveDeck = buildReserveDeckKnowledge(gameState, playerId);
        int bestIdx = -1;
        int bestScore = Integer.MIN_VALUE;
        int bestBlockedIdx = -1;
        int bestBlockedScore = Integer.MIN_VALUE;
        for (int i = 0; i < actions.length; i++) {
            String actionText = lower(getAtIndex(actionTexts, i));
            String actionCardId = lower(getAtIndex(actionCardIds, i));
            String actionBlueprintId = lower(getAtIndex(actionBlueprintIds, i));
            String blockText = actionText;
            if (blockText.isEmpty()) {
                blockText = actionCardId;
            }
            if (blockText.isEmpty()) {
                blockText = actionBlueprintId;
            }
            boolean recentReassignment = isRecentlyReassignedAction(actionText, actionCardId, actionBlueprintId);
            boolean priorReassignment = hasPriorReassignment(actionText, actionCardId, actionBlueprintId);
            boolean avoidReassignment = recentReassignment || priorReassignment;
            int score = scoreAction(actionText, decisionText, phase)
                    + scoreActionContext(playerId, gameState, decisionText, actionText, phase, params);
            if (i == passIdx) {
                score -= getPassPenalty();
            }
            boolean isBlocked = isBlockedResponse(blocked, String.valueOf(i), blockText);
            if (isBlocked) {
                score -= BLOCKED_RESPONSE_PENALTY;
                if (score > bestBlockedScore) {
                    bestBlockedScore = score;
                    bestBlockedIdx = i;
                }
                if (forceDifferent || hasPreferredAction) {
                    continue;
                }
            }
            if (avoidReassignment && hasPreferredAction) {
                continue;
            }
            if (isReassignmentAction(actionText)) {
                score -= REASSIGNMENT_BASE_PENALTY;
            }
            if (priorReassignment) {
                score -= REASSIGNMENT_REPEAT_PENALTY;
            }
            if (recentReassignment) {
                score -= RECENT_REASSIGNMENT_PENALTY;
            }
            if (!actionText.isEmpty() && failedSearchActionTexts.contains(actionText)) {
                score -= FAILED_SEARCH_PENALTY;
            }
            if (!actionCardId.isEmpty() && failedSearchCardIds.contains(actionCardId)) {
                score -= FAILED_SEARCH_PENALTY;
            }
            if (!actionBlueprintId.isEmpty()
                    && !"inplay".equals(actionBlueprintId)
                    && !"rules".equals(actionBlueprintId)
                    && failedSearchBlueprintIds.contains(actionBlueprintId)) {
                score -= FAILED_SEARCH_PENALTY;
            }
            if (shouldAvoidReserveDeckAction(actionText, reserveDeck)) {
                score -= MISSING_RESERVE_DECK_TITLE_PENALTY;
            }
            if (score > bestScore) {
                bestScore = score;
                bestIdx = i;
            }
        }

        if (!noPass && passIdx >= 0 && isOptionalDecision(decisionText) && bestScore < 0) {
            return String.valueOf(passIdx);
        }

        if (bestIdx != -1) {
            return String.valueOf(bestIdx);
        }
        if (bestBlockedIdx != -1) {
            return String.valueOf(bestBlockedIdx);
        }

        return pickFirstIndex(params);
    }

    private String pickCardSelection(AwaitingDecision decision, Map<String, String[]> params) {
        String[] cardIds = params.get("cardId");
        int min = parseInt(params.get("min"), 0);
        if (cardIds == null || cardIds.length == 0) {
            return "";
        }
        int pickCount = Math.max(min, 1);
        pickCount = Math.min(pickCount, cardIds.length);
        Set<String> blocked = getBlockedResponses(decision);
        List<String> ordered = new ArrayList<String>();
        for (String cardId : cardIds) {
            if (cardId != null && !blocked.contains(cardId)) {
                ordered.add(cardId);
            }
        }
        for (String cardId : cardIds) {
            if (cardId != null && blocked.contains(cardId)) {
                ordered.add(cardId);
            }
        }
        if (ordered.isEmpty()) {
            return "";
        }
        return String.join(",", ordered.subList(0, Math.min(pickCount, ordered.size())));
    }

    private String pickArbitraryCards(String playerId, AwaitingDecision decision, Map<String, String[]> params, GameState gameState) {
        String[] cardIds = params.get("cardId");
        String[] selectable = params.get("selectable");
        int min = parseInt(params.get("min"), 0);
        int max = parseInt(params.get("max"), cardIds != null ? cardIds.length : 0);
        if (max < min) {
            max = min;
        }
        if (cardIds == null || cardIds.length == 0) {
            return "";
        }

        List<Integer> candidates = new ArrayList<Integer>();
        for (int i = 0; i < cardIds.length; i++) {
            if (isSelectable(selectable, i)) {
                candidates.add(i);
            }
        }
        if (candidates.isEmpty()) {
            return "";
        }

        final String decisionText = lower(decision.getText());
        final String[] cardText = params.get("cardText");
        final String[] testingText = params.get("testingText");
        final String[] backSideTestingText = params.get("backSideTestingText");
        final Set<String> blocked = getBlockedResponses(decision);
        final boolean forceDifferent = decisionTracker.shouldForceDifferentChoice();
        final String[] ids = cardIds;

        Collections.sort(candidates, new Comparator<Integer>() {
            @Override
            public int compare(Integer left, Integer right) {
                int leftScore = scoreCard(decisionText, combine(cardText, testingText, backSideTestingText, left))
                        + scoreCardContext(playerId, gameState, decisionText, combine(cardText, testingText, backSideTestingText, left));
                int rightScore = scoreCard(decisionText, combine(cardText, testingText, backSideTestingText, right))
                        + scoreCardContext(playerId, gameState, decisionText, combine(cardText, testingText, backSideTestingText, right));
                if (isBlockedCard(ids, left, blocked)) {
                    leftScore -= forceDifferent ? BLOCKED_RESPONSE_PENALTY * 2 : BLOCKED_RESPONSE_PENALTY;
                }
                if (isBlockedCard(ids, right, blocked)) {
                    rightScore -= forceDifferent ? BLOCKED_RESPONSE_PENALTY * 2 : BLOCKED_RESPONSE_PENALTY;
                }
                if (leftScore != rightScore) {
                    return rightScore - leftScore;
                }
                return left - right;
            }
        });

        int pickCount = Math.max(min, 1);
        if (min == 0 && isOptionalDecision(decisionText)) {
            int bestScore = scoreCard(decisionText, combine(cardText, testingText, backSideTestingText, candidates.get(0)));
            if (bestScore < 0) {
                return "";
            }
        }
        pickCount = Math.min(pickCount, max);
        pickCount = Math.min(pickCount, candidates.size());

        StringBuilder choice = new StringBuilder();
        for (int i = 0; i < pickCount; i++) {
            if (choice.length() > 0) {
                choice.append(",");
            }
            choice.append(cardIds[candidates.get(i)]);
        }
        return choice.toString();
    }

    private boolean isSelectable(String[] selectable, int index) {
        if (selectable == null || index < 0 || index >= selectable.length) {
            return true;
        }
        return Boolean.parseBoolean(selectable[index]);
    }

    private String pickInteger(AwaitingDecision decision, Map<String, String[]> params) {
        int min = parseInt(params.get("min"), 0);
        int max = parseInt(params.get("max"), min);
        int defaultVal = parseInt(params.get("defaultValue"), min);

        // Normalize bounds
        if (max < min) {
            max = min;
        }

        String text = lower(decision.getText());
        boolean forceActivation = text.contains("force") && text.contains("activate");
        boolean gainValue = text.contains("draw") || text.contains("retrieve") || text.contains("activate") || text.contains("peek");
        boolean loseValue = text.contains("lose") || text.contains("forfeit") || text.contains("use");

        int choice = defaultVal;
        if (forceActivation || gainValue) {
            choice = max;
        } else if (loseValue) {
            choice = min;
        }
        if (choice < min) choice = min;
        if (choice > max) choice = max;

        return String.valueOf(choice);
    }

    private int parseInt(String[] value, int defaultValue) {
        if (value == null || value.length == 0 || value[0] == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value[0]);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String first(String[] value, String defaultValue) {
        if (value == null || value.length == 0 || value[0] == null) {
            return defaultValue;
        }
        return value[0];
    }

    private String[] firstNonNull(Map<String, String[]> params, String... keys) {
        for (String key : keys) {
            String[] value = params.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private int findPassIndex(String[] values, boolean includeNo) {
        if (values == null) {
            return -1;
        }
        for (int i = 0; i < values.length; i++) {
            String v = values[i];
            if (v == null) continue;
            String lc = v.toLowerCase(Locale.ROOT);
            if (lc.equals("pass") || lc.equals("cancel") || (includeNo && lc.equals("no"))) {
                return i;
            }
        }
        return -1;
    }

    private String lower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private String getAtIndex(String[] values, int index) {
        if (values == null || index < 0 || index >= values.length) {
            return "";
        }
        String value = values[index];
        return value == null ? "" : value;
    }

    private Set<String> getBlockedResponses(AwaitingDecision decision) {
        if (decision == null || decision.getDecisionType() == null) {
            return Collections.emptySet();
        }
        String decisionType = decision.getDecisionType().name();
        String decisionText = decision.getText() != null ? decision.getText() : "";
        String key = buildDecisionKey(decisionType, decisionText);
        Set<String> blocked = new HashSet<String>();
        blocked.addAll(decisionTracker.getBlockedResponses(decisionType, decisionText));
        Set<String> local = localBlockedResponses.get(key);
        if (local != null) {
            blocked.addAll(local);
        }
        return blocked;
    }

    private boolean isBlockedResponse(Set<String> blocked, String responseValue, String responseText) {
        if (blocked == null || blocked.isEmpty()) {
            return false;
        }
        if (responseText != null && isPassLike(responseText)) {
            return false;
        }
        if (responseValue != null && blocked.contains(responseValue)) {
            return true;
        }
        return responseText != null && blocked.contains(responseText);
    }

    private boolean isPassLike(String text) {
        if (text == null) {
            return false;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        return lower.equals("pass") || lower.equals("cancel") || lower.equals("no");
    }

    private Set<String> getRecentLoopBlockedResponses(AwaitingDecision decision) {
        if (decision == null || decision.getDecisionType() == null || currentStateHash.isEmpty()) {
            return Collections.emptySet();
        }
        AwaitingDecisionType decisionType = decision.getDecisionType();
        if (decisionType != AwaitingDecisionType.ACTION_CHOICE
                && decisionType != AwaitingDecisionType.CARD_ACTION_CHOICE) {
            return Collections.emptySet();
        }
        String key = buildDecisionKey(decisionType.name(), decision.getText());
        ArrayDeque<String> history = recentDecisionResponses.get(key);
        if (history == null || history.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<String>(history);
    }

    private void recordRecentDecisionResponse(AwaitingDecision decision, String trackingResponse) {
        if (decision == null || trackingResponse == null || trackingResponse.isEmpty() || currentStateHash.isEmpty()) {
            return;
        }
        AwaitingDecisionType decisionType = decision.getDecisionType();
        if (decisionType != AwaitingDecisionType.ACTION_CHOICE
                && decisionType != AwaitingDecisionType.CARD_ACTION_CHOICE) {
            return;
        }
        String key = buildDecisionKey(decisionType.name(), decision.getText());
        ArrayDeque<String> history = recentDecisionResponses.get(key);
        if (history == null) {
            history = new ArrayDeque<String>();
            recentDecisionResponses.put(key, history);
        }
        history.addLast(trackingResponse);
        while (history.size() > RECENT_DECISION_RESPONSE_WINDOW) {
            history.removeFirst();
        }
    }

    private void recordRecentReassignment(AwaitingDecision decision, Map<String, String[]> params, String response) {
        if (decision == null || params == null || response == null || response.isEmpty() || currentTurnNumber <= 0) {
            return;
        }
        AwaitingDecisionType decisionType = decision.getDecisionType();
        if (decisionType != AwaitingDecisionType.ACTION_CHOICE
                && decisionType != AwaitingDecisionType.CARD_ACTION_CHOICE) {
            return;
        }
        if (isPassResponse(decision, params, response)) {
            return;
        }
        int index = parseResponseIndex(response);
        if (index < 0) {
            return;
        }
        String actionText = lower(getAtIndex(params.get("actionText"), index));
        if (!isReassignmentAction(actionText)) {
            return;
        }
        String actionCardId = lower(getAtIndex(params.get("cardId"), index));
        String actionBlueprintId = lower(getAtIndex(params.get("blueprintId"), index));
        String key = buildReassignmentKey(actionCardId, actionBlueprintId, actionText);
        if (!key.isEmpty()) {
            recentReassignmentTurns.put(key, currentTurnNumber);
            incrementReassignmentCount(key);
        }
    }

    private boolean isRecentlyReassignedAction(String actionText, String actionCardId, String actionBlueprintId) {
        if (!isReassignmentAction(actionText) || currentTurnNumber <= 0) {
            return false;
        }
        String key = buildReassignmentKey(actionCardId, actionBlueprintId, actionText);
        if (key.isEmpty()) {
            return false;
        }
        Integer lastTurn = recentReassignmentTurns.get(key);
        if (lastTurn == null) {
            return false;
        }
        return currentTurnNumber - lastTurn <= RECENT_REASSIGNMENT_TURN_MEMORY;
    }

    private boolean hasPriorReassignment(String actionText, String actionCardId, String actionBlueprintId) {
        if (!isReassignmentAction(actionText)) {
            return false;
        }
        String key = buildReassignmentKey(actionCardId, actionBlueprintId, actionText);
        if (key.isEmpty()) {
            return false;
        }
        Integer count = reassignmentCounts.get(key);
        return count != null && count > 0;
    }

    private void incrementReassignmentCount(String key) {
        if (key == null || key.isEmpty()) {
            return;
        }
        Integer count = reassignmentCounts.get(key);
        reassignmentCounts.put(key, count == null ? 1 : count + 1);
    }

    private boolean isReassignmentAction(String actionText) {
        if (actionText == null || actionText.isEmpty()) {
            return false;
        }
        return actionText.contains("transfer")
                || actionText.contains("relocate")
                || actionText.contains("reassign");
    }

    private String buildReassignmentKey(String actionCardId, String actionBlueprintId, String actionText) {
        if (actionCardId != null && !actionCardId.isEmpty()) {
            return "card:" + actionCardId;
        }
        if (actionBlueprintId != null && !actionBlueprintId.isEmpty()
                && !"inplay".equals(actionBlueprintId)
                && !"rules".equals(actionBlueprintId)) {
            return "blueprint:" + actionBlueprintId;
        }
        String subject = extractReassignmentSubject(actionText);
        if (!subject.isEmpty()) {
            return "text:" + subject;
        }
        return "";
    }

    private String extractReassignmentSubject(String actionText) {
        if (actionText == null || actionText.isEmpty()) {
            return "";
        }
        String text = actionText.toLowerCase(Locale.ROOT);
        int start = -1;
        int startLen = 0;
        String[] verbs = new String[] {"transfer ", "relocate ", "reassign "};
        for (String verb : verbs) {
            int idx = text.indexOf(verb);
            if (idx >= 0) {
                start = idx + verb.length();
                startLen = verb.length();
                break;
            }
        }
        if (start <= 0 || startLen == 0) {
            return normalizeText(text);
        }
        int cutTo = text.indexOf(" to ", start);
        int cutFrom = text.indexOf(" from ", start);
        int cut = cutTo;
        if (cut < 0 || (cutFrom >= 0 && cutFrom < cut)) {
            cut = cutFrom;
        }
        String subject = cut > start ? text.substring(start, cut) : text.substring(start);
        subject = subject.trim();
        return normalizeText(subject);
    }

    private void updateSingleDecisionLoop(AwaitingDecision decision, Map<String, String[]> params,
                                          String response, String trackingResponse) {
        if (decision == null || response == null || currentStateHash.isEmpty()) {
            return;
        }
        String responseKey = trackingResponse != null ? trackingResponse : response;
        if (responseKey == null || responseKey.isEmpty()) {
            lastDecisionRepeatCount = 0;
            lastDecisionKey = "";
            lastDecisionResponse = "";
            lastDecisionStateHash = "";
            return;
        }
        String decisionTypeName = decision.getDecisionType() != null ? decision.getDecisionType().name() : "UNKNOWN";
        String decisionText = decision.getText() != null ? decision.getText() : "";
        String key = buildDecisionKey(decisionTypeName, decisionText);
        boolean sameDecision = key.equals(lastDecisionKey)
                && responseKey.equals(lastDecisionResponse)
                && currentStateHash.equals(lastDecisionStateHash);

        if (sameDecision) {
            lastDecisionRepeatCount++;
        } else {
            lastDecisionRepeatCount = 1;
        }

        lastDecisionKey = key;
        lastDecisionResponse = responseKey;
        lastDecisionStateHash = currentStateHash;

        if (lastDecisionRepeatCount >= SINGLE_DECISION_LOOP_THRESHOLD
                && !isPassResponse(decision, params, response)) {
            Set<String> blocked = localBlockedResponses.get(key);
            if (blocked == null) {
                blocked = new HashSet<String>();
                localBlockedResponses.put(key, blocked);
            }
            AwaitingDecisionType decisionType = decision.getDecisionType();
            if (decisionType == AwaitingDecisionType.CARD_SELECTION
                    || decisionType == AwaitingDecisionType.ARBITRARY_CARDS) {
                String[] parts = responseKey.split(",");
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty()) {
                        blocked.add(trimmed);
                    }
                }
            } else {
                blocked.add(responseKey);
                if (!response.equals(responseKey)) {
                    blocked.add(response);
                }
            }
        }
    }

    private String getTrackingResponse(AwaitingDecision decision, Map<String, String[]> params, String response) {
        if (response == null) {
            return "";
        }
        if (decision == null || decision.getDecisionType() == null || params == null) {
            return response;
        }
        AwaitingDecisionType decisionType = decision.getDecisionType();
        int index = parseResponseIndex(response);
        String text = "";
        switch (decisionType) {
            case ACTION_CHOICE:
            case CARD_ACTION_CHOICE:
                text = lower(getAtIndex(params.get("actionText"), index));
                if (text.isEmpty()) {
                    text = lower(getAtIndex(params.get("cardId"), index));
                }
                if (text.isEmpty()) {
                    text = lower(getAtIndex(params.get("blueprintId"), index));
                }
                if (isPassLike(text)) {
                    return "";
                }
                return !text.isEmpty() ? text : response;
            case MULTIPLE_CHOICE:
                String[] choices = firstNonNull(params, "index", "choice", "results");
                text = lower(getAtIndex(choices, index));
                if (isPassLike(text)) {
                    return "";
                }
                return !text.isEmpty() ? text : response;
            case CARD_SELECTION:
            case ARBITRARY_CARDS:
                return normalizeSelectionResponse(response);
            default:
                return response;
        }
    }

    private int parseResponseIndex(String response) {
        if (response == null) {
            return -1;
        }
        try {
            return Integer.parseInt(response);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String normalizeSelectionResponse(String response) {
        if (response == null || response.isEmpty()) {
            return "";
        }
        String[] parts = response.split(",");
        List<String> cleaned = new ArrayList<String>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                cleaned.add(trimmed);
            }
        }
        return String.join(",", cleaned);
    }

    private boolean isPassResponse(AwaitingDecision decision, Map<String, String[]> params, String response) {
        if (response == null || response.isEmpty()) {
            return true;
        }
        if (decision == null || decision.getDecisionType() == null || params == null) {
            return false;
        }
        AwaitingDecisionType type = decision.getDecisionType();
        String[] values;
        if (type == AwaitingDecisionType.ACTION_CHOICE || type == AwaitingDecisionType.CARD_ACTION_CHOICE) {
            values = params.get("actionText");
        } else if (type == AwaitingDecisionType.MULTIPLE_CHOICE) {
            values = firstNonNull(params, "index", "choice", "results");
        } else {
            return false;
        }
        if (values == null || values.length == 0) {
            return false;
        }
        try {
            int index = Integer.parseInt(response);
            if (index >= 0 && index < values.length) {
                return isPassLike(values[index]);
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    private String buildDecisionKey(String decisionType, String decisionText) {
        String text = decisionText == null ? "" : decisionText;
        if (text.length() > 60) {
            text = text.substring(0, 60);
        }
        return (decisionType == null ? "UNKNOWN" : decisionType) + ":" + text;
    }

    private boolean isBlockedCard(String[] cardIds, int index, Set<String> blocked) {
        if (blocked == null || blocked.isEmpty()) {
            return false;
        }
        if (cardIds == null || index < 0 || index >= cardIds.length) {
            return false;
        }
        String cardId = cardIds[index];
        return cardId != null && blocked.contains(cardId);
    }

    private String[] buildResponseOptions(AwaitingDecision decision, Map<String, String[]> params) {
        if (decision == null || params == null) {
            return null;
        }
        AwaitingDecisionType decisionType = decision.getDecisionType();
        if (decisionType == null) {
            return null;
        }
        switch (decisionType) {
            case MULTIPLE_CHOICE:
                String[] results = firstNonNull(params, "index", "choice", "results");
                if (results == null) {
                    return null;
                }
                String[] indexes = new String[results.length];
                for (int i = 0; i < results.length; i++) {
                    indexes[i] = String.valueOf(i);
                }
                return indexes;
            case ACTION_CHOICE:
            case CARD_ACTION_CHOICE:
                return params.get("actionId");
            case CARD_SELECTION:
            case ARBITRARY_CARDS:
                return params.get("cardId");
            default:
                return null;
        }
    }

    private void updateDecisionTrackerState(GameState gameState, String playerId) {
        if (gameState == null || playerId == null) {
            return;
        }
        int handSize = 0;
        int forcePile = 0;
        int reserveDeck = 0;
        int turn = 0;
        int cardsInPlay = 0;

        try {
            handSize = gameState.getHand(playerId).size();
            forcePile = gameState.getForcePileSize(playerId);
            reserveDeck = gameState.getReserveDeckSize(playerId);
            turn = gameState.getPlayersLatestTurnNumber(playerId);

            for (PhysicalCard card : gameState.getAllPermanentCards()) {
                if (card != null && card.getZone() != null && card.getZone().isInPlay()) {
                    if (playerId.equals(card.getOwner())) {
                        cardsInPlay++;
                    }
                }
            }
        } catch (RuntimeException e) {
            return;
        }

        int previousTurn = currentTurnNumber;
        currentTurnNumber = turn;
        if (turn < previousTurn) {
            recentReassignmentTurns.clear();
            reassignmentCounts.clear();
        }
        pruneReassignmentHistory(turn);

        decisionTracker.updateState(handSize, forcePile, reserveDeck, turn, cardsInPlay);
        currentStateHash = handSize + ":" + forcePile + ":" + reserveDeck + ":" + turn + ":" + cardsInPlay;
        if (!currentStateHash.equals(blockStateHash)) {
            localBlockedResponses.clear();
            recentDecisionResponses.clear();
            lastDecisionRepeatCount = 0;
            blockStateHash = currentStateHash;
        }
    }

    private void pruneReassignmentHistory(int turn) {
        if (turn <= 0 || recentReassignmentTurns.isEmpty()) {
            return;
        }
        int cutoff = turn - RECENT_REASSIGNMENT_TURN_MEMORY;
        List<String> expired = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : recentReassignmentTurns.entrySet()) {
            Integer lastTurn = entry.getValue();
            if (lastTurn == null || lastTurn < cutoff) {
                expired.add(entry.getKey());
            }
        }
        for (String key : expired) {
            recentReassignmentTurns.remove(key);
        }
    }

    private void handleFailedSearchVerification(AwaitingDecision decision, Map<String, String[]> params,
                                               GameState gameState, String playerId) {
        if (!isFailedSearchVerification(decision, params, gameState, playerId)) {
            return;
        }
        if (lastActionChoiceText != null && !lastActionChoiceText.isEmpty()) {
            failedSearchActionTexts.add(lastActionChoiceText);
        }
        if (lastActionChoiceCardId != null && !lastActionChoiceCardId.isEmpty()) {
            failedSearchCardIds.add(lastActionChoiceCardId);
        }
        if (lastActionChoiceBlueprintId != null && !lastActionChoiceBlueprintId.isEmpty()) {
            failedSearchBlueprintIds.add(lastActionChoiceBlueprintId);
        }
    }

    private boolean isFailedSearchVerification(AwaitingDecision decision, Map<String, String[]> params,
                                               GameState gameState, String playerId) {
        if (decision == null || params == null || gameState == null || playerId == null) {
            return false;
        }
        if (decision.getDecisionType() != AwaitingDecisionType.ARBITRARY_CARDS) {
            return false;
        }
        String text = lower(decision.getText());
        if (!text.contains("verify") || !text.contains("unsuccessful attempt")) {
            return false;
        }
        if (!text.contains("reserve deck")) {
            return false;
        }
        if (parseInt(params.get("min"), 0) != 0 || parseInt(params.get("max"), 0) != 0) {
            return false;
        }

        String[] blueprintIds = params.get("blueprintId");
        if (blueprintIds == null) {
            return false;
        }

        List<PhysicalCard> reserveDeck = gameState.getReserveDeck(playerId);
        if (reserveDeck.size() != blueprintIds.length) {
            return false;
        }

        for (int i = 0; i < blueprintIds.length; i++) {
            PhysicalCard card = reserveDeck.get(i);
            if (card == null || card.getBlueprint() == null) {
                return false;
            }
            String blueprintId = card.getBlueprintId(card.getBlueprint().getCardCategory() != CardCategory.OBJECTIVE);
            if (!blueprintIds[i].equals(blueprintId)) {
                return false;
            }
        }

        return true;
    }

    private void updateLastActionChoiceText(String decisionType, String result, Map<String, String[]> params) {
        if (!"CARD_ACTION_CHOICE".equals(decisionType) && !"ACTION_CHOICE".equals(decisionType)) {
            return;
        }
        if (result == null || result.isEmpty() || params == null) {
            return;
        }
        String[] actionTexts = params.get("actionText");
        String[] cardIds = params.get("cardId");
        String[] blueprintIds = params.get("blueprintId");
        if (actionTexts == null || actionTexts.length == 0) {
            return;
        }
        try {
            int index = Integer.parseInt(result);
            if (index >= 0 && index < actionTexts.length) {
                lastActionChoiceText = lower(actionTexts[index]);
                lastActionChoiceCardId = lower(getAtIndex(cardIds, index));
                String blueprintId = lower(getAtIndex(blueprintIds, index));
                if (!blueprintId.isEmpty() && !"inplay".equals(blueprintId) && !"rules".equals(blueprintId)) {
                    lastActionChoiceBlueprintId = blueprintId;
                } else {
                    lastActionChoiceBlueprintId = "";
                }
            }
        } catch (NumberFormatException e) {
            return;
        }
    }

    private ReserveDeckKnowledge buildReserveDeckKnowledge(GameState gameState, String playerId) {
        if (gameState == null || playerId == null) {
            return ReserveDeckKnowledge.empty();
        }
        List<PhysicalCard> reserveDeck;
        try {
            reserveDeck = gameState.getReserveDeck(playerId);
        } catch (RuntimeException e) {
            return ReserveDeckKnowledge.empty();
        }
        if (reserveDeck == null || reserveDeck.isEmpty()) {
            return ReserveDeckKnowledge.empty();
        }
        List<ReserveDeckCardInfo> cards = new ArrayList<ReserveDeckCardInfo>();
        for (PhysicalCard card : reserveDeck) {
            if (card == null) {
                continue;
            }
            SwccgCardBlueprint blueprint = card.getBlueprint();
            ReserveDeckCardInfo info = buildReserveDeckCardInfo(card, blueprint);
            if (info != null) {
                cards.add(info);
            }
        }
        return cards.isEmpty() ? ReserveDeckKnowledge.empty() : new ReserveDeckKnowledge(cards);
    }

    private boolean shouldAvoidReserveDeckAction(String actionText, ReserveDeckKnowledge reserveDeck) {
        if (actionText == null || actionText.isEmpty()) {
            return false;
        }
        if (reserveDeck.isEmpty()) {
            return false;
        }
        if (!actionText.contains("reserve deck")) {
            return false;
        }
        String target = extractReserveDeckTarget(actionText);
        if (target.isEmpty() || isGenericReserveDeckTarget(target)) {
            return false;
        }
        return !matchesReserveDeckTarget(target, reserveDeck);
    }

    private String extractReserveDeckTarget(String actionText) {
        int reserveIndex = actionText.indexOf("reserve deck");
        if (reserveIndex < 0) {
            return "";
        }
        int forIndex = actionText.indexOf(" for ", reserveIndex);
        if (forIndex >= 0) {
            String after = actionText.substring(forIndex + 5).trim();
            return stripSuffix(after, "into hand").trim();
        }
        int fromIndex = actionText.lastIndexOf(" from ", reserveIndex);
        int endIndex = fromIndex >= 0 ? fromIndex : reserveIndex;

        int startIndex = -1;
        int startLength = 0;
        for (String verb : RESERVE_DECK_ACTION_VERBS) {
            int idx = actionText.lastIndexOf(verb, endIndex);
            if (idx >= 0 && idx + verb.length() > startIndex + startLength) {
                startIndex = idx;
                startLength = verb.length();
            }
        }
        if (startIndex < 0) {
            return "";
        }
        String target = actionText.substring(startIndex + startLength, endIndex).trim();
        target = stripSuffix(target, "into hand");
        target = stripSuffix(target, "into your hand");
        target = stripSuffix(target, "to hand");
        return target.trim();
    }

    private String stripSuffix(String text, String suffix) {
        if (text.endsWith(suffix)) {
            return text.substring(0, text.length() - suffix.length()).trim();
        }
        return text;
    }

    private boolean isGenericReserveDeckTarget(String target) {
        String normalized = normalizeText(target);
        if (normalized.isEmpty()) {
            return true;
        }
        String trimmed = removeStopWords(normalized);
        return trimmed.isEmpty() || trimmed.equals("card") || trimmed.equals("cards");
    }

    private String normalizeText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        StringBuilder normalized = new StringBuilder(text.length());
        boolean lastWasSpace = false;
        for (int i = 0; i < text.length(); i++) {
            char c = Character.toLowerCase(text.charAt(i));
            if (Character.isLetterOrDigit(c)) {
                normalized.append(c);
                lastWasSpace = false;
            } else if (!lastWasSpace) {
                normalized.append(' ');
                lastWasSpace = true;
            }
        }
        return normalized.toString().trim();
    }

    private boolean matchesReserveDeckTarget(String target, ReserveDeckKnowledge reserveDeck) {
        String cleaned = stripParentheticals(target);
        List<String> alternatives = splitAlternatives(cleaned);
        for (String alt : alternatives) {
            String normalized = normalizeText(alt);
            if (normalized.isEmpty()) {
                continue;
            }
            String gameTextPhrase = extractGameTextPhrase(alt);
            String meaningful = removeStopWords(normalized);
            if (!gameTextPhrase.isEmpty()) {
                meaningful = removePhrase(meaningful, gameTextPhrase);
                meaningful = removeStopWords(meaningful);
            }
            boolean requiresNonInterrupt = containsNonInterrupt(normalized);
            boolean requiresInterrupt = containsInterrupt(normalized, requiresNonInterrupt);
            boolean allowTokenMatch = allowsTokenMatch(alt, meaningful);
            boolean requiresSpecificMatch = requiresSpecificMatch(meaningful);

            for (ReserveDeckCardInfo card : reserveDeck.cards) {
                if (requiresNonInterrupt && card.isInterrupt) {
                    continue;
                }
                if (requiresInterrupt && !card.isInterrupt) {
                    continue;
                }
                if (!gameTextPhrase.isEmpty() && !card.gameText.contains(gameTextPhrase)) {
                    continue;
                }
                if (!requiresSpecificMatch) {
                    return true;
                }
                if (matchesAnyTitle(meaningful, card.titles)) {
                    return true;
                }
                if (allowTokenMatch && matchesTokenPhrase(meaningful, card.tokens)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> splitAlternatives(String target) {
        List<String> parts = new ArrayList<String>();
        String lower = target.toLowerCase(Locale.ROOT);
        int start = 0;
        int idx;
        while ((idx = lower.indexOf(" or ", start)) >= 0) {
            parts.add(target.substring(start, idx));
            start = idx + 4;
        }
        parts.add(target.substring(start));
        return parts;
    }

    private boolean matchesAnyTitle(String normalizedTarget, Set<String> titles) {
        for (String title : titles) {
            if (title.equals(normalizedTarget)
                    || title.contains(normalizedTarget)
                    || normalizedTarget.contains(title)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesTokenPhrase(String normalizedTarget, Set<String> tokens) {
        if (tokens.isEmpty()) {
            return false;
        }
        if (tokens.contains(normalizedTarget)) {
            return true;
        }
        for (String token : tokens) {
            if (token.contains(normalizedTarget) || normalizedTarget.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private boolean allowsTokenMatch(String rawTarget, String normalizedTarget) {
        String lower = rawTarget.toLowerCase(Locale.ROOT);
        if (lower.startsWith("a ")
                || lower.startsWith("an ")
                || lower.startsWith("any ")
                || lower.startsWith("your ")
                || lower.startsWith("opponent")
                || lower.contains(" any ")) {
            return true;
        }
        return isKnownFilterToken(normalizedTarget);
    }

    private boolean isKnownFilterToken(String normalizedTarget) {
        if (normalizedTarget == null || normalizedTarget.isEmpty()) {
            return false;
        }
        String padded = " " + normalizedTarget + " ";
        for (String token : RESERVE_DECK_FILTER_TOKENS) {
            if (padded.contains(" " + token + " ")) {
                return true;
            }
        }
        return false;
    }

    private String extractGameTextPhrase(String rawTarget) {
        String lower = rawTarget.toLowerCase(Locale.ROOT);
        int gameTextIndex = lower.indexOf("game text");
        if (gameTextIndex < 0) {
            return "";
        }
        String phrase = extractQuotedPhrase(rawTarget);
        if (phrase.isEmpty()) {
            int withIndex = lower.indexOf(" with ");
            int inGameTextIndex = lower.indexOf(" in game text");
            if (withIndex >= 0 && inGameTextIndex > withIndex) {
                phrase = rawTarget.substring(withIndex + 6, inGameTextIndex).trim();
            }
        }
        phrase = stripQuotes(phrase);
        return normalizeText(phrase);
    }

    private String extractQuotedPhrase(String text) {
        int singleQuote = text.indexOf('\'');
        int doubleQuote = text.indexOf('"');
        int quoteIndex = singleQuote >= 0 ? singleQuote : doubleQuote;
        if (singleQuote >= 0 && doubleQuote >= 0) {
            quoteIndex = Math.min(singleQuote, doubleQuote);
        }
        if (quoteIndex < 0) {
            return "";
        }
        char quoteChar = text.charAt(quoteIndex);
        int endQuote = text.indexOf(quoteChar, quoteIndex + 1);
        if (endQuote < 0) {
            return "";
        }
        return text.substring(quoteIndex + 1, endQuote).trim();
    }

    private String stripQuotes(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String trimmed = text.trim();
        if ((trimmed.startsWith("'") && trimmed.endsWith("'"))
                || (trimmed.startsWith("\"") && trimmed.endsWith("\""))) {
            return trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return trimmed;
    }

    private boolean containsNonInterrupt(String normalizedTarget) {
        return normalizedTarget.contains("non interrupt");
    }

    private boolean containsInterrupt(String normalizedTarget, boolean requiresNonInterrupt) {
        if (requiresNonInterrupt) {
            return false;
        }
        return normalizedTarget.contains("interrupt");
    }

    private String stripParentheticals(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        StringBuilder cleaned = new StringBuilder(text.length());
        int depth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') {
                depth++;
                continue;
            }
            if (c == ')') {
                if (depth > 0) {
                    depth--;
                }
                continue;
            }
            if (depth == 0) {
                cleaned.append(c);
            }
        }
        return cleaned.toString();
    }

    private String removeStopWords(String normalizedText) {
        String[] words = normalizedText.split(" ");
        StringBuilder cleaned = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty() || isStopWord(word)) {
                continue;
            }
            if (cleaned.length() > 0) {
                cleaned.append(' ');
            }
            cleaned.append(word);
        }
        return cleaned.toString().trim();
    }

    private String removePhrase(String normalizedText, String phrase) {
        if (normalizedText == null || normalizedText.isEmpty() || phrase == null || phrase.isEmpty()) {
            return normalizedText == null ? "" : normalizedText;
        }
        String cleaned = normalizedText;
        String padded = " " + cleaned + " ";
        String paddedPhrase = " " + phrase + " ";
        if (padded.contains(paddedPhrase)) {
            padded = padded.replace(paddedPhrase, " ");
            cleaned = padded.trim();
        }
        return cleaned;
    }

    private boolean requiresSpecificMatch(String meaningful) {
        if (meaningful == null || meaningful.isEmpty()) {
            return false;
        }
        return !isInterruptOnlyFilter(meaningful);
    }

    private boolean isInterruptOnlyFilter(String meaningful) {
        return "interrupt".equals(meaningful) || "non interrupt".equals(meaningful);
    }

    private boolean isStopWord(String word) {
        for (String stop : RESERVE_DECK_STOP_WORDS) {
            if (stop.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private ReserveDeckCardInfo buildReserveDeckCardInfo(PhysicalCard card, SwccgCardBlueprint blueprint) {
        Set<String> titles = new HashSet<String>();
        if (card != null) {
            for (String title : card.getTitles()) {
                if (title == null) {
                    continue;
                }
                String normalized = normalizeText(title);
                if (!normalized.isEmpty()) {
                    titles.add(normalized);
                }
            }
        }
        if (titles.isEmpty() && blueprint != null && blueprint.getTitle() != null) {
            String normalized = normalizeText(blueprint.getTitle());
            if (!normalized.isEmpty()) {
                titles.add(normalized);
            }
        }

        Set<String> tokens = new HashSet<String>();
        if (blueprint != null) {
            addCategoryToken(tokens, blueprint.getCardCategory());
            addTypeTokens(tokens, blueprint.getCardTypes());
            addKeywordTokens(tokens, blueprint);
        }
        if (card != null && AiCardHelper.isPilot(card)) {
            tokens.add("pilot");
        }

        boolean isInterrupt = blueprint != null && blueprint.getCardCategory() == CardCategory.INTERRUPT;
        String gameText = "";
        if (blueprint != null && blueprint.getGameText() != null) {
            gameText = normalizeText(blueprint.getGameText());
        }

        return new ReserveDeckCardInfo(titles, tokens, isInterrupt, gameText);
    }

    private void addCategoryToken(Set<String> tokens, CardCategory category) {
        if (category == null) {
            return;
        }
        tokens.add(normalizeText(category.getHumanReadable()));
    }

    private void addTypeTokens(Set<String> tokens, Set<CardType> cardTypes) {
        if (cardTypes == null) {
            return;
        }
        for (CardType type : cardTypes) {
            if (type == null) {
                continue;
            }
            switch (type) {
                case JEDI_MASTER:
                case DARK_JEDI_MASTER:
                case JEDI_TEST:
                    tokens.add("jedi");
                    tokens.add(normalizeText(type.getHumanReadable()));
                    break;
                case SITH:
                    tokens.add("sith");
                    tokens.add(normalizeText(type.getHumanReadable()));
                    break;
                default:
                    tokens.add(normalizeText(type.getHumanReadable()));
                    break;
            }
        }
    }

    private void addKeywordTokens(Set<String> tokens, SwccgCardBlueprint blueprint) {
        if (blueprint == null) {
            return;
        }
        for (Keyword keyword : RESERVE_DECK_KEYWORDS) {
            if (blueprint.hasKeyword(keyword)) {
                tokens.add(normalizeText(keyword.getHumanReadable()));
            }
        }
    }

    private static Keyword[] buildReserveDeckKeywords() {
        List<Keyword> keywords = new ArrayList<Keyword>();
        for (Keyword keyword : Keyword.values()) {
            if (keyword.isCharacteristic()) {
                keywords.add(keyword);
            }
        }
        return keywords.toArray(new Keyword[0]);
    }

    private static final String[] RESERVE_DECK_FILTER_TOKENS = buildReserveDeckFilterTokens();

    private static String[] buildReserveDeckFilterTokens() {
        List<String> tokens = new ArrayList<String>();
        tokens.add("character");
        tokens.add("starship");
        tokens.add("vehicle");
        tokens.add("weapon");
        tokens.add("device");
        tokens.add("interrupt");
        tokens.add("effect");
        tokens.add("location");
        tokens.add("site");
        tokens.add("system");
        tokens.add("objective");
        tokens.add("alien");
        tokens.add("droid");
        tokens.add("pilot");
        tokens.add("rebel");
        tokens.add("imperial");
        tokens.add("sith");
        tokens.add("jedi");
        for (Keyword keyword : RESERVE_DECK_KEYWORDS) {
            String token = normalizeStaticText(keyword.getHumanReadable());
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens.toArray(new String[0]);
    }

    private static String normalizeStaticText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        StringBuilder normalized = new StringBuilder(text.length());
        boolean lastWasSpace = false;
        for (int i = 0; i < text.length(); i++) {
            char c = Character.toLowerCase(text.charAt(i));
            if (Character.isLetterOrDigit(c)) {
                normalized.append(c);
                lastWasSpace = false;
            } else if (!lastWasSpace) {
                normalized.append(' ');
                lastWasSpace = true;
            }
        }
        return normalized.toString().trim();
    }

    private static final class ReserveDeckKnowledge {
        private final List<ReserveDeckCardInfo> cards;

        private ReserveDeckKnowledge(List<ReserveDeckCardInfo> cards) {
            this.cards = cards;
        }

        private boolean isEmpty() {
            return cards.isEmpty();
        }

        private static ReserveDeckKnowledge empty() {
            return new ReserveDeckKnowledge(Collections.<ReserveDeckCardInfo>emptyList());
        }
    }

    private static final class ReserveDeckCardInfo {
        private final Set<String> titles;
        private final Set<String> tokens;
        private final boolean isInterrupt;
        private final String gameText;

        private ReserveDeckCardInfo(Set<String> titles, Set<String> tokens, boolean isInterrupt, String gameText) {
            this.titles = titles;
            this.tokens = tokens;
            this.isInterrupt = isInterrupt;
            this.gameText = gameText != null ? gameText : "";
        }
    }

    protected int scoreAction(String actionText, String decisionText, Phase phase) {
        int score = 0;
        score += scoreKeywords(actionText, getActionWeights());
        score += scoreKeywords(actionText, getActionPenalties());
        score += scorePhaseBias(phase, actionText);
        if (!decisionText.isEmpty() && actionText.contains(decisionText)) {
            score += 5;
        }
        return score;
    }

    protected int scorePhaseBias(Phase phase, String actionText) {
        if (phase == null) {
            return 0;
        }
        switch (phase) {
            case ACTIVATE:
                return scoreKeywords(actionText, new KeywordWeight[] {
                        new KeywordWeight("activate", 80),
                        new KeywordWeight("take into hand", 20)
                });
            case DEPLOY:
                return scoreKeywords(actionText, new KeywordWeight[] {
                        new KeywordWeight("deploy", 90),
                        new KeywordWeight("play", 30)
                });
            case CONTROL:
                return scoreKeywords(actionText, new KeywordWeight[] {
                        new KeywordWeight("force drain", 120),
                        new KeywordWeight("control", 30)
                });
            case BATTLE:
                return scoreKeywords(actionText, new KeywordWeight[] {
                        new KeywordWeight("battle", 80),
                        new KeywordWeight("weapon", 40),
                        new KeywordWeight("fire", 40),
                        new KeywordWeight("destiny", 20)
                });
            case MOVE:
                return scoreKeywords(actionText, new KeywordWeight[] {
                        new KeywordWeight("move", 80),
                        new KeywordWeight("relocate", 40),
                        new KeywordWeight("transfer", 20)
                });
            case DRAW:
                return scoreKeywords(actionText, new KeywordWeight[] {
                        new KeywordWeight("draw", 80),
                        new KeywordWeight("retrieve", 30)
                });
            default:
                return 0;
        }
    }

    protected int scoreChoice(String decisionText, String choiceText, String[] results) {
        int score = 0;
        score += scoreKeywords(choiceText, getChoiceWeights());
        score += scoreKeywords(choiceText, getChoicePenalties());

        if (decisionText.contains("allow") && decisionText.contains("opponent")) {
            if (choiceText.startsWith("no")) {
                score += 30;
            } else if (choiceText.startsWith("yes")) {
                score -= 30;
            }
        }

        boolean usedPresent = containsChoice(results, "used");
        boolean lostPresent = containsChoice(results, "lost");
        if (usedPresent && lostPresent) {
            if (choiceText.contains("used")) {
                score += 30;
            }
            if (choiceText.contains("lost")) {
                score -= 30;
            }
        }

        return score;
    }

    private boolean containsChoice(String[] results, String token) {
        if (results == null) {
            return false;
        }
        String needle = token.toLowerCase(Locale.ROOT);
        for (String result : results) {
            if (result != null && result.toLowerCase(Locale.ROOT).contains(needle)) {
                return true;
            }
        }
        return false;
    }

    protected int scoreCard(String decisionText, String cardText) {
        if (cardText == null || cardText.isEmpty()) {
            return 0;
        }
        String lowerCardText = cardText.toLowerCase(Locale.ROOT);
        int score = 0;
        for (String hint : getCardHints()) {
            boolean decisionWants = decisionText.contains(hint);
            boolean cardMatches = lowerCardText.contains(hint);
            if (decisionWants && cardMatches) {
                score += 25;
            } else if (decisionWants && !cardMatches) {
                score -= 5;
            }
        }
        return score;
    }

    protected boolean isOptionalDecision(String decisionText) {
        return decisionText.contains("optional") || decisionText.contains("may") || decisionText.contains("up to");
    }

    private String combine(String[] cardText, String[] testingText, String[] backSideTestingText, int index) {
        String first = getAtIndex(cardText, index);
        String second = getAtIndex(testingText, index);
        String third = getAtIndex(backSideTestingText, index);
        String combined = (first + " " + second + " " + third).trim();
        return combined.isEmpty() ? "" : combined;
    }

    protected int scoreKeywords(String text, KeywordWeight[] weights) {
        if (text == null || text.isEmpty() || weights == null) {
            return 0;
        }
        int score = 0;
        for (KeywordWeight weight : weights) {
            if (text.contains(weight.keyword)) {
                score += weight.score;
            }
        }
        return score;
    }

    protected static final class KeywordWeight {
        private final String keyword;
        private final int score;

        public KeywordWeight(String keyword, int score) {
            this.keyword = keyword;
            this.score = score;
        }
    }
}
