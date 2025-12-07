package com.gempukku.swccgo.cards.set226.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.HitCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 26
 * Type: Character
 * Subtype: Droid
 * Title: Guri (V)
 */
public class Card226_005 extends AbstractDroid {
    public Card226_005() {
        super(Side.DARK, 2, 4, 6, 6, Title.Guri, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setArmor(5);
        setLore("Human-replica droid. Programmed to function as Xizor's personal bodyguard and assassin. Black Sun agent. Cost 9 million credits. Worth every decicred.");
        setGameText("[Pilot] 2. While Guri and Xizor present at same site, Force drain +1 here and Xizor may not be targeted by weapons (unless Guri 'hit'). While your [Reflections II] objective on table, draws one battle destiny if unable to otherwise and immune to attrition < 5");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR, Icon.PRESENCE, Icon.VIRTUAL_SET_26);
        addKeywords(Keyword.FEMALE, Keyword.BLACK_SUN_AGENT, Keyword.BODYGUARD, Keyword.ASSASSIN);
        addModelTypes(ModelType.ASSASSIN);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        String playerId = self.getOwner();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        Condition presentWithXizor = new PresentWithCondition(self, Filters.and(Filters.Xizor, Filters.present(self)));
        modifiers.add(new ForceDrainModifier(self, Filters.sameSite(self), presentWithXizor, 1, playerId));
        Condition presentWithXizorAndNotHit = new AndCondition(presentWithXizor, new NotCondition(new HitCondition(self)));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.Xizor, presentWithXizorAndNotHit));
        Condition yourRef2ObjectiveOnTable = new OnTableCondition(self, Filters.and(Filters.your(self), Icon.REFLECTIONS_II, Filters.Objective));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, yourRef2ObjectiveOnTable, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, yourRef2ObjectiveOnTable, 5));
        return modifiers;
    }
}
