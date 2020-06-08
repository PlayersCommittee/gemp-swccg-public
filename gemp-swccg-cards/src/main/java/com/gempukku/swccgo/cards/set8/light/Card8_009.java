package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Dresselian Commando
 */
public class Card8_009 extends AbstractRebel {
    public Card8_009() {
        super(Side.LIGHT, 2, 3, 2, 2, 2, "Dresselian Commando");
        setLore("Dresselians combat their homeworld's subjugation with supplies provided by Bothans. The Alliance uses their talents as resistance fighters. These scouts prefer to work alone.");
        setGameText("Power +1 when at a forest, jungle or exterior Endor site. When in battle at an exterior site against a non-unique alien, adds one destiny to power only, prevents opponent from drawing more than one battle destiny and is immune to attrition < 4.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.DRESSELIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition inBattleAgainstCondition = new AndCondition(new InBattleAtCondition(self, Filters.exterior_site),
                new InBattleWithCondition(self, Filters.and(Filters.opponents(self), Filters.non_unique, Filters.alien)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.forest, Filters.jungle, Filters.exterior_Endor_site)), 1));
        modifiers.add(new AddsDestinyToPowerModifier(self, inBattleAgainstCondition, 1));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, inBattleAgainstCondition, 1, game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, inBattleAgainstCondition, 4));
        return modifiers;
    }
}
