package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Starfighter
 * Title: DFS Squadron Starfighter
 */
public class Card14_115 extends AbstractStarfighter {
    public Card14_115() {
        super(Side.DARK, 2, 1, 2, null, 2, null, 3, "DFS Squadron Starfighter", Uniqueness.RESTRICTED_3, ExpansionSet.THEED_PALACE, Rarity.C);
        setLore("Starfighters assigned to DFS Squadron are provided with advanced targeting routines to assist them in protecting Trade Federation command ships.");
        setGameText("Deploys -1 to Naboo or same system as your battleship. While at same system as your battleship, may deploy Droid Starfighter Laser Cannons aboard from Reserve Deck; reshuffle.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.TRADE_FEDERATION, Icon.PILOT, Icon.PRESENCE);
        addKeywords(Keyword.DFS_SQUADRON, Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.DROID_STARFIGHTER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.or(Filters.Deploys_at_Naboo,
                Filters.sameSystemAs(self, Filters.and(Filters.your(self), Filters.battleship)))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DFS_SQUADRON_STARFIGHTER__DOWNLOAD_DROID_STARFIGHTER_LASER_CANNONS;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.isAtLocation(game, self, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.battleship)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Droid Starfighter Laser Cannons aboard " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Droid_Starfighter_Laser_Cannons, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
