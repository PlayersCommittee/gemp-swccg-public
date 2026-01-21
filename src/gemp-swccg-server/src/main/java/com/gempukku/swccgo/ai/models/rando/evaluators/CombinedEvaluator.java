package com.gempukku.swccgo.ai.models.rando.evaluators;

import com.gempukku.swccgo.ai.models.rando.RandoLogger;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Combines multiple evaluators to make a final decision.
 *
 * Each applicable evaluator scores the actions, then we pick the best.
 */
public class CombinedEvaluator {
    private static final Logger LOG = RandoLogger.getEvaluatorLogger();
    private static final float BAD_ACTION_THRESHOLD = -100.0f;

    private final List<ActionEvaluator> evaluators;
    private final Random random = new Random();

    public CombinedEvaluator() {
        this.evaluators = new ArrayList<>();
        initializeEvaluators();
    }

    /**
     * Initialize all available evaluators.
     */
    private void initializeEvaluators() {
        // Add evaluators in priority order

        // Specific evaluators (handled first - they check decision type/text carefully)
        evaluators.add(new ForceActivationEvaluator());  // INTEGER decisions for force activation
        evaluators.add(new DeployEvaluator());           // Deploy decisions
        evaluators.add(new BattleEvaluator());           // Battle initiation
        evaluators.add(new MoveEvaluator());             // Movement decisions
        evaluators.add(new DrawEvaluator());             // Card draw decisions (draw phase only)
        evaluators.add(new CardSelectionEvaluator());    // Card selection from lists

        // General evaluators (catch-all for remaining actions)
        evaluators.add(new ActionTextEvaluator());       // Text-based action scoring
        evaluators.add(new PassEvaluator());             // Pass/cancel option scoring

        LOG.info("Initialized {} evaluators", evaluators.size());
    }

    /**
     * Run all applicable evaluators and return the best action.
     *
     * @param context Decision context
     * @return Best evaluated action, or null if no evaluators apply
     */
    public EvaluatedAction evaluateDecision(DecisionContext context) {
        // Use a map to merge scores for actions with the same ID
        // This prevents a generic evaluator from overriding a specific evaluator's score
        Map<String, EvaluatedAction> actionMap = new HashMap<>();

        for (ActionEvaluator evaluator : evaluators) {
            if (!evaluator.isEnabled()) {
                continue;
            }

            if (evaluator.canEvaluate(context)) {
                LOG.debug("Running evaluator: {}", evaluator.getName());
                List<EvaluatedAction> actions = evaluator.evaluate(context);

                for (EvaluatedAction action : actions) {
                    String actionId = action.getActionId();

                    // Merge with existing action if same ID, otherwise add new
                    if (actionMap.containsKey(actionId)) {
                        EvaluatedAction existing = actionMap.get(actionId);
                        // Merge: add the scores together and combine reasoning
                        existing.mergeFrom(action);
                        LOG.debug("Merged scores for '{}': now {}", actionId, existing.getScore());
                    } else {
                        actionMap.put(actionId, action);
                    }

                    // Log this evaluator's contribution
                    evaluator.logEvaluation(action);
                }
            }
        }

        List<EvaluatedAction> allActions = new ArrayList<>(actionMap.values());

        if (allActions.isEmpty()) {
            LOG.warn("No evaluators produced actions for decision: {}", context.getDecisionText());
            return null;
        }

        // Pick the best action (highest merged score)
        EvaluatedAction bestAction = allActions.stream()
            .max(Comparator.comparing(EvaluatedAction::getScore))
            .orElse(null);

        if (bestAction == null) {
            return null;
        }

        // If ALL actions are terrible, consider passing (50% of the time)
        if (bestAction.getScore() < BAD_ACTION_THRESHOLD) {
            boolean canPass = !context.isNoPass() && context.getMin() == 0;

            if (canPass && random.nextFloat() < 0.5f) {
                LOG.info("All actions bad (best: {}), choosing to PASS", bestAction.getScore());
                EvaluatedAction passAction = new EvaluatedAction(
                    "",  // Empty = pass
                    ActionType.PASS,
                    0.0f,
                    "Pass (all actions were bad)"
                );
                passAction.addReasoning(String.format("Best action was %.1f, deciding to pass instead", bestAction.getScore()));
                return passAction;
            } else if (canPass) {
                LOG.info("All actions bad (best: {}), but taking least-bad action anyway", bestAction.getScore());
            } else {
                LOG.info("All actions bad (best: {}), but MUST choose (noPass={}, min={})",
                        bestAction.getScore(), context.isNoPass(), context.getMin());
            }
        }

        LOG.info("Best action: {} (score: {})", bestAction.getDisplayText(), bestAction.getScore());
        LOG.info("   Reasoning: {}", bestAction.getReasoningString());

        return bestAction;
    }

    /**
     * Check if any evaluator can handle this decision type.
     */
    public boolean canHandle(DecisionContext context) {
        for (ActionEvaluator evaluator : evaluators) {
            if (evaluator.isEnabled() && evaluator.canEvaluate(context)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the list of evaluators (for testing/debugging).
     */
    public List<ActionEvaluator> getEvaluators() {
        return evaluators;
    }

    /**
     * Add a custom evaluator.
     */
    public void addEvaluator(ActionEvaluator evaluator) {
        evaluators.add(evaluator);
    }
}
