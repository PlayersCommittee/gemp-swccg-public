package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractNonuniqueVehicleSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Title: Sandcrawler: Loading Bay
 */
public class Card2_066 extends AbstractNonuniqueVehicleSite {
    public Card2_066() {
        super(Side.LIGHT, "Sandcrawler: Loading Bay", Filters.sandcrawler, Uniqueness.RESTRICTED_3, ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLocationDarkSideGameText("Your characters may enter/exit here for 1 Force each. 'Nighttime conditions' here.");
        setLocationLightSideGameText("Deploy on your sandcrawler. Each Jawa is forfeit +2 here. 'Nighttime conditions' here.");
        addIcons(Icon.A_NEW_HOPE, Icon.INTERIOR_SITE);
        addKeywords(Keyword.SANDCRAWLER_SITE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EnterExitCostForCharactersModifier(self, 1, playerOnDarkSideOfLocation));
        modifiers.add(new NighttimeConditionsModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Jawa, Filters.here(self)), 2));
        modifiers.add(new NighttimeConditionsModifier(self));
        return modifiers;
    }
}