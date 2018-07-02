package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.UnderNighttimeConditionConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: 5D6-RA-7 (Fivedesix)
 */
public class Card1_163 extends AbstractDroid {
    public Card1_163() {
        super(Side.DARK, 2, 4, 2, 5, Title._5D6RA7, Uniqueness.UNIQUE);
        setLore("Aide to Admiral Motti's staff. Foul-tempered and vindictive. Feared by other droids. A spy for the ISB. Secretly investigates Imperial officers whose loyalties are in question.");
        setGameText("Power +1 if at same site as Motti or Yularen. Deploy +1 for all opponent's droids (and your 'mouse' droids) at same location. Immune to attrition during 'nighttime conditions.'");
        addKeywords(Keyword.SPY);
        addModelType(ModelType.SERVANT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, Filters.or(Filters.Motti, Filters.Yularen)), 1));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.or(Filters.and(Filters.opponents(self), Filters.droid),
                        Filters.and(Filters.your(self), Filters.mouse_droid)), 1, Filters.sameLocation(self)));
        modifiers.add(new ImmuneToAttritionModifier(self, new UnderNighttimeConditionConditions(self)));
        return modifiers;
    }
}
