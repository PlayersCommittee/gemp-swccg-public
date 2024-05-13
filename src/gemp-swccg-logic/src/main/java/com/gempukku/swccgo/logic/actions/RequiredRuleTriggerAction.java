package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.rules.Rule;

/**
 * An action that is for required triggers as part of a game rule.
 */
public class RequiredRuleTriggerAction extends RuleTriggerAction {

    /**
     * Needed to generate snapshot.
     */
    public RequiredRuleTriggerAction() {
    }

    /**
     * Creates a required trigger action from the specified rule.
     * @param rule the rule
     */
    public RequiredRuleTriggerAction(Rule rule) {
        this(rule, null);
    }

    /**
     * Creates a required trigger action from the specified rule with the specified card as the source.
     * @param rule the rule
     * @param physicalCard the card
     */
    public RequiredRuleTriggerAction(Rule rule, PhysicalCard physicalCard) {
        String ruleName = rule.getClass().getSimpleName();
        _physicalCard = physicalCard;
        if (physicalCard != null) {
            _triggerIdentifierUsingCardId = physicalCard.getCardId() + "|||" + ruleName;
            _triggerIdentifierUsingBlueprintId = physicalCard.getBlueprintId(true) + "|||" + ruleName;
            _text = "Required response from " + GameUtils.getCardLink(_physicalCard);
            _initiationMessage = GameUtils.getCardLink(_physicalCard) + " required response is initiated";
        }
        else {
            _triggerIdentifierUsingCardId = "|||" + ruleName;
            _triggerIdentifierUsingBlueprintId = "|||" + ruleName;
        }
    }
}
