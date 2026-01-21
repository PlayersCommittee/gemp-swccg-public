package com.gempukku.swccgo.ai.models.rando.evaluators;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.state.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * Pass Evaluator
 *
 * Simple evaluator that creates a PASS action.
 * Ported from Python base.py PassEvaluator (~210 lines)
 *
 * Used when we want to pass/cancel instead of taking an action.
 * Score is typically low (5-10) unless we really want to pass.
 *
 * IMPORTANT: For ACTION_CHOICE decisions, empty string may not be valid!
 * We need to find a "Cancel" or "Done" action from available options instead.
 */
public class PassEvaluator extends ActionEvaluator {

    // Priority keywords for finding cancel actions (in order of preference)
    private static final String[] CANCEL_KEYWORDS = {
        "cancel", "done", "pass", "decline", "no response", "no further"
    };

    public PassEvaluator() {
        super("Pass");
    }

    @Override
    public boolean canEvaluate(DecisionContext context) {
        // Can only pass if:
        // 1. noPass=false (passing is allowed)
        // 2. AND min=0 (no minimum selection required)
        // 3. For "Required responses", only pass if there's an explicit cancel action
        int minRequired = context.getMin();

        // Basic requirement: noPass must be false and no minimum selection
        if (context.isNoPass() || minRequired > 0) {
            return false;
        }

        // Check for "Required responses" in decision text
        String decisionText = context.getDecisionText();
        if (decisionText != null && decisionText.toLowerCase().contains("required")) {
            // Only allow "passing" if there's a cancel action we can select
            String cancelId = findCancelAction(context);
            if (cancelId == null) {
                return false;  // No cancel option = can't pass on required responses
            }
        }

        return true;
    }

