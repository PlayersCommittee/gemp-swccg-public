package com.gempukku.swccgo.logic.conditions;


/**
 * A condition that is fulfilled when the included condition is not fulfilled. This has the same meaning as "not".
 */
public class UnlessCondition extends NotCondition {

    /**
     * Creates a condition that is fulfilled when the specified condition is not fulfilled.
     * @param condition the condition
     */
    public UnlessCondition(Condition condition) {
        super(condition);
    }
}
