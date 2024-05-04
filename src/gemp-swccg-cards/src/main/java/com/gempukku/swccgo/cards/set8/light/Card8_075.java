package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Location
 * Subtype: Site
 * Title: Endor: Hidden Forest Trail
 */
public class Card8_075 extends AbstractSite {
    public Card8_075() {
        super(Side.LIGHT, Title.Hidden_Forest_Trail, Title.Endor, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.U);
        setLocationDarkSideGameText("If you occupy, opponent's Hidden Forest Trail game text is canceled.");
        setLocationLightSideGameText("During your move phase, your scouts may move between here and any exterior Endor site.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Hidden_Forest_Trail,
                new OccupiesCondition(playerOnDarkSideOfLocation, self), game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        Filter otherExteriorEndorSite = Filters.and(Filters.other(self), Filters.exterior_Endor_site);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.MOVE)
                && GameConditions.canSpotLocation(game, otherExteriorEndorSite)) {
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.scout, self, otherExteriorEndorSite, false)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, Filters.scout, self, otherExteriorEndorSite, false);
                action.setText("Move from here to exterior Endor site");
                actions.add(action);
            }
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.scout, Filters.exterior_Endor_site, self, false)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, Filters.scout, otherExteriorEndorSite, self, false);
                action.setText("Move from exterior Endor site to here");
                actions.add(action);
            }
        }
        return actions;
    }
}