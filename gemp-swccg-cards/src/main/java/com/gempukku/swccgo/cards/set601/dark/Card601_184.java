package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 2
 * Type: Character
 * Subtype: Imperial
 * Title: General Veers (V)
 */
public class Card601_184 extends AbstractImperial {
    public Card601_184() {
        super(Side.DARK, 1, 3, 3, 3, 5, "General Veers", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("General Maximillian Veers is the model of Imperial Army officer. Cunning, loyal and ruthlessly efficient leader. In charge of the ground assault troops in Vader's forces.");
        setGameText("[Pilot] 3. While piloting an AT-AT, draws two battle destiny if unable to otherwise. While piloting a combat vehicle, its immunity to attrition is +2.");
        addPersona(Persona.VEERS);
        addIcons(Icon.HOTH, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_2);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.AT_AT), 2));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(Filters.combat_vehicle, Filters.hasPiloting(self)), 2));
        return modifiers;
    }
}
