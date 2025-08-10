package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeExcludedFromBattle;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Location
 * Subtype: Site
 * Title: Crait: Outpost Entrance Cavern
 */
public class Card225_016 extends AbstractSite {
    public Card225_016() {
        super(Side.DARK, "Crait: Outpost Entrance Cavern", Title.Crait, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLocationDarkSideGameText("Force drain +1 here. Kylo may not be excluded from battles here.");
        setLocationLightSideGameText("During battle here, Force Projection is immune to Sense.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EPISODE_VII, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, 1, playerOnDarkSideOfLocation));
        modifiers.add(new MayNotBeExcludedFromBattle(self, Filters.and(Filters.Kylo, Filters.here(self))));
        return modifiers;
    }


    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.title(Title.Force_Projection), new DuringBattleAtCondition(Filters.here(self)), Title.Sense));
        return modifiers;
    }
}
