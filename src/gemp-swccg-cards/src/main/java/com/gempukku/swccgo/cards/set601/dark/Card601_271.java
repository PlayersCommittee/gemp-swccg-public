package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractSite;
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
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Location
 * Subtype: Site
 * Title: Ralltiir: Spaceport Financial District
 */
public class Card601_271 extends AbstractSite {
    public Card601_271() {
        super(Side.DARK, "Ralltiir: Spaceport Financial District", Title.Ralltiir, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLocationDarkSideGameText("Same and related spaceport sites opponent does not control gain one [Dark Side] icon.");
        setLocationLightSideGameText("Immune to Expand the Empire and Revolution.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_4);
        addKeywords(Keyword.SPACEPORT_SITE);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter filter = Filters.and(Filters.sameOrRelatedSite(self), Filters.spaceport_site, Filters.not(Filters.controls(game.getOpponent(playerOnDarkSideOfLocation))));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new IconModifier(self, filter, Icon.DARK_FORCE));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Expand_The_Empire));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }
}