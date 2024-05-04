package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
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
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Location
 * Subtype: System
 * Title: Coruscant (V)
 */
public class Card203_031 extends AbstractSystem {
    public Card203_031() {
        super(Side.DARK, Title.Coruscant, 0, ExpansionSet.SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("While you occupy with a Black Sun agent or ISB agent, gains one [Dark Force] icon and one [Light Force] icon.");
        setLocationLightSideGameText("While you control, gains one [Light Force] icon.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_3);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition condition = new OccupiesWithCondition(playerOnDarkSideOfLocation, self, Filters.or(Filters.Black_Sun_agent, Filters.ISB_agent));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, condition, Icon.DARK_FORCE, 1));
        modifiers.add(new IconModifier(self, condition, Icon.LIGHT_FORCE, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, new ControlsCondition(playerOnLightSideOfLocation, self), Icon.LIGHT_FORCE, 1));
        return modifiers;
    }
}