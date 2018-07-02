package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractSector;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.evaluators.TopLocationsOnTableEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Sector
 * Title: Big One
 */
public class Card4_082 extends AbstractSector {
    public Card4_082() {
        super(Side.LIGHT, Title.Big_One, Uniqueness.DIAMOND_1);
        setLocationDarkSideGameText("'Asteroid Rules' in effect here. If you control, Force drain +1 here.");
        setLocationLightSideGameText("'Asteroid Rules' in effect here. If you control, Force drain +1 here for each Asteroid Field at same system.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.SPACE);
        addKeywords(Keyword.ASTEROID);
        addSpecialRulesInEffectHere(SpecialRule.ASTEROID_RULES);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnDarkSideOfLocation, self), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnLightSideOfLocation, self),
                new TopLocationsOnTableEvaluator(Filters.and(Filters.Asteroid_Field, Filters.relatedLocation(self))),
                playerOnLightSideOfLocation));
        return modifiers;
    }
}