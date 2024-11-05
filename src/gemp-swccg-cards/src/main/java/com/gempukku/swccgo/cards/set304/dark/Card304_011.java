package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Reiden Karr
 */
public class Card304_011 extends AbstractImperial {
    public Card304_011() {
        super(Side.DARK, 1, 5, 5, 6, 6, "Reiden Karr", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Reiden is currently the leader of House Acclivis Draco. Under his cautious eye the House has continued to thrive. Reiden is conflicted by Kamjin and tends to align himself with Thran, the Usurper.");
        setGameText("May not be disarmed. Immune to Clash Of Sabers. While armed with a lightsaber add 1 to each of his lightsaber weapon destiny draws. ");
        addIcons(Icon.CSP, Icon.WARRIOR);
		addKeywords(Keyword.LEADER, Keyword.MALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        modifiers.add(new ImmuneToTitleModifier(self, Title.Disarmed));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Clash_Of_Sabers));
		modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 1, Filters.lightsaber, Filters.any));
        return modifiers;
    }
}
