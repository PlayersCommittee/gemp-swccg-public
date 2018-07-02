package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.rules.Rule;

/**
 * An action that is for optional triggers as part of a game rule.
 */
public class OptionalRuleTriggerAction extends RuleTriggerAction {

    /**
     * Creates an optional trigger action from the specified rule.
     * @param rule the rule
     */
    public OptionalRuleTriggerAction(Rule rule) {
        this(rule, null);
    }

    /**
     * Creates an optional trigger action from the specified rule with the specified card as the source.
     * @param rule the rule
     * @param physicalCard the card
     */
    public OptionalRuleTriggerAction(Rule rule, PhysicalCard physicalCard) {
        String ruleName = rule.getClass().getSimpleName();
        _physicalCard = physicalCard;
        if (physicalCard != null) {
            _triggerIdentifierUsingCardId = physicalCard.getCardId() + "|||" + ruleName;
            _triggerIdentifierUsingBlueprintId = physicalCard.getBlueprintId(true) + "|||" + ruleName;
            _text = "Optional response from " + GameUtils.getCardLink(_physicalCard);
            _initiationMessage = GameUtils.getCardLink(_physicalCard) + " optional response is initiated";
        }
        else {
            _triggerIdentifierUsingCardId = "|||" + ruleName;
            _triggerIdentifierUsingBlueprintId = "|||" + ruleName;
        }
    }
}
