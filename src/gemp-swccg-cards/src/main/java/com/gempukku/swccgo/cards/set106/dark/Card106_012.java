package com.gempukku.swccgo.cards.set106.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Location
 * Subtype: System
 * Title: Corulag
 */
public class Card106_012 extends AbstractSystem {
    public Card106_012() {
        super(Side.DARK, Title.Corulag, 4, ExpansionSet.OTSD, Rarity.PM);
        setLocationDarkSideGameText("If you control, all non-unique Imperials are power and forfeit +1 and Imperial trooper guards may move.");
        setLocationLightSideGameText("If you control, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PREMIUM, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition youControl = new ControlsCondition(playerOnDarkSideOfLocation, self);
        Filter nonuniqueImperials = Filters.and(Filters.non_unique, Filters.Imperial);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, nonuniqueImperials, youControl, 1));
        modifiers.add(new ForfeitModifier(self, nonuniqueImperials, youControl, 1));
        modifiers.add(new MayMoveModifier(self, Filters.Imperial_Trooper_Guard, youControl));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnLightSideOfLocation, self), -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}