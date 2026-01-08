package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecisionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Heuristic AI base that scores actions/choices using simple keyword weights.
public abstract class HeuristicAiBase implements SwccgAiController {
    @Override
    public String decide(String playerId, AwaitingDecision decision, GameState gameState) {
        Map<String, String[]> params = decision.getDecisionParameters();

        if (decision.getDecisionType() == AwaitingDecisionType.EMPTY) {
            return "pass";
        }

        if (params == null || params.isEmpty()) {
            return "0";
        }

        switch (decision.getDecisionType()) {
            case MULTIPLE_CHOICE:
                return pickMultipleChoice(playerId, decision, params, gameState);
            case ACTION_CHOICE:
            case CARD_ACTION_CHOICE:
                return pickActionChoice(playerId, decision, params, gameState);
            case CARD_SELECTION:
                return pickCardSelection(params);
            case ARBITRARY_CARDS:
                return pickArbitraryCards(playerId, decision, params, gameState);
            case INTEGER:
                return pickInteger(decision, params);
            default:
                return "0";
        }
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

        int passIdx = findPassIndex(results, false);
        int bestIdx = -1;
        int bestScore = Integer.MIN_VALUE;
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
            if (score > bestScore) {
                bestScore = score;
                bestIdx = i;
            }
        }

        if (bestIdx != -1) {
            return String.valueOf(bestIdx);
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

        Phase phase = gameState != null ? gameState.getCurrentPhase() : null;
        int bestIdx = -1;
        int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < actions.length; i++) {
            String actionText = lower(getAtIndex(actionTexts, i));
            int score = scoreAction(actionText, decisionText, phase)
                    + scoreActionContext(playerId, gameState, decisionText, actionText, phase, params);
            if (i == passIdx) {
                score -= getPassPenalty();
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

        return pickFirstIndex(params);
    }

    private String pickCardSelection(Map<String, String[]> params) {
        String[] cardIds = params.get("cardId");
        int min = parseInt(params.get("min"), 0);
        if (cardIds == null || cardIds.length == 0) {
            return "";
        }
        int pickCount = Math.max(min, 1);
        pickCount = Math.min(pickCount, cardIds.length);
        return String.join(",", Arrays.copyOfRange(cardIds, 0, pickCount));
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

        Collections.sort(candidates, new Comparator<Integer>() {
            @Override
            public int compare(Integer left, Integer right) {
                int leftScore = scoreCard(decisionText, combine(cardText, testingText, backSideTestingText, left))
                        + scoreCardContext(playerId, gameState, decisionText, combine(cardText, testingText, backSideTestingText, left));
                int rightScore = scoreCard(decisionText, combine(cardText, testingText, backSideTestingText, right))
                        + scoreCardContext(playerId, gameState, decisionText, combine(cardText, testingText, backSideTestingText, right));
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

        protected KeywordWeight(String keyword, int score) {
            this.keyword = keyword;
            this.score = score;
        }
    }
}
