package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Rodian
 */
public class Card2_104 extends AbstractAlien {
    public Card2_104() {
        super(Side.DARK, 3, 2, 2, 1, 2, "Rodian", Uniqueness.RESTRICTED_3, ExpansionSet.A_NEW_HOPE, Rarity.C2);
        setLore("Thuku is a typical male Rodian. Sent to hunt down Greedo by Navik the Red, head of the Chattza tribe. Rodians enjoy 'the hunt,' so many are employed as assassins.");
        setGameText("Adds 1 to power of each of your bounty hunters and smugglers (but subtracts 1 from Greedo's power) at same site. Adds 1 to power of anything he pilots.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT);
        setSpecies(Species.RODIAN);
        addKeyword(Keyword.ASSASSIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.bounty_hunter, Filters.smuggler),
                Filters.not(Filters.Greedo), Filters.atSameSite(self)), 1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Greedo, Filters.atSameSite(self)), -1));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        return modifiers;
    }
}
