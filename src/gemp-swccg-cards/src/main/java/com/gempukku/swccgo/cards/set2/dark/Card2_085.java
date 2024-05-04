package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Danz Borin
 */
public class Card2_085 extends AbstractAlien {
    public Card2_085() {
        super(Side.DARK, 3, 2, 1, 2, 3, Title.Danz_Borin, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.U2);
        setLore("Cocky gunner and bounty hunter. Maintains a residence on Nar Shaddaa, the spaceport moon of the Hutt homeworld. To his companions' delight, he's nearly as good as he boasts.");
        setGameText("Adds 3 to power of anything he pilots. Adds 1 to weapon destiny draws of anything he is aboard as a passenger.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT);
        addKeywords(Keyword.GUNNER, Keyword.BOUNTY_HUNTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, Filters.hasPassenger(self), 1));
        return modifiers;
    }
}
