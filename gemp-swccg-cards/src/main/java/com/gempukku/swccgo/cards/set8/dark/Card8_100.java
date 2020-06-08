package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.FireWeaponFiredByCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Elite Squadron Stormtrooper
 */
public class Card8_100 extends AbstractImperial {
    public Card8_100() {
        super(Side.DARK, 3, 2, 1, 2, 3, Title.Elite_Squadron_Stormtrooper, Uniqueness.RESTRICTED_3);
        setLore("Commander Igar selected the Empire's most dedicated stormtroopers to guard Endor for the Emperor. All they know is killing and white uniforms.");
        setGameText("Power +2 while armed with a blaster. When using a blaster, subtracts 1 from Force required to fire it. Adds 2 to forfeit of each stormtrooper of ability < 2 at same site.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.STORMTROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new ArmedWithCondition(self, Filters.blaster), 2));
        modifiers.add(new FireWeaponFiredByCostModifier(self, -1, Filters.blaster));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.stormtrooper, Filters.abilityLessThan(2), Filters.atSameSite(self)), 2));
        return modifiers;
    }
}
