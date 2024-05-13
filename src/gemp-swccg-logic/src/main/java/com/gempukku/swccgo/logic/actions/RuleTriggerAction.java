package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.SnapshotData;

/**
 * An abstract action that has the base implementation for all game rule trigger actions.
 */
public abstract class RuleTriggerAction extends AbstractAction implements TriggerAction {
    protected PhysicalCard _physicalCard;
    protected boolean _sentInitiationMessage;
    protected String _initiationMessage;
    protected String _text;
    protected String _triggerIdentifierUsingCardId;
    protected String _triggerIdentifierUsingBlueprintId;
    private boolean _singletonTrigger;

    /**
     * Needed to generate snapshot.
     */
    public RuleTriggerAction() {
    }

    @Override
    public void generateSnapshot(Action selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        RuleTriggerAction snapshot = (RuleTriggerAction) selfSnapshot;

        snapshot._physicalCard = snapshotData.getDataForSnapshot(_physicalCard);
        snapshot._sentInitiationMessage = _sentInitiationMessage;
        snapshot._initiationMessage = _initiationMessage;
        snapshot._text = _text;
        snapshot._triggerIdentifierUsingCardId = _triggerIdentifierUsingCardId;
        snapshot._triggerIdentifierUsingBlueprintId = _triggerIdentifierUsingBlueprintId;
        snapshot._singletonTrigger = _singletonTrigger;
    }

    @Override
    public PhysicalCard getActionSource() {
        return null;
    }

    @Override
    public PhysicalCard getActionAttachedToCard() {
        return _physicalCard;
    }

    @Override
    public String getText() {
        return _text;
    }

    /**
     * Sets the text shown for the action selection on the User Interface
     * @param text the text to show for the action selection
     */
    public void setText(String text) {
        _text = text;
    }

    /**
     * Sets the initiation message and card animation to be skipped.
     */
    public void skipInitialMessageAndAnimation() {
        _sentInitiationMessage = true;
    }

    /**
     * Sets the message shown on the User Interface when the action is initiated.
     * @param message the message to show
     */
    public void setMessage(String message) {
        _initiationMessage = message;
    }

    /**
     * Gets an identifier for this trigger action, which is used to tell if the trigger action has already been performed.
     * The identifier by default uses the card id as part of it, so it is unique per card on the table, but it certain cases
     * the caller may only want to know if it unique per card blueprint (same printed card face). The value of useBlueprintId
     * when this method is chooses which type for id is requested.
     * @param useBlueprintId true if the trigger id should use the blueprint id instead of the card id
     * @return the identifier
     */
    public String getTriggerIdentifier(boolean useBlueprintId) {
        return useBlueprintId ? _triggerIdentifierUsingBlueprintId : _triggerIdentifierUsingCardId;
    }

    /**
     * Determines if the trigger action is allowed to respond multiple times to the same trigger. The default is false.
     * @return true if trigger action is allowed multiple times to the same trigger, otherwise false
     */
    @Override
    public boolean isRepeatableTrigger() {
        return false;
    }

    /**
     * Determines if the trigger action should only be shown once even if different effect results can cause a response. The default is false.
     * @return true if trigger action is only shown once even if could respond to different effect results, otherwise false
     */
    @Override
    public boolean isSingletonTrigger() {
        return _singletonTrigger;
    }

    /**
     * Sets whether the trigger action should only be shown once even if different effect results can cause a response. The default is false.
     */
    public void setSingletonTrigger(boolean singleton) {
        _singletonTrigger = singleton;
    }

    @Override
    public Type getType() {
        return Type.RULE_TRIGGER;
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!_sentInitiationMessage) {
            _sentInitiationMessage = true;
            if (_physicalCard != null && _physicalCard.getZone().isInPlay())
                game.getGameState().activatedCard(getPerformingPlayer(), _physicalCard);
            if (_initiationMessage != null && _physicalCard!=null && !_physicalCard.getZone().isFaceDown())
                game.getGameState().sendMessage(_initiationMessage);
        }

        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }
}
