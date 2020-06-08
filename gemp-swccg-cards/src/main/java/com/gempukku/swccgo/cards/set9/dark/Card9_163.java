package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Saber 2
 */
public class Card9_163 extends AbstractStarfighter {
    public Card9_163() {
        super(Side.DARK, 3, 2, 3, null, 4, null, 3, Title.Saber_2, Uniqueness.UNIQUE);
        setLore("TIE interceptor flown by Major Phennir. Assigned to protect Avenger at the Battle of Endor. Responsible for the logistics of Saber Squadron.");
        setGameText("May add 1 pilot. When Phennir piloting, immune to attrition < 4 and adds 1 to forfeit of each other Saber Squadron pilot in same battle.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.SABER_SQUADRON, Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.TIE_INTERCEPTOR);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Phennir);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition phennirPiloting = new HasPilotingCondition(self, Filters.Phennir);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, phennirPiloting, 4));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.not(Filters.Phennir), Filters.Saber_Squadron_pilot,
                Filters.inBattleWith(self)), phennirPiloting, 1));
        return modifiers;
    }
}
