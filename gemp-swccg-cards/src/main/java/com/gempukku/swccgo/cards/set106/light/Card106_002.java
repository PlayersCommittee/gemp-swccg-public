package com.gempukku.swccgo.cards.set106.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Location
 * Subtype: System
 * Title: Corulag
 */
public class Card106_002 extends AbstractSystem {
    public Card106_002() {
        super(Side.LIGHT, Title.Corulag, 4);
        setLocationDarkSideGameText("If you control, Force drain -1 here.");
        setLocationLightSideGameText("If you control, all non-unique Rebels are power and forfeit +1 and Rebel guards may move.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.PREMIUM, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnDarkSideOfLocation, self), -1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition youControl = new ControlsCondition(playerOnLightSideOfLocation, self);
        Filter nonuniqueRebels = Filters.and(Filters.non_unique, Filters.Rebel);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, nonuniqueRebels, youControl, 1));
        modifiers.add(new ForfeitModifier(self, nonuniqueRebels, youControl, 1));
        modifiers.add(new MayMoveModifier(self, Filters.Rebel_Guard, youControl));
        return modifiers;
    }
}