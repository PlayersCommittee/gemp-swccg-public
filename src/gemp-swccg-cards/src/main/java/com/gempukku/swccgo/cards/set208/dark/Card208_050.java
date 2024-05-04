package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Location
 * Subtype: System
 * Title: Jedha
 */
public class Card208_050 extends AbstractSystem {
    public Card208_050() {
        super(Side.DARK, Title.Jedha, 4, ExpansionSet.SET_8, Rarity.V);
        setLocationDarkSideGameText("While you control, your superlaser weapon destiny draws are +1.");
        setLocationLightSideGameText("Unless Baze, Bodhi, Chirrut, or Saw here, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.superlaser_weapon),
                new ControlsCondition(playerOnDarkSideOfLocation, self), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new UnlessCondition(new HereCondition(self, Filters.or(Filters.Baze,
                Filters.Bodhi, Filters.Chirrut, Filters.Saw))), -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}