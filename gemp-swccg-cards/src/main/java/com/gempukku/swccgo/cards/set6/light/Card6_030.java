package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Ortolan
 */
public class Card6_030 extends AbstractAlien {
    public Card6_030() {
        super(Side.LIGHT, 3, 2, 1, 1, 2, "Ortolan", Uniqueness.RESTRICTED_3);
        setLore("Squat, floppy-eared food lovers. Ortolans come from the frigid planet Orto. Communicate above the auditory range of most species.");
        setGameText("Power and forfeit +2 on Hoth or at Dining Room. While at a marker site, cumulatively adds 1 to number of Hoth sites required for opponent to gain a Force drain bonus from Walker Garrison.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.ORTOLAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition onHothOrAtDiningRoom = new OrCondition(new OnCondition(self, Title.Hoth), new AtCondition(self, Filters.Dining_Room));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, onHothOrAtDiningRoom, 2));
        modifiers.add(new ForfeitModifier(self, onHothOrAtDiningRoom, 2));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.opponents(self), Filters.Walker_Garrison),
                new AtCondition(self, Filters.marker_site), ModifyGameTextType.WALKER_GARRISON__ADDITIONAL_SITE_TO_GAIN_FORCE_DRAIN_BONUS));
        return modifiers;
    }
}
