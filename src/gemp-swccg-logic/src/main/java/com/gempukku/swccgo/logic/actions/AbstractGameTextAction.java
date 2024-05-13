package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.SnapshotData;

/**
 * An abstract action that has the base implementation for all game text actions.
 */
public abstract class AbstractGameTextAction extends AbstractRespondableAction implements GameTextAction {
    private int _gameTextSourceCardId;
    private GameTextActionId _gameTextActionId;

    /**
     * Needed to generate snapshot.
     */
    public AbstractGameTextAction() {
    }

    @Override
    public void generateSnapshot(Action selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        AbstractGameTextAction snapshot = (AbstractGameTextAction) selfSnapshot;

        // Set each field
        snapshot._gameTextSourceCardId = _gameTextSourceCardId;
        snapshot._gameTextActionId = _gameTextActionId;
    }

    /**
     * Creates an action with the specified card as the source and the specified player as the player performing the action.
     * @param physicalCard the card
     * @param performingPlayer the player
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     */
    public AbstractGameTextAction(PhysicalCard physicalCard, String performingPlayer, int gameTextSourceCardId) {
        super(physicalCard, performingPlayer);
        _gameTextSourceCardId = gameTextSourceCardId;
        _gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_DEFAULT;
    }

    /**
     * Creates an action with the specified card as the source and the specified player as the player performing the action.
     * @param physicalCard the card
     * @param performingPlayer the player
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     */
    public AbstractGameTextAction(PhysicalCard physicalCard, String performingPlayer, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        super(physicalCard, performingPlayer);
        _gameTextSourceCardId = gameTextSourceCardId;
        _gameTextActionId = gameTextActionId;
    }

    /**
     * Determines if the action is from game text.
     * @return true if from game text, otherwise false
     */
    @Override
    public boolean isFromGameText() {
        return true;
    }

    /**
     * Gets the card id of the card the game text is originally from
     * @return the card id
     */
    @Override
    public int getGameTextSourceCardId() {
        return _gameTextSourceCardId;
    }

    /**
     * Gets the game text action id.
     * @return the game text action id
     */
    @Override
    public GameTextActionId getGameTextActionId() {
        return _gameTextActionId;
    }

    /**
     * Sets whether the trigger action should only be shown once even if different effect results can cause a response. The default is false.
     * Note: This is does not set anything
     */
    public void setSingletonTrigger(boolean singleton) {
    }
}
