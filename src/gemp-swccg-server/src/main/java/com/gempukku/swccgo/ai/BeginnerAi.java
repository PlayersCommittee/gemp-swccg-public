package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecisionType;

import java.util.Map;

public class BeginnerAi implements SwccgAiController {

    @Override
    public String decide(AwaitingDecision decision, GameState gameState) {
        AwaitingDecisionType type = decision.getDecisionType();
        Map<String, String[]> params = decision.getDecisionParameters();

        switch (type) {
            case EMPTY:
                return "";

            case INTEGER:
                if (params.containsKey("min") && params.get("min").length > 0)
                    return params.get("min")[0];
                return firstNonEmptyValue(params);

            default:
                return firstNonEmptyValue(params);
        }
    }


    private static String firstNonEmptyValue(Map<String, String[]> params) {
        if (params == null) return "";
        for (String[] values : params.values()) {
            if (values != null && values.length > 0 && values[0] != null) {
                return values[0];
            }
        }
        return "";
    }

}
