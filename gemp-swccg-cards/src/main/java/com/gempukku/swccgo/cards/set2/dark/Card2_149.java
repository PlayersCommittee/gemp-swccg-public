package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractNonuniqueVehicleSite;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EnterExitCostForCharactersModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NighttimeConditionsModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Location
 * Subtype: Site
 * Title: Sandcrawler: Droid Junkheap
 */
public class Card2_149 extends AbstractNonuniqueVehicleSite {
    public Card2_149() {
        super(Side.DARK, Title.Droid_Junkheap, Filters.sandcrawler, Uniqueness.RESTRICTED_3);
        setLocationDarkSideGameText("Deploy on your sandcrawler. Each Jawa is forfeit +2 here. 'Nighttime conditions' here.");
        setLocationLightSideGameText("Your characters may enter/exit here for 1 Force each. 'Nighttime conditions' here.");
        addIcons(Icon.A_NEW_HOPE, Icon.INTERIOR_SITE);
        addKeywords(Keyword.SANDCRAWLER_SITE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Jawa, Filters.here(self)), 2));
        modifiers.add(new NighttimeConditionsModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EnterExitCostForCharactersModifier(self, 1, playerOnLightSideOfLocation));
        modifiers.add(new NighttimeConditionsModifier(self));
        return modifiers;
    }
}