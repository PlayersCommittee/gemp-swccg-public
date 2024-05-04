package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Droid
 * Title: 2X-7KPR (Tooex)
 */
public class Card7_163 extends AbstractDroid {
    public Card7_163() {
        super(Side.DARK, 3, 2, 0, 2, "2X-7KPR (Tooex)", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("Basic security droid. Monitors perimeter sensors and floodlights at Imperial outposts. Responsible for maintenance of security devices. Stolen from Jabba.");
        setGameText("Where present under 'nighttime conditions,' each of your Imperials and aliens at same planet site is power +2 and immune to attrition < 3.");
        addIcon(Icon.SPECIAL_EDITION);
        addModelType(ModelType.SECURITY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.or(Filters.Imperial, Filters.alien), Filters.atSameSite(self));
        Condition condition = new PresentAtCondition(self, Filters.and(Filters.planet_site, Filters.under_nighttime_conditions));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, filter, condition, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, filter, condition, 3));
        return modifiers;
    }
}
