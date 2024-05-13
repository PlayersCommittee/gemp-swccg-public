package com.gempukku.swccgo.cards.set218.light;

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
 * Set: Set 18
 * Type: Character
 * Subtype: Jedi Master
 * Title: Master Windu
 */
public class Card218_004 extends AbstractJediMaster {
    public Card218_004() {
        super(Side.LIGHT, 1, 7, 6, 7, 8, "Master Windu", Uniqueness.UNIQUE, ExpansionSet.SET_18, Rarity.V);
        setLore("Jedi Council member.");
        setGameText("During battle, may swing a lightsaber twice. Power +1 for each of opponent's character of ability < 2 here. Your clones are forfeit +1 here. Immune to attrition < 6.");
        addPersona(Persona.MACE);
        addIcons(Icon.CLONE_ARMY, Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_18);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayFireOneWeaponTwicePerBattleModifier(self, new DuringBattleCondition(), Filters.lightsaber));
        modifiers.add(new PowerModifier(self, new HereEvaluator(self, Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(2)))));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.clone, Filters.here(self)), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }
}
