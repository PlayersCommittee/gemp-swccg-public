package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecisionType;

import java.util.Arrays;
import java.util.Map;

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
                return pickFirstIndex(params);
            case ACTION_CHOICE:
            case CARD_ACTION_CHOICE:
                return pickActionChoice(params);
            case CARD_SELECTION:
                return pickCardSelection(params);
            case ARBITRARY_CARDS:
                return pickArbitraryCards(params);
            case INTEGER:
                return pickInteger(params);
            default:
                return "0";
        }
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
        boolean autoPassEligible = Boolean.parseBoolean(first(params.get("autoPassEligible"), "false"));

        if (actions == null || actions.length == 0) {
            // No available actions -> pass
            return "";
        }

        // If passing is allowed and the UI deems this auto-pass eligible, pass to keep play moving
        if (!noPass && autoPassEligible) {
            return "";
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

    private String pickInteger(Map<String, String[]> params) {
        int min = parseInt(params.get("min"), 0);
        int max = parseInt(params.get("max"), min);
        int defaultVal = parseInt(params.get("defaultValue"), min);

        // Normalize bounds
        if (max < min) {
            max = min;
        }

        // Prefer a provided default if valid, otherwise clamp to range
        int choice = defaultVal;
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
}
