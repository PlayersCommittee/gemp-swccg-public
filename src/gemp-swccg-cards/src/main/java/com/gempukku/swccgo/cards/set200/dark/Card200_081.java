package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
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
 * Set: Set 0
 * Type: Character
 * Subtype: Imperial
 * Title: General Veers (V)
 */
public class Card200_081 extends AbstractImperial {
    public Card200_081() {
        super(Side.DARK, 1, 3, 3, 3, 5, "General Veers", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("General Maximillian Veers is the model of Imperial Army officer. Cunning, loyal and ruthlessly efficient leader. In charge of the ground assault troops in Vader's forces.");
        setGameText("[Pilot] 3. While piloting an AT-AT, draws two battle destiny if unable to otherwise. While piloting a combat vehicle at a site where you have no other characters, its immunity to attrition is +2.");
        addPersona(Persona.VEERS);
        addIcons(Icon.HOTH, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.AT_AT), 2));

        String playerId = self.getOwner();
        Filter ownersOtherCharacterHereFilter = Filters.and(Filters.owner(playerId), Filters.other(self), Filters.character, Filters.here(self));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(Filters.combat_vehicle, Filters.hasPiloting(self)), new CantSpotCondition(self, ownersOtherCharacterHereFilter), 2));

        return modifiers;
    }
}
