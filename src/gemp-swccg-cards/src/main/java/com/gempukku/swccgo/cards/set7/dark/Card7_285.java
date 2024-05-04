package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Jungle
 */
public class Card7_285 extends AbstractSite {
    public Card7_285() {
        super(Side.DARK, "Jungle", Uniqueness.DIAMOND_1, ExpansionSet.SPECIAL_EDITION, Rarity.F);
        setLocationDarkSideGameText("May not deploy to Bespin, Coruscant, Hoth, Kessel or Tatooine. Your aliens and creature vehicles are each power +1 here.");
        setLocationLightSideGameText("May not deploy to Bespin, Coruscant, Hoth, Kessel or Tatooine. Force Drain -1 here. Your attrition against opponent is -2 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.JUNGLE);
        addMayNotBePartOfSystem(Title.Bespin, Title.Coruscant, Title.Hoth, Title.Kessel, Title.Tatooine, Title.Ahch_To);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.or(Filters.alien, Filters.creature_vehicle), Filters.here(self)), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, -1, playerOnLightSideOfLocation));
        modifiers.add(new AttritionModifier(self, Filters.here(self), -2, game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}