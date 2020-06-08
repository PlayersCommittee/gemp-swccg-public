package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotUseCardToTransportToOrFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Lobel
 */
public class Card7_186 extends AbstractAlien {
    public Card7_186() {
        super(Side.DARK, 3, 2, 2, 1, 3, "Lobel");
        setLore("Powerful magnetic fields in the Lobel physiology make the operation of nearby navigation devices impossible. Pilots claim these fields also produce headaches.");
        setGameText("Subtracts 2 from forfeit of each opponent's pilot at same site. Prevents characters from moving to or from same or adjacent site using Elis Helrot or Nabrun Leids.");
        addIcons(Icon.SPECIAL_EDITION);
        setSpecies(Species.LOBEL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.opponents(self), Filters.pilot, Filters.atSameSite(self)), -2));
        modifiers.add(new MayNotUseCardToTransportToOrFromLocationModifier(self, Filters.or(Filters.Elis_Helrot, Filters.Nabrun_Leids), Filters.sameOrAdjacentSite(self)));
        return modifiers;
    }
}
