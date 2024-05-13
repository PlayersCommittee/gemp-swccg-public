package com.gempukku.swccgo.logic.effects;


import com.gempukku.swccgo.logic.timing.Action;

/**
 * This class is used when a top-level or response action occurs that would
 * never have anything respond to that action (which is generally the case unless
 * a card is being played or cards are being "targeted" for a specific reason).
 * If the action can be responded to, then the parent class, RespondableEffect should
 * be used instead. Setting a RespondableEffect via the allowResponses method of AbstractRespondableAction
 * is required if any targeting was done by the card. If there is nothing that would ever respond to that action, then
 * the this class can be set with the allowResponses method to avoid unnecessary triggers, etc.
 * The getType() method returns null, so nothing will be triggered from this effect.
 */
public abstract class UnrespondableEffect extends RespondableEffect {

    /**
     * Creates an unrespondable effect
     * @param action the action performing this effect
     */
    public UnrespondableEffect(Action action) {
        super(action);
    }

    @Override
    public Type getType() {
        return null;
    }
}
