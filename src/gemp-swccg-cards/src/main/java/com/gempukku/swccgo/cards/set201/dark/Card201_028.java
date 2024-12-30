package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePlacedOutOfPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Effect
 * Title: Despair (V)
 */
public class Card201_028 extends AbstractNormalEffect {
    public Card201_028() {
        super(Side.DARK, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Despair, Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("The carbonite froze more than just Han's body.");
        setGameText("Deploy on table. My Favorite Decoration may not be placed out of play. At battlegrounds where you have a frozen captive, your Force drains are +1. While a frozen captive with Scum And Villainy, your total power in all battles is +3. [Immune to Alter.]");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_1);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.battleground, Filters.sameLocationAs(self, SpotOverride.INCLUDE_CAPTIVE, Filters.frozenCaptive)), 1, playerId));
        modifiers.add(new MayNotBePlacedOutOfPlayModifier(self, Filters.My_Favorite_Decoration));
        modifiers.add(new TotalPowerModifier(self, Filters.battleLocation, new AndCondition(new DuringBattleCondition(), new OnTableCondition(self, Filters.and(Filters.Scum_And_Villainy, Filters.with(self, SpotOverride.INCLUDE_CAPTIVE, Filters.frozenCaptive)))), 3, playerId));
        return modifiers;
    }
}