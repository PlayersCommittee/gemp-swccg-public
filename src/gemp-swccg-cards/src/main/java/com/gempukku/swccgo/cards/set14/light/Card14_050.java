package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractSite;
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
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Location
 * Subtype: Site
 * Title: Naboo: Otoh Gunga Entrance
 */
public class Card14_050 extends AbstractSite {
    public Card14_050() {
        super(Side.LIGHT, "Naboo: Otoh Gunga Entrance", Title.Naboo, Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLocationDarkSideGameText("Cards with ability (except Gungans) deploy +4 to underwater sites. Immune to Revolution.");
        setLocationLightSideGameText("Your Gungans deploy -1 here and move from here for free. You may not Force drain here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.UNDERWATER, Icon.INTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.hasAbilityOrHasPermanentPilotWithAbility, Filters.except(Filters.Gungan)),
                4, Filters.underwater_site, true));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourGungans = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Gungan);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourGungans, -1, self, true));
        modifiers.add(new MovesFreeFromLocationModifier(self, yourGungans, self));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, playerOnLightSideOfLocation));
        return modifiers;
    }
}