package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: Site
 * Title: Lothal: Jedi Temple
 */
public class Card219_041 extends AbstractSite {
    public Card219_041() {
        super(Side.LIGHT, Title.Lothal_Jedi_Temple, Title.Lothal, Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setLocationDarkSideGameText("");
        setLocationLightSideGameText("Ezra and Kanan deploy -1 here. " +
                                     "Unless you occupy, Vader may not deploy here and opponent's characters, vehicles, " +
                                     "and starships deploy and move to here for +2 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 3);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.or(Filters.Ezra, Filters.Kanan),  -1, self));
        Condition unlessYouOccupyCondition = new UnlessCondition(new OccupiesCondition(playerOnLightSideOfLocation, self));
        Filter opponentsCharacterVehicleOrStarship = Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.or(Filters.character, Filters.vehicle, Filters.starship));
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.Vader, unlessYouOccupyCondition, self));
        modifiers.add(new DeployCostToLocationModifier(self, opponentsCharacterVehicleOrStarship, unlessYouOccupyCondition,2, self));
        modifiers.add(new MoveCostToLocationModifier(self, opponentsCharacterVehicleOrStarship, unlessYouOccupyCondition, 2, self));
        return modifiers;
    }
}
