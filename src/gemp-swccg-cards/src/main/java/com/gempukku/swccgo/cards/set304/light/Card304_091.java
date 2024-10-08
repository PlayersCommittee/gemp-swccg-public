package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOneWeaponTwicePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Great Hutt Expansion
 * Type: Character
 * Subtype: Jedi Master
 * Title: Master Rajhin Cindertail
 */
public class Card304_091 extends AbstractJediMaster {
    public Card304_091() {
        super(Side.LIGHT, 1, 6, 5, 7, 8, "Master Rajhin Cindertail", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Recently elevated to Master, Rajhin continues to serve the Council as Fist.");
        setGameText("During battle, may fire one weapon twice. Power +1 for each of opponent's character of ability < 3 here. Your aliens are forfeit +1 here. Immune to attrition < 6.");
        addPersona(Persona.RAJHIN);
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.COUNCILOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayFireOneWeaponTwicePerBattleModifier(self, new DuringBattleCondition(), Filters.weapon));
        modifiers.add(new PowerModifier(self, new HereEvaluator(self, Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(3)))));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.alien, Filters.here(self)), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }
}
