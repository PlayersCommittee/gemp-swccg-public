package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
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
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOneWeaponTwicePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddBattleDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Character
 * Subtype: Jedi Master
 * Title: Mace Windu (AI) (V)
 */
public class Card201_042 extends AbstractJediMaster {
    public Card201_042() {
        super(Side.LIGHT, 1, 7, 6, 7, 8, "Mace Windu", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setAlternateImageSuffix(true);
        setVirtualSuffix(true);
        setLore("Senior Jedi Council member who maintains rigorous adherence to the Code. Sent Qui-Gon to Naboo to accompany the Queen and learn more about the mysterious 'dark warrior'.");
        setGameText("During battle, Mace may 'swing' one lightsaber twice. Fetts may not add battle destiny draws here. Immune to attrition < 6 (< 8 if with a Dark Jedi).");
        addPersona(Persona.MACE);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayFireOneWeaponTwicePerBattleModifier(self, new DuringBattleCondition(), Filters.lightsaber));
        modifiers.add(new MayNotAddBattleDestinyDrawsModifier(self, Filters.and(Filters.Fett, Filters.here(self))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(6, 8, new WithCondition(self, Filters.Dark_Jedi))));
        return modifiers;
    }
}
