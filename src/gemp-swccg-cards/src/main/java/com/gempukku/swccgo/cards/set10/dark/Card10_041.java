package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePurchasedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Droid
 * Title: Guri
 */
public class Card10_041 extends AbstractDroid {
    public Card10_041() {
        super(Side.DARK, 2, 6, 6, 7, Title.Guri, Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setArmor(5);
        setLore("Human-replica droid. Programmed to function as Xizor's personal bodyguard and assassin. Black Sun agent. Cost 9 million credits. Worth every decicred.");
        setGameText("Adds 2 to power of anything she pilots. When present with Xizor, he may not be targeted by weapons. While Vader not here, opponent may draw no more than one battle destiny here. Immune to purchase, Restraining Bolt, and attrition < 5.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE, Keyword.BLACK_SUN_AGENT, Keyword.BODYGUARD, Keyword.ASSASSIN);
        addModelTypes(ModelType.ASSASSIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.Xizor, new AndCondition(new PresentWithCondition(self, Filters.Xizor), new NotCondition(new GameTextModificationCondition(self, ModifyGameTextType.LEGACY__TREAT_XIZOR_AS_SHADA)))));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.title("Shada"), new AndCondition(new PresentWithCondition(self, Filters.title("Shada")), new GameTextModificationCondition(self, ModifyGameTextType.LEGACY__TREAT_XIZOR_AS_SHADA))));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self), new NotCondition(new HereCondition(self, Filters.Vader)),
                1, game.getOpponent(self.getOwner())));
        modifiers.add(new MayNotBePurchasedModifier(self));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
