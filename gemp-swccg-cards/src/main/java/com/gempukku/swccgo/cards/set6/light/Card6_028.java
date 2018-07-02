package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
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
 * Title: Max Rebo
 */
public class Card6_028 extends AbstractAlien {
    public Card6_028() {
        super(Side.LIGHT, 2, 3, 1, 1, 2, "Max Rebo", Uniqueness.UNIQUE);
        setLore("Ortolan musician and gambler. Leader of The Max Rebo Band. Signed a lifetime contract to Jabba in exchange for unlimited food.");
        setGameText("Power +2 on Hoth. If at same site as another of you musicians, you may play Bith Shuffle to cancel a Force drain at an adjacent site. While at Audience Chamber, all your other musicians are deploy -1 and forfeit +3.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.MUSICIAN, Keyword.GAMBLER, Keyword.LEADER);
        setSpecies(Species.ORTOLAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);
        Filter yourOtherMusician = Filters.and(Filters.your(self), Filters.other(self), Filters.musician);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OnCondition(self, Title.Hoth), 2));
        modifiers.add(new MayPlayToCancelForceDrainModifier(self, Filters.and(Filters.your(self), Filters.Bith_Shuffle),
                new AtSameSiteAsCondition(self, yourOtherMusician), Filters.adjacentSite(self)));
        modifiers.add(new DeployCostModifier(self, yourOtherMusician, atAudienceChamber, -1));
        modifiers.add(new ForfeitModifier(self, yourOtherMusician, atAudienceChamber, 3));
        return modifiers;
    }
}
