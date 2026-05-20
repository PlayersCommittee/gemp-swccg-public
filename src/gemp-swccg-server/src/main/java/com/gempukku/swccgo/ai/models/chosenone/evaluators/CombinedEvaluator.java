package com.gempukku.swccgo.ai.models.chosenone.evaluators;

import com.gempukku.swccgo.ai.models.chosenone.ChosenOneLogger;
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
    private static final Logger LOG = ChosenOneLogger.getEvaluatorLogger();
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

        // V67bc DPS HIERARCHY WALK (mirrors Rando's V67bc)
        java.util.List<java.util.Set<String>> buckets = context.getStepBuckets();
        java.util.List<String> bucketLabels = context.getStepBucketLabels();
        if (buckets != null && !buckets.isEmpty()) {
            for (int b = 0; b < buckets.size(); b++) {
                java.util.Set<String> bucket = buckets.get(b);
                String label = (bucketLabels != null && b < bucketLabels.size()) ? bucketLabels.get(b) : ("step#" + b);
                List<EvaluatedAction> bucketActions = new ArrayList<>();
                for (EvaluatedAction ea : allActions) {
                    if (bucket.contains(ea.getActionId())) {
                        bucketActions.add(ea);
                    }
                }
                if (bucketActions.isEmpty()) {
                    LOG.warn("V67bc DPS WALK: step={} bucket has 0 scored actions, skipping", label);
                    continue;
                }
                EvaluatedAction bestInBucket = bucketActions.stream()
                    .max(Comparator.comparing(EvaluatedAction::getScore))
                    .orElse(null);
                if (bestInBucket == null) continue;
                LOG.warn("V67bc DPS WALK: step={} best={} score={}",
                    label, bestInBucket.getDisplayText(), bestInBucket.getScore());
                if (bestInBucket.getScore() >= BAD_ACTION_THRESHOLD) {
                    LOG.warn("V67bc DPS WALK: step={} viable → picking '{}' (score {})",
                        label, bestInBucket.getDisplayText(), bestInBucket.getScore());
                    return bestInBucket;
                }
                LOG.warn("V67bc DPS WALK: step={} all bad (best score {}) → falling through to next step",
                    label, bestInBucket.getScore());
            }
            LOG.warn("V67bc DPS WALK: every bucket all-bad → PASS");
            EvaluatedAction passAction = new EvaluatedAction(
                "",
                ActionType.PASS,
                0.0f,
                "V67bc DPS: every hierarchy step had only bad-scored actions, passing");
            passAction.addReasoning(context.getAllowedActionsReason() != null
                ? context.getAllowedActionsReason() : "DPS hierarchy exhausted");
            return passAction;
        }

        // Legacy single-set filter
        java.util.Set<String> allowed = context.getAllowedActionIds();
        if (allowed != null) {
            int before = allActions.size();
            List<EvaluatedAction> filtered = new ArrayList<>();
            for (EvaluatedAction ea : allActions) {
                if (allowed.contains(ea.getActionId())) {
                    filtered.add(ea);
                }
            }
            LOG.warn("V67ax DPS FILTER (legacy): {}/{} actions allowed (reason: {})",
                filtered.size(), before, context.getAllowedActionsReason());
            if (!filtered.isEmpty()) {
                allActions = filtered;
            } else if (allowed.isEmpty()) {
                LOG.warn("V67ax DPS FILTER: empty allowed set → PASS");
                EvaluatedAction passAction = new EvaluatedAction(
                    "",
                    ActionType.PASS,
                    0.0f,
                    "V67ax DPS: no step qualified, passing");
                passAction.addReasoning(context.getAllowedActionsReason() != null
                    ? context.getAllowedActionsReason() : "DPS empty allowed");
                return passAction;
            } else {
                LOG.warn("V67ax DPS FILTER: allowed set non-empty but no scored action matched — falling back to unfiltered");
            }
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

            // V24.5: No randomness — always pass when all actions are bad
            if (canPass) {
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
