package com.gempukku.swccgo.cards.set226.light;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
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

/**
 * Set: Set 26
 * Type: Location
 * Subtype: Site
 * Title: Mapuzo: Underground Corridor
 */
public class Card226_023 extends AbstractSite {
    public Card226_023() {
        super(Side.LIGHT, Title.Underground_Corridor, Title.Mapuzo, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLocationLightSideGameText("During your move phase, Jedi survivors may move from here to a Jabiim site (or to opponent's battleground site).");
        setLocationDarkSideGameText("");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 0);
        addIcons(Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_26);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        Filter jediSurvivorHere = Filters.and(Filters.Jedi_Survivor, Filters.here(self));
        Filter destinationSite = Filters.or(Filters.Jabiim_site, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.battleground_site));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.MOVE)
                && GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, jediSurvivorHere, self, destinationSite, false)) {

            MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, jediSurvivorHere, self, destinationSite, false);
            action.setText("Move Jedi Survivor here to a site");
            actions.add(action);
        }
        return actions;
    } 
}
