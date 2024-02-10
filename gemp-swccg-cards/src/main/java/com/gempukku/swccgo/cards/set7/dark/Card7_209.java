package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Umpass-stay
 */
public class Card7_209 extends AbstractAlien {
    public Card7_209() {
        super(Side.DARK, 2, 4, 3, 1, 2, "Umpass-stay", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Klatooinian born into slavery to Desilijic Hutt clan on Shawti. Musician. Works as a spy for Jabba at his desert fortress while playing drums for visiting bands.");
        setGameText("Power +2 at a Jabba's Palace site. Each of your other musicians at same site is power +2 and immune to attrition < 3. Immune to attrition < 4.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.MUSICIAN, Keyword.SPY);
        setSpecies(Species.KLATOOINIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter otherMusiciansFilter = Filters.and(Filters.your(self), Filters.other(self), Filters.musician, Filters.atSameSite(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.Jabbas_Palace_site), 2));
        modifiers.add(new PowerModifier(self, otherMusiciansFilter, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, otherMusiciansFilter, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