    @Override
    public List<EvaluatedAction> evaluate(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();

        // For ACTION_CHOICE, we may need to use a "Cancel" action instead of empty string
        String passActionId = "";
        String passDisplay = "Pass / Do nothing";

        // Check if this is an ACTION_CHOICE decision with available actions
        if ("ACTION_CHOICE".equals(context.getDecisionType()) && !context.getActionTexts().isEmpty()) {
            String cancelId = findCancelAction(context);
            if (cancelId != null) {
                passActionId = cancelId;
                // Find the display text for this action
                List<String> actionIds = context.getActionIds();
                List<String> actionTexts = context.getActionTexts();
                for (int i = 0; i < actionIds.size(); i++) {
                    if (actionIds.get(i).equals(cancelId) && i < actionTexts.size()) {
                        passDisplay = "Cancel: " + actionTexts.get(i);
                        break;
                    }
                }
                logger.trace("ACTION_CHOICE: Using cancel action '{}' instead of empty string", cancelId);
            }
        }

        EvaluatedAction action = new EvaluatedAction(passActionId, ActionType.PASS, 5.0f, passDisplay);
        action.addReasoning("Default pass option");

        // Get game state for resource checks
        GameState gameState = context.getGameState();
        String decisionTextLower = (context.getDecisionText() != null ? context.getDecisionText() : "").toLowerCase();
        Phase phase = context.getPhase();
        int turnNumber = context.getTurnNumber();

        // === EARLY GAME AGGRESSION ===
        // Turns 1-3, reduce pass preference to encourage action
        float earlyGameMultiplier = turnNumber <= 3 ? 0.5f : 1.0f;
        if (turnNumber <= 3) {
            action.addReasoning("Early game - reduced pass preference", -3.0f);
        }

        // === DECISION-SPECIFIC ADJUSTMENTS ===
        // Don't apply "save force" logic during certain decisions
        boolean isActivateDecision = decisionTextLower.contains("activate");
        boolean isDrawDecision = decisionTextLower.contains("draw") && decisionTextLower.contains("action");
        boolean isControlDecision = phase == Phase.CONTROL && decisionTextLower.contains("control action");
        boolean isInitiateBattleDecision = decisionTextLower.contains("initiate battle");
        boolean isBattlePhaseAction = phase == Phase.BATTLE && decisionTextLower.contains("battle action");
        boolean isFollowthroughDecision = decisionTextLower.contains("choose where to move") ||
                                           decisionTextLower.contains("choose where to deploy");

        // For battle initiation decisions, pass should have VERY low score
        if (isInitiateBattleDecision || isBattlePhaseAction) {
            action.addReasoning("Battle phase - should fight, not pass", -10.0f);
            actions.add(action);
            return actions;
        }

        // For follow-through decisions, pass should also have low score
        if (isFollowthroughDecision) {
            action.addReasoning("Already committed to action - follow through", -15.0f);
            actions.add(action);
            return actions;
        }

        // === RESOURCE-BASED ADJUSTMENTS ===
        if (gameState != null) {
            String playerId = context.getPlayerId();
            int forcePile = context.getForcePileSize();
            int reserveDeck = context.getReserveDeckSize();
            int handSize = context.getHandSize();

            // Low force - prefer to pass (unless activating or drawing)
            if (forcePile < 3 && !isActivateDecision && !isDrawDecision && !isControlDecision) {
                float bonus = 2.0f * earlyGameMultiplier;
                action.addReasoning("Low on Force - prefer to pass", bonus);
            }

            // Low reserve - conserve cards (unless during control phase)
            if (reserveDeck < 10 && !isControlDecision) {
                float bonus = 3.0f * earlyGameMultiplier;
                action.addReasoning("Reserve deck low - conserve cards", bonus);
            }

            // Hand management (unless activating or drawing)
            if (!isActivateDecision && !isDrawDecision) {
                if (handSize < 5) {
                    float bonus = 8.0f * earlyGameMultiplier;
                    action.addReasoning("Small hand (" + handSize + ") - save force for drawing", bonus);
                } else if (handSize < 7) {
                    float bonus = 4.0f * earlyGameMultiplier;
                    action.addReasoning("Hand below target (" + handSize + "/7) - conserve force", bonus);
                }
            }

            // During Move phase, be more conservative to save force for drawing
            if (phase == Phase.MOVE && forcePile <= 4 && handSize < 7) {
                float bonus = 10.0f * earlyGameMultiplier;
                action.addReasoning("Move phase + low force + small hand - pass to draw", bonus);
            }
        }

        actions.add(action);
        return actions;
    }

    /**
     * Find a "Cancel" or "Done" action from available actions.
     *
     * For ACTION_CHOICE decisions, we can't use empty string to pass.
     * Instead, we need to find and select a cancel/done action.
     *
     * @return the action_id of the cancel action, or null if not found
     */
    private String findCancelAction(DecisionContext context) {
        List<String> actionIds = context.getActionIds();
        List<String> actionTexts = context.getActionTexts();

        // Priority 1: Actions that START with cancel/done keywords
        for (int i = 0; i < actionTexts.size(); i++) {
            String textLower = actionTexts.get(i).toLowerCase().trim();
            for (String keyword : CANCEL_KEYWORDS) {
                if (textLower.startsWith(keyword)) {
                    if (i < actionIds.size()) {
                        return actionIds.get(i);
                    }
                }
            }
        }

        // Priority 2: Actions that contain "- cancel" or "- done" patterns
        for (int i = 0; i < actionTexts.size(); i++) {
            String textLower = actionTexts.get(i).toLowerCase();
            if (textLower.contains(" - cancel") || textLower.contains(" - done") || textLower.contains(" - no ")) {
                if (i < actionIds.size()) {
                    return actionIds.get(i);
                }
            }
        }

        // Priority 3: Actions that start with keyword and don't have " or "
        for (int i = 0; i < actionTexts.size(); i++) {
            String textLower = actionTexts.get(i).toLowerCase().trim();
            for (String keyword : CANCEL_KEYWORDS) {
                if (textLower.startsWith(keyword) && !textLower.contains(" or ")) {
                    if (i < actionIds.size()) {
                        return actionIds.get(i);
                    }
                }
            }
        }

        return null;
    }
}
