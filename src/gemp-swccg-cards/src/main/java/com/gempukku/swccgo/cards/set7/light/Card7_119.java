package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.PartOfSystemCondition;
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
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Desert
 */
public class Card7_119 extends AbstractSite {
    public Card7_119() {
        super(Side.LIGHT, "Desert", Uniqueness.DIAMOND_1, ExpansionSet.SPECIAL_EDITION, Rarity.F);
        setLocationDarkSideGameText("May not be deployed to Bespin, Coruscant, Dagobah, Hoth, Kessel or Naboo. Each of your battle destiny draws is -1 here. Sandwhirl here moves only if on Tatooine.");
        setLocationLightSideGameText("May not be deployed to Bespin, Coruscant, Dagobah, Hoth, Kessel or Naboo. Your Jawas may deploy here. Sandwhirl here moves only if on Tatooine.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.DESERT);
        addMayNotBePartOfSystem(Title.Bespin, Title.Coruscant, Title.Dagobah, Title.Hoth, Title.Kessel, Title.Naboo, Title.Ahch_To);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, self, -1, playerOnDarkSideOfLocation));
        modifiers.add(new MayNotMoveModifier(self, Filters.and(Filters.Sandwhirl, Filters.here(self)), new NotCondition(new PartOfSystemCondition(self, Title.Tatooine))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Jawa), self));
        modifiers.add(new MayNotMoveModifier(self, Filters.and(Filters.Sandwhirl, Filters.here(self)), new NotCondition(new PartOfSystemCondition(self, Title.Tatooine))));
        return modifiers;
    }
}