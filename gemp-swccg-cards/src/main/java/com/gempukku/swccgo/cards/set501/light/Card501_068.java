package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: Death Star II: Chasm Walkway
 */
public class Card501_068 extends AbstractSite {
    public Card501_068() {
        super(Side.LIGHT, Title.Death_Star_II_Chasm_Walkway, Title.Death_Star_II);
        setLocationDarkSideGameText("Your total power here is -1 for each card stacked on I Feel The Conflict.");
        setLocationLightSideGameText("If alone, Luke is power +2 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.DEATH_STAR_II, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_13);
        setTestingText("Death Star II: Chasm Walkway");
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.here(self), new StackedEvaluator(self, Filters.I_Feel_The_Conflict), playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Luke, Filters.alone, Filters.here(self)), 2));
        return modifiers;
    }
}
