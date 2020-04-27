package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Effect
 * Title: Despair (V)
 */
public class Card201_028 extends AbstractNormalEffect {
    public Card201_028() {
        super(Side.DARK, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Despair, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("The carbonite froze more than just Han's body.");
        setGameText("Deploy on table. My Favorite Decoration may not be placed out of play. At same site as Jabba's Prize, opponent's characters deploy +1 and your Force Drains are +1. While Jabba's Prize with Scum and Villainy, your total power in battles is +3. [Immune to Alter]");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_1);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter sameSiteAsJabbasPrize = Filters.sameSiteAs(self, SpotOverride.INCLUDE_CAPTIVE, Filters.Jabbas_Prize);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.character), 1, sameSiteAsJabbasPrize));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.site, sameSiteAsJabbasPrize), 1, playerId));
        modifiers.add(new MayNotBePlacedOutOfPlayModifier(self, Filters.My_Favorite_Decoration));
        modifiers.add(new TotalPowerModifier(self, Filters.any, new AndCondition(new DuringBattleCondition(), new AtCondition(self, Filters.Scum_And_Villainy, sameSiteAsJabbasPrize)), 3, playerId));
        return modifiers;
    }
}