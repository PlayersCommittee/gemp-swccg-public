package com.gempukku.swccgo.ai.models.chosenone.evaluators;

import com.gempukku.swccgo.ai.models.chosenone.ChosenOneLogger;
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
    protected final Logger logger = ChosenOneLogger.getEvaluatorLogger();

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

    // =========================================================================
    // V35: Reusable card type detection helpers
    // =========================================================================

    /**
     * Check if a card title (lowercase) is an Inquisitor character.
     */
    protected static boolean isInquisitor(String titleLower) {
        return titleLower.contains("inquisitor")
            || titleLower.contains("fifth brother")
            || titleLower.contains("seventh sister")
            || titleLower.contains("eighth brother")
            || titleLower.contains("grand inquisitor")
            || titleLower.contains("second sister")
            || titleLower.contains("ninth sister")
            || titleLower.contains("third sister");
    }

    /**
     * Check if a card title (lowercase) is a Jedi or Padawan character.
     */
    protected static boolean isJediOrPadawan(String titleLower) {
        return titleLower.contains("jedi")
            || titleLower.contains("padawan")
            || titleLower.contains("luke")
            || titleLower.contains("obi-wan")
            || titleLower.contains("yoda")
            || titleLower.contains("ahsoka")
            || titleLower.contains("ezra")
            || titleLower.contains("kanan")
            || titleLower.contains("rey")
            || titleLower.contains("sabine");
    }
}
