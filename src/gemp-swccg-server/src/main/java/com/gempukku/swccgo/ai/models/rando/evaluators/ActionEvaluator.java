package com.gempukku.swccgo.ai.models.rando.evaluators;

import com.gempukku.swccgo.ai.models.rando.RandoLogger;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Base class for action evaluators.
 *
 * Each evaluator implements logic for scoring actions in a specific context
 * (e.g., deploy phase, battle decisions, card selection).
 *
 * Evaluators are composable - multiple can score the same action and
 * their scores are combined.
 */
public abstract class ActionEvaluator {
    protected final String name;
    protected boolean enabled = true;
    protected final Logger logger = RandoLogger.getEvaluatorLogger();

    protected ActionEvaluator(String name) {
        this.name = name;
    }

    /**
     * Check if this evaluator applies to the given context.
     *
     * @param context the decision context
     * @return true if this evaluator should score actions for this decision
     */
    public abstract boolean canEvaluate(DecisionContext context);

    /**
     * Evaluate all possible actions and return scored list.
     *
     * @param context Decision context with game state and available actions
     * @return List of evaluated actions with scores and reasoning
     */
    public abstract List<EvaluatedAction> evaluate(DecisionContext context);

    /**
     * Log evaluation for debugging.
     */
    protected void logEvaluation(EvaluatedAction action) {
        logger.debug("  [{}] {}: {} - {}", name, action.getDisplayText(),
                    action.getScore(), action.getReasoningString());
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
