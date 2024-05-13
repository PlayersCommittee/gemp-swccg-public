package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: WED15-I662 'Treadwell' Droid
 */
public class Card1_197 extends AbstractDroid {
    public Card1_197() {
        super(Side.DARK, 3, 2, 1, 3, "WED15-I662 'Treadwell' Droid", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Standard treadwell droid. This one, also known as 'Eyesixsixtoo', is typical of the thousands of droids which repair and maintain heavy machinery and starfighters.");
        setGameText("Adds immunity to attrition < 2 to all your vehicles and droids at same location. Also, if 'treadwell' droid is at a docking bay, adds immunity to attrition < 3 to all your starfighters at the related system and related sectors.");
        addModelType(ModelType.MAINTENANCE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.vehicle,
                Filters.droid), Filters.at(Filters.sameLocation(self))), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.starfighter,
                Filters.at(Filters.relatedSystemOrSector(self))), new AtCondition(self, Filters.docking_bay), 3));
        return modifiers;
    }
}
