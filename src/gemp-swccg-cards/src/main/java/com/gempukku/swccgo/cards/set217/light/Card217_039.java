package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.AloneAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Location
 * Subtype: Site
 * Title: Kef Bir: Oceanic Wreckage
 */
public class Card217_039 extends AbstractSite {
    public Card217_039() {
        super(Side.LIGHT, "Kef Bir: Oceanic Wreckage", Title.Kef_Bir, Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setLocationDarkSideGameText("While Rey alone here, you may not Force drain at Kylo's location.");
        setLocationLightSideGameText("While Rey alone here, she is immune to attrition.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EPISODE_VII, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.sameLocationAs(self, Filters.Kylo), new AloneAtCondition(self, Filters.Rey, Filters.here(self)), playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.and(Filters.Rey, Filters.alone, Filters.here(self))));
        return modifiers;
    }
}