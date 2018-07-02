package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.logic.timing.Action;

/**
 * An interface to define the methods that trigger actions need to implement.
 */
public interface TriggerAction extends Action {

    /**
     * Gets an identifier for this trigger action, which is used to tell if the trigger action has already been performed.
     * The identifier by default uses the card id as part of it, so it is unique per card on the table, but it certain cases
     * the caller may only want to know if it unique per card blueprint (same printed card face). The value of useBlueprintId
     * when this method is chooses which type for id is requested.
     * @param useBlueprintId true if the trigger id should use the blueprint id instead of the card id
     * @return the identifier
     */
    String getTriggerIdentifier(boolean useBlueprintId);

    /**
     * Determines if the trigger action is allowed to respond multiple times to the same trigger. The default is false.
     * @return true if trigger action is allowed multiple times to the same trigger, otherwise false
     */
    boolean isRepeatableTrigger();

    /**
     * Determines if the trigger action should only be shown once even if different effect results can cause a response. The default is false.
     * @return true if trigger action is only shown once even if could respond to different effect results, otherwise false
     */
    boolean isSingletonTrigger();
}
