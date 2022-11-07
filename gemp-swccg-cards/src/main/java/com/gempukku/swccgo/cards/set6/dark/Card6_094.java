package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Aqualish
 */
public class Card6_094 extends AbstractAlien {
    public Card6_094() {
        super(Side.DARK, 3, 3, 2, 1, 2, "Aqualish", Uniqueness.RESTRICTED_3);
        setLore("Aqualish originate from Ando. Continually at war. Reached the stars by eliminating the creator of the first ship that landed on their planet. Often seen of Cloud City.");
        setGameText("Power +2 and forfeit +1 while Ponda Baba is at Audience Chamber. May initiate a battle for free where present. When at a Cloud City site, cumulatively adds one to number of Bespin locations required to cancel Dark Deal.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.AQUALISH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whilePondaBabaAtAudienceChamber = new AtCondition(self, Filters.Ponda_Baba, Filters.Audience_Chamber);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, whilePondaBabaAtAudienceChamber, 2));
        modifiers.add(new ForfeitModifier(self, whilePondaBabaAtAudienceChamber, 1));
        modifiers.add(new MayInitiateBattlesForFreeModifier(self, Filters.wherePresent(self), self.getOwner()));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Dark_Deal, new AtCondition(self, Filters.Cloud_City_site),
                ModifyGameTextType.DARK_DEAL__ADDITIONAL_BESPIN_LOCATION_TO_CANCEL));
        return modifiers;
    }
}
