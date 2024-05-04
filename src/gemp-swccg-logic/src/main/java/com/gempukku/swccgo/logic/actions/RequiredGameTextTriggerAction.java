package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;

/**
 * An action that is for required triggers as part of a card's game text.
 */
public class RequiredGameTextTriggerAction extends AbstractGameTextAction implements TriggerAction {
    private String _triggerIdentifierUsingCardId;
    private String _triggerIdentifierUsingBlueprintId;
    private boolean _singletonTrigger;
    private boolean _repeatableTrigger;

    /**
     * Creates a required trigger action with the specified card as the source.
     * @param physicalCard the card
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     */
    public RequiredGameTextTriggerAction(PhysicalCard physicalCard, int gameTextSourceCardId) {
        this(physicalCard, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Creates a required trigger action with the specified card as the source.
     * @param physicalCard the card
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     */
    public RequiredGameTextTriggerAction(PhysicalCard physicalCard, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        super(physicalCard, null, gameTextSourceCardId, gameTextActionId);
        _triggerIdentifierUsingCardId = physicalCard.getCardId()+"||"+gameTextSourceCardId+"|"+ gameTextActionId;
        _triggerIdentifierUsingBlueprintId = physicalCard.getBlueprintId(true)+"||"+gameTextSourceCardId+"|"+ gameTextActionId;
        _text = "Required response from " + GameUtils.getCardLink(physicalCard);
        _initiationMessage = GameUtils.getCardLink(physicalCard) + " required response is initiated";
    }

    /**
     * Gets an identifier for this trigger action, which is used to tell if the trigger action has already been performed.
     * The identifier by default uses the card id as part of it, so it is unique per card on the table, but it certain cases
     * the caller may only want to know if it unique per card blueprint (same printed card face). The value of useBlueprintId
     * when this method is chooses which type for id is requested.
     * @param useBlueprintId true if the trigger id should use the blueprint id instead of the card id
     * @return the identifier
     */
    @Override
    public String getTriggerIdentifier(boolean useBlueprintId) {
        return useBlueprintId ? _triggerIdentifierUsingBlueprintId : _triggerIdentifierUsingCardId;
    }

    /**
     * Determines if the trigger action is allowed to respond multiple times to the same trigger. The default is false.
     * @return true if trigger action is allowed multiple times to the same trigger, otherwise false
     */
    @Override
    public boolean isRepeatableTrigger() {
        return _repeatableTrigger;
    }

    /**
     * Sets whether the trigger action is allowed to respond multiple times to the same trigger. The default is false.
     */
    public void setRepeatableTrigger(boolean repeatable) {
        _repeatableTrigger = repeatable;
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
    @Override
    public void setSingletonTrigger(boolean singleton) {
        _singletonTrigger = singleton;
    }

    @Override
    public Type getType() {
        return Type.GAME_TEXT_TRIGGER;
    }
}
