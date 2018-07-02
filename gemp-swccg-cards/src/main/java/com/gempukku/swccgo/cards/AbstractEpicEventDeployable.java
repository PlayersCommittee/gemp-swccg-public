package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelEpicEventGameTextAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;

import java.util.LinkedList;
import java.util.List;

/**
 * The abstract class providing the common implementation for Epic Events that are deployed.
 */
public abstract class AbstractEpicEventDeployable extends AbstractDeployable {

    /**
     * Creates a blueprint for an Epic Event that is deployed.
     * @param side the side of the Force
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     */
    protected AbstractEpicEventDeployable(Side side, PlayCardZoneOption playCardZoneOption, String title) {
        this(side, playCardZoneOption, title, null);
    }

    /**
     * Creates a blueprint for an Epic Event that is deployed.
     * @param side the side of the Force
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractEpicEventDeployable(Side side, PlayCardZoneOption playCardZoneOption, String title, Uniqueness uniqueness) {
        super(side, 0f, playCardZoneOption, 0f, title, uniqueness);
        setCardCategory(CardCategory.EPIC_EVENT);
        addCardType(CardType.EPIC_EVENT);
        addIcon(Icon.EPIC_EVENT);
    }

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player when the card is active (or only undercover) in play.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelEpicEventGameTextAction> epicEventActions = getEpicEventGameTextTopLevelActions(playerId, game, self, gameTextSourceCardId);
        if (epicEventActions != null) {
            return new LinkedList<TopLevelGameTextAction>(epicEventActions);
        }
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level Epic Event actions that can be performed by the
     * specified player when the Epic Event is active in play.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelEpicEventGameTextAction> getEpicEventGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }
}
