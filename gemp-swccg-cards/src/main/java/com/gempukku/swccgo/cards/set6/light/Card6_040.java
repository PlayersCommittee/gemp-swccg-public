package com.gempukku.swccgo.cards.set6.light;

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
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Sic-Six
 */
public class Card6_040 extends AbstractAlien {
    public Card6_040() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, "Sic-Six");
        setLore("Gifted engineers. Sic-six believe their technology is superior. Have a poison stinger. Spin vast and intricate webs which create a hazard for landing starships. Avoided by pilots.");
        setGameText("Subtracts 2 from forfeit of each opponent's pilot at same site. Prevents characters from moving to or from same or adjacent sites using Elis Helrot or Nabrun Leids.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.SICSIX);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.opponents(self), Filters.pilot, Filters.atSameSite(self)), -2));
        modifiers.add(new MayNotUseCardToTransportToOrFromLocationModifier(self, Filters.or(Filters.Elis_Helrot, Filters.Nabrun_Leids), Filters.sameOrAdjacentSite(self)));
        return modifiers;
    }
}
