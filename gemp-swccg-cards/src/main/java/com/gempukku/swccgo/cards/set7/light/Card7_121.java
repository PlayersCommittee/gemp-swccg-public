package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Forest
 */
public class Card7_121 extends AbstractSite {
    public Card7_121() {
        super(Side.LIGHT, "Forest", Uniqueness.DIAMOND_1);
        setLocationDarkSideGameText("May not be deployed to Bespin, Coruscant, Hoth, Kessel or Tatooine.");
        setLocationLightSideGameText("May not be deployed to Bespin, Coruscant, Hoth, Kessel or Tatooine. Your characters present here are immune to attrition.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.FOREST);
        addMayNotBePartOfSystem(Title.Bespin, Title.Coruscant, Title.Hoth, Title.Kessel, Title.Tatooine, Title.Ahch_To);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.character, Filters.present(self))));
        return modifiers;
    }
}