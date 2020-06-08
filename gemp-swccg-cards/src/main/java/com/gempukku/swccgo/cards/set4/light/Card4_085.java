package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Dagobah: Bog Clearing
 */
public class Card4_085 extends AbstractSite {
    public Card4_085() {
        super(Side.LIGHT, Title.Dagobah_Bog_Clearing, Title.Dagobah);
        setLocationDarkSideGameText("If you occupy, Force generation +1 for you here.");
        setLocationLightSideGameText("Your starfighters may deploy here and immune to Awwww, Cannot Get Your Ship Out here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, new OccupiesCondition(playerOnDarkSideOfLocation, self), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourStarfighters = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starfighter);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToDagobahLocationModifier(self, yourStarfighters, self));
        modifiers.add(new MayDeployAsLandedToLocationModifier(self, yourStarfighters, self));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(yourStarfighters, Filters.here(self)), Title.Awwww_Cannot_Get_Your_Ship_Out));
        return modifiers;
    }
}