package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: 2X-3KPR (Tooex)
 */
public class Card1_001 extends AbstractDroid {
    public Card1_001() {
        super(Side.LIGHT, 3, 2, 0, 2, "2X-3KPR (Tooex)");
        setLore("Simple maintenance and diagnostics droid. Activates alarm sensors, security lighting and power fences on remote installations. KPR servant droids built by Lerrimore Droids.");
        setGameText("Where present under 'nighttime conditions,' each of your Rebels and aliens at same planet site are power +2, and also have immunity to attrition < 3 if a Hydroponics Station is present.");
        addModelType(ModelType.SECURITY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.or(Filters.Rebel, Filters.alien), Filters.atSameSite(self));
        Condition condition = new PresentAtCondition(self, Filters.and(Filters.planet_site, Filters.under_nighttime_conditions));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, filter, condition, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, filter, new AndCondition(condition, new PresentCondition(self, Filters.Hydroponics_Station)), 3));
        return modifiers;
    }
}
