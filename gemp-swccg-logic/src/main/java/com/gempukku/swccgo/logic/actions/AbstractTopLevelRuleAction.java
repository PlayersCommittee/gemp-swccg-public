package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.SnapshotData;

/**
 * An abstract action that has the base implementation for all game rule card actions.
 * The action classes that extend this class will provide the action process via implementation of the nextEffect method.
 */
public abstract class AbstractTopLevelRuleAction extends AbstractAction {
    protected PhysicalCard _physicalCard;
    protected String _text;

    /**
     * Needed to generate snapshot.
     */
    public AbstractTopLevelRuleAction() {
    }

    @Override
    public void generateSnapshot(Action selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        AbstractTopLevelRuleAction snapshot = (AbstractTopLevelRuleAction) selfSnapshot;

        snapshot._physicalCard = snapshotData.getDataForSnapshot(_physicalCard);
        snapshot._text = _text;
    }

    /**
     * Creates an action with the specified card as the source and the card's owner as the player performing the action.
     * @param physicalCard the card
     */
    public AbstractTopLevelRuleAction(PhysicalCard physicalCard) {
        this(physicalCard, physicalCard.getOwner());

        if (physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION)
            throw new UnsupportedOperationException(GameUtils.getFullName(_physicalCard) + " should explicitly indicate performing player");
    }

    /**
     * Creates an action with the specified card as the source and the specified player as the player performing the action.
     * @param physicalCard the card
     * @param performingPlayer the player
     */
    public AbstractTopLevelRuleAction(PhysicalCard physicalCard, String performingPlayer) {
        _physicalCard = physicalCard;
        setPerformingPlayer(performingPlayer);
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

    @Override
    public Type getType() {
        return Type.RULE_TOP_LEVEL;
    }

    @Override
    public boolean wasCarriedOut() {
        return super.wasCarriedOut() && wasActionCarriedOut();
    }

    /**
     * Determines if the action process fully carried out.
     * The class that implements the nextEffect method should implement this method to do any additional checking as to
     * if the action was fully carried out.
     * @return true if the action was fully carried out, otherwise false
     */
    protected abstract boolean wasActionCarriedOut();
}
