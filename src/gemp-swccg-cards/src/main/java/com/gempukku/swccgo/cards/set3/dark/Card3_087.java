package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ArmorModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Imperial
 * Title: General Veers
 */
public class Card3_087 extends AbstractImperial {
    public Card3_087() {
        super(Side.DARK, 1, 3, 3, 3, 5, "General Veers", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R1);
        setLore("General Maximillian Veers is the model of Imperial Army officer. Cunning, loyal and ruthlessly efficient leader. In charge of the ground assault troops in Vader's forces.");
        setGameText("Power +1 when at same site as Admiral Ozzel. Adds 1 to power of each Imperial at same Hoth site. Adds 3 to power of any combat vehicle he pilots. On Blizzard 1, also adds 1 to armor and draws one battle destiny if not able to otherwise.");
        addPersona(Persona.VEERS);
        addIcons(Icon.HOTH, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
        setMatchingVehicleFilter(Filters.Blizzard_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, Filters.Ozzel), 1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Imperial, Filters.atSameSite(self)), new AtCondition(self, Filters.Hoth_site), 1));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.combat_vehicle));
        modifiers.add(new ArmorModifier(self, Filters.and(Filters.Blizzard_1, Filters.hasPiloting(self)), 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Blizzard_1), 1));
        return modifiers;
    }
}
