package com.gempukku.swccgo.ai.models.rando.evaluators;

import java.util.ArrayList;
import java.util.List;

/**
 * An action that has been scored by evaluators.
 *
 * Represents a possible decision with:
 * - The action to take (action_id or card_id)
 * - Score (higher = better)
 * - Reasoning (for debugging/logging)
 */
public class EvaluatedAction {
    private String actionId;
    private ActionType actionType;
    private float score;
    private List<String> reasoning;

    // Optional metadata
    private String displayText = "";
    private String cardName = "";
    private String blueprintId = "";
    private int deployCost = 0;
    private float expectedValue = 0.0f;

    public EvaluatedAction(String actionId, ActionType actionType, float score, String displayText) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.score = score;
        this.displayText = displayText;
        this.reasoning = new ArrayList<>();
    }

    /**
     * Add reasoning with optional score adjustment.
     *
     * @param reason the reasoning text
     * @param scoreDelta score adjustment (can be 0)
     */
    public void addReasoning(String reason, float scoreDelta) {
        if (scoreDelta != 0) {
            reasoning.add(String.format("%s (%+.1f)", reason, scoreDelta));
            score += scoreDelta;
        } else {
            reasoning.add(reason);
        }
    }

    /**
     * Add reasoning without score adjustment.
     */
    public void addReasoning(String reason) {
        addReasoning(reason, 0.0f);
    }

    /**
     * Merge another action's score and reasoning into this one.
     * Used when multiple evaluators score the same action ID.
     *
     * @param other the other action to merge from
     */
    public void mergeFrom(EvaluatedAction other) {
        if (other == null) return;

        // Add the other action's score to this one
        this.score += other.score;

        // Merge reasoning lists
        this.reasoning.addAll(other.reasoning);

        // Keep the more specific action type if this one is UNKNOWN
        if (this.actionType == ActionType.UNKNOWN && other.actionType != ActionType.UNKNOWN) {
            this.actionType = other.actionType;
        }

        // Use the more descriptive display text if this one is empty
        if ((this.displayText == null || this.displayText.isEmpty()) &&
            other.displayText != null && !other.displayText.isEmpty()) {
            this.displayText = other.displayText;
        }
    }

    // Getters and setters
    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public List<String> getReasoning() {
        return reasoning;
    }

    public String getReasoningString() {
        return String.join(" | ", reasoning);
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getBlueprintId() {
        return blueprintId;
    }

    public void setBlueprintId(String blueprintId) {
        this.blueprintId = blueprintId;
    }

    public int getDeployCost() {
        return deployCost;
    }

    public void setDeployCost(int deployCost) {
        this.deployCost = deployCost;
    }

    public float getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(float expectedValue) {
        this.expectedValue = expectedValue;
    }

    @Override
    public String toString() {
        return String.format("EvaluatedAction(id=%s, score=%.1f, %s)", actionId, score, displayText);
    }
}
