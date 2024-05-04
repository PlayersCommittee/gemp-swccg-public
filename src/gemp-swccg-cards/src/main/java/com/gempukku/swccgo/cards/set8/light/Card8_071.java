package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Location
 * Subtype: Site
 * Title: Endor: Chief Chirpa's Hut
 */
public class Card8_071 extends AbstractSite {
    public Card8_071() {
        super(Side.LIGHT, Title.Chief_Chirpas_Hut, Title.Endor, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLocationDarkSideGameText("If you control Chief Chirpa's Hut, Ewoks at Endor sites are forfeit -1.");
        setLocationLightSideGameText("Your Ewoks are deploy -1 here. Chief Chirpa deploys free here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.INTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Ewok, Filters.at(Filters.Endor_site)),
                new ControlsCondition(playerOnDarkSideOfLocation, Filters.Chief_Chirpas_Hut), -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Ewok), -1, self));
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Chief_Chirpa, self));
        return modifiers;
    }
}