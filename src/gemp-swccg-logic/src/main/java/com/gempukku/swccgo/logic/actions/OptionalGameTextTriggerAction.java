package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;

/**
 * An action that is for optional triggers as part of a card's game text.
 */
public class OptionalGameTextTriggerAction extends AbstractGameTextAction implements TriggerAction {
    private String _triggerIdentifierUsingCardId;
    private String _triggerIdentifierUsingBlueprintId;
    private boolean _repeatableTrigger;

    /**
     * Creates an optional trigger action with the specified card as the source.
     * @param physicalCard the card
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     */
    public OptionalGameTextTriggerAction(PhysicalCard physicalCard, int gameTextSourceCardId) {
        this(physicalCard, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Creates an optional trigger action with the specified card as the source.
     * @param physicalCard the card
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     */
    public OptionalGameTextTriggerAction(PhysicalCard physicalCard, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        this(physicalCard, physicalCard.getOwner(), gameTextSourceCardId, gameTextActionId);

        if (physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION)
            throw new UnsupportedOperationException(GameUtils.getFullName(physicalCard) + " should explicitly indicate performing player");
    }

    /**
     * Creates an optional trigger action with the specified card as the source and performed by the specified player.
     * @param physicalCard the card
     * @param performingPlayer the player
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     */
    public OptionalGameTextTriggerAction(PhysicalCard physicalCard, String performingPlayer, int gameTextSourceCardId) {
        this(physicalCard, performingPlayer, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Creates an optional trigger action with the specified card as the source and performed by the specified player.
     * @param physicalCard the card
     * @param performingPlayer the player
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     */
    public OptionalGameTextTriggerAction(PhysicalCard physicalCard, String performingPlayer, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        super(physicalCard, performingPlayer, gameTextSourceCardId, gameTextActionId);
        _triggerIdentifierUsingCardId = physicalCard.getCardId()+"|"+performingPlayer+"|"+gameTextSourceCardId+"|"+ gameTextActionId;
        _triggerIdentifierUsingBlueprintId = physicalCard.getBlueprintId(true)+"|"+performingPlayer+"|"+gameTextSourceCardId+"|"+ gameTextActionId;
        _text = "Optional response from " + GameUtils.getCardLink(physicalCard);
        _initiationMessage = performingPlayer + " initiates " + GameUtils.getCardLink(physicalCard) + " optional response";
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
        return false;
    }

    @Override
    public Type getType() {
        return Type.GAME_TEXT_TRIGGER;
    }
}
