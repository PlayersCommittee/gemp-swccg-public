package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.SnapshotData;


/**
 * An action that is used to process sub-steps of another action, and shares several of the same values, such as source card.
 * The practical use is for the parent action to put the sub-action on the action stack, which will cause the sub-action
 * to being processing. Then when the sub-action complete, the parent action is on the top of the action stack again.
 */
public class SubAction extends AbstractRespondableAction {
    protected Action _action;

    /**
     * Needed to generate snapshot.
     */
    public SubAction() {
    }

    @Override
    public void generateSnapshot(Action selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        SubAction snapshot = (SubAction) selfSnapshot;

        // Set each field
        snapshot._action = snapshotData.getDataForSnapshot(_action);
    }

    /**
     * Creates a sub-action based on the specified action that can be responded to.
     * @param action the card
     */
    public SubAction(Action action) {
        this(action, action.getPerformingPlayer());
    }

    /**
     * Creates a sub-action based on the specified action that can be responded to.
     * @param action the card
     * @param performingPlayerId the performing player
     */
    public SubAction(Action action, String performingPlayerId) {
        super(action.getActionSource(), performingPlayerId);
        _action = action;
        skipInitialMessageAndAnimation();
    }

    @Override
    public Type getType() {
        return _action.getType();
    }

    /**
     * Determines if the action is from game text.
     * @return true if from game text, otherwise false
     */
    @Override
    public boolean isFromGameText() {
        return _action.isFromGameText();
    }

    /**
     * Determines if the action is from playing an Interrupt.
     * @return true if from playing an Interrupt, otherwise false
     */
    @Override
    public boolean isFromPlayingInterrupt() {
        return _action.isFromPlayingInterrupt();
    }

    /**
     * Gets the card id of the card the game text is originally from
     * @return the card id
     */
    @Override
    public int getGameTextSourceCardId() {
        return _action.getGameTextSourceCardId();
    }

    /**
     * Gets the game text action id.
     * @return the game text action id
     */
    @Override
    public GameTextActionId getGameTextActionId() {
        return _action.getGameTextActionId();
    }

    /**
     * Gets the card that is the source of the action or the card that the action is attached to if the action comes
     * from a game rule.
     *
     * @return the card, or null
     */
    @Override
    public PhysicalCard getActionAttachedToCard() {
        return _action.getActionAttachedToCard();
    }

    /**
     * Gets the card built-in that is the source of the action or null if not associated with a card built-in.
     *
     * @return the card built-in, or null
     */
    @Override
    public SwccgBuiltInCardBlueprint getActionAttachedToCardBuiltIn() {
        return _action.getActionAttachedToCardBuiltIn();
    }

    /**
     * Gets the parent action of this sub-action;
     * @return the parent action
     */
    @Override
    public Action getParentAction() {
        return _action;
    }

    /**
     * Adds an action to the stack that will be performed before any other effects of this SubAction. Tracks if the stacked
     * action is carried out.
     * @param actionToStack the action to stack
     */
    public final void stackSubAction(Action actionToStack) {
        insertEffect(
                new StackActionEffect(this, actionToStack));
    }
}
