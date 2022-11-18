package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Location
 * Subtype: System
 * Title: Scarif
 */
public class Card209_023 extends AbstractSystem {
    public Card209_023() {
        super(Side.LIGHT, Title.Scarif, 7, ExpansionSet.SET_9, Rarity.V);
        setLocationLightSideGameText("While Rogue One is piloted by a Rebel here, it is immune to attrition.");
        setLocationDarkSideGameText("Unless your Star Destroyer here (or your Imperial at a related location), Force drain -1 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_9);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter rogueOnePilotedByRebelHere = Filters.and(Filters.Rogue_One, Filters.hasPiloting(self, Filters.Rebel), Filters.here(self));
        modifiers.add(new ImmuneToAttritionModifier(self, rogueOnePilotedByRebelHere));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        UnlessCondition unlessYourStarDestroyerHere = new UnlessCondition(new HereCondition(self, Filters.Star_Destroyer));
        UnlessCondition unlessYourImperialAtRelatedLocation = new UnlessCondition(new AtCondition(self, Filters.Imperial, Filters.and(Filters.relatedLocation(self), Filters.anotherLocation(self))));
        modifiers.add(new ForceDrainModifier(self, new AndCondition(unlessYourStarDestroyerHere, unlessYourImperialAtRelatedLocation), -1, playerOnDarkSideOfLocation));
        return modifiers;
    }
}