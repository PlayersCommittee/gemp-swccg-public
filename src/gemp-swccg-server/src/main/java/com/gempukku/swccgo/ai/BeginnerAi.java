package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecisionType;

import java.util.Arrays;
import java.util.Map;
import java.util.Locale;

public class BeginnerAi implements SwccgAiController {

    @Override
    public String decide(AwaitingDecision decision, GameState gameState) {
        Map<String, String[]> params = decision.getDecisionParameters();

        if (decision.getDecisionType() == AwaitingDecisionType.EMPTY) {
            return "pass";
        }

        if (params == null || params.isEmpty()) {
            return "0";
        }

        switch (decision.getDecisionType()) {
            case MULTIPLE_CHOICE:
                return pickMultipleChoice(decision, params);
            case ACTION_CHOICE:
            case CARD_ACTION_CHOICE:
                return pickActionChoice(params);
            case CARD_SELECTION:
                return pickCardSelection(params);
            case ARBITRARY_CARDS:
                return pickArbitraryCards(params);
            case INTEGER:
                return pickInteger(decision, params);
            default:
                return "0";
        }
    }

    private String pickMultipleChoice(AwaitingDecision decision, Map<String, String[]> params) {
        String[] results = firstNonNull(params, "index", "choice", "results");
        if (results == null || results.length == 0) {
            return "0";
        }

        // Avoid obvious pass/no responses if there is a more proactive option
        int passIdx = findPassIndex(results);
        if (passIdx != -1 && results.length > 1) {
            for (int i = 0; i < results.length; i++) {
                if (i != passIdx) {
                    return String.valueOf(i);
                }
            }
        }

        // Respect defaultIndex if present and valid
        int defaultIdx = parseInt(params.get("defaultIndex"), 0);
        if (defaultIdx >= 0 && defaultIdx < results.length) {
            return String.valueOf(defaultIdx);
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

    private String pickActionChoice(Map<String, String[]> params) {
        String[] actions = params.get("actionId");
        boolean noPass = Boolean.parseBoolean(first(params.get("noPass"), "false"));

        if (actions == null || actions.length == 0) {
            // No available actions -> pass
            return "";
        }

        // Prefer a non-pass action if available
        String[] actionTexts = params.get("actionText");
        int passIdx = findPassIndex(actionTexts);
        for (int i = 0; i < actions.length; i++) {
            if (i != passIdx) {
                return String.valueOf(i);
            }
        }

        // If only pass remains and it's allowed, pass, otherwise first option
        if (!noPass && passIdx != -1) {
            return String.valueOf(passIdx);
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

    private String pickArbitraryCards(Map<String, String[]> params) {
        String[] cardIds = params.get("cardId");
        String[] selectable = params.get("selectable");
        int min = parseInt(params.get("min"), 0);
        if (cardIds == null || cardIds.length == 0) {
            return "";
        }

        StringBuilder choice = new StringBuilder();
        int selected = 0;
        for (int i = 0; i < cardIds.length && selected < Math.max(min, 1); i++) {
            if (isSelectable(selectable, i)) {
                if (choice.length() > 0) {
                    choice.append(",");
                }
                choice.append(cardIds[i]);
                selected++;
            }
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

        String text = decision.getText() != null ? decision.getText().toLowerCase(Locale.ROOT) : "";
        boolean forceActivation = text.contains("force") && text.contains("activate");

        // If we're asked how much Force to activate/allow, lean aggressive
        int choice = forceActivation ? max : defaultVal;
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

    private int findPassIndex(String[] values) {
        if (values == null) {
            return -1;
        }
        for (int i = 0; i < values.length; i++) {
            String v = values[i];
            if (v == null) continue;
            String lc = v.toLowerCase(Locale.ROOT);
            if (lc.equals("pass") || lc.equals("cancel") || lc.equals("no")) {
                return i;
            }
        }
        return -1;
    }
}
