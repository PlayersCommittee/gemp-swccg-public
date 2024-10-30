package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Great Hutt Expansion
 * Type: Location
 * Subtype: Site
 * Title: Ulress: Ixtal's Garage
 */
public class Card304_090 extends AbstractSite {
    public Card304_090() {
        super(Side.LIGHT, "Ulress: Ixtal's Garage", Title.Ulress, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLocationDarkSideGameText("Your vehicles are forfeit -2 here.");
        setLocationLightSideGameText("Your vehicles deploy -1 here and are power +1.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 0);
        addIcons(Icon.SCOMP_LINK, Icon.EXTERIOR_SITE, Icon.PLANET);
		addKeywords(Keyword.ULRESS_SITE);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter powerPlusOneHere = Filters.vehicle;
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), powerPlusOneHere, Filters.here(self)), 1));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.vehicle, -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter forfeitMinusTwoHere = Filters.vehicle;
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), forfeitMinusTwoHere, Filters.here(self)), -2));
        return modifiers;
    }
}
