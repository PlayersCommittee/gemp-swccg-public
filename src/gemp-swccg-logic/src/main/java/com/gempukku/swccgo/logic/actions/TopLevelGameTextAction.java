package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;

/**
 * An action that is for top-level actions as part of a card's game text.
 */
public class TopLevelGameTextAction extends AbstractGameTextAction {

    /**
     * Needed to generate snapshot.
     */
    public TopLevelGameTextAction() {
    }

    /**
     * Creates a top-level action with the specified card as the source.
     * @param physicalCard the card
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     */
    public TopLevelGameTextAction(PhysicalCard physicalCard, int gameTextSourceCardId) {
        this(physicalCard, physicalCard.getOwner(), gameTextSourceCardId);

        if (physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION)
            throw new UnsupportedOperationException(GameUtils.getFullName(physicalCard) + " should explicitly indicate performing player");
    }

    /**
     * Creates a top-level action with the specified card as the source.
     * @param physicalCard the card
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     */
    public TopLevelGameTextAction(PhysicalCard physicalCard, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        this(physicalCard, physicalCard.getOwner(), gameTextSourceCardId, gameTextActionId);

        if (physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION)
            throw new UnsupportedOperationException(GameUtils.getFullName(physicalCard) + " should explicitly indicate performing player");
    }

    /**
     * Creates a top-level action with the specified card as the source and performed by the specified player.
     * @param physicalCard the card
     * @param performingPlayer the player
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     */
    public TopLevelGameTextAction(PhysicalCard physicalCard, String performingPlayer, int gameTextSourceCardId) {
        super(physicalCard, performingPlayer, gameTextSourceCardId);
        _text = "Top-level action from " + GameUtils.getCardLink(physicalCard);
        _initiationMessage = performingPlayer + " initiates " + GameUtils.getCardLink(physicalCard) + " top-level action";
    }

    /**
     * Creates a top-level action with the specified card as the source and performed by the specified player.
     * @param physicalCard the card
     * @param performingPlayer the player
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     */
    public TopLevelGameTextAction(PhysicalCard physicalCard, String performingPlayer, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        super(physicalCard, performingPlayer, gameTextSourceCardId, gameTextActionId);
        _text = "Top-level action from " + GameUtils.getCardLink(physicalCard);
        _initiationMessage = performingPlayer + " initiates " + GameUtils.getCardLink(physicalCard) + " top-level action";
    }

    @Override
    public Type getType() {
        return Type.GAME_TEXT_TOP_LEVEL;
    }
}
