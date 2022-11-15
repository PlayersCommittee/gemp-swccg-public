package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DoubledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Location
 * Subtype: System
 * Title: Kiffex
 */
public class Card6_083 extends AbstractSystem {
    public Card6_083() {
        super(Side.LIGHT, Title.Kiffex, 2, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLocationDarkSideGameText("If you occupy with exactly 2 starships, your total power here is +2.");
        setLocationLightSideGameText("If you occupy with exactly 2 starships, your total power here is +2 and Vul Tazaene anywhere is doubled.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.JABBAS_PALACE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        Condition occupyWithExactlyTwoStarships = new OccupiesWithCondition(playerOnDarkSideOfLocation, self, 2, true, Filters.starship);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, occupyWithExactlyTwoStarships, 2, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition occupyWithExactlyTwoStarships = new OccupiesWithCondition(playerOnLightSideOfLocation, self, 2, true, Filters.starship);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, occupyWithExactlyTwoStarships, 2, playerOnLightSideOfLocation));
        modifiers.add(new DoubledModifier(self, Filters.Vul_Tazaene, occupyWithExactlyTwoStarships));
        return modifiers;
    }
}