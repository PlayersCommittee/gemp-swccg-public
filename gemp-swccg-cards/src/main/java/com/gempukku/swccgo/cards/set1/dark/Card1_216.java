package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Fear Will Keep Them In Line
 */
public class Card1_216 extends AbstractNormalEffect {
    public Card1_216() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Fear_Will_Keep_Them_In_Line);
        setLore("'The regional governors now have direct control over their territories. Fear will keep the local systems in line. Fear of this battle station.'");
        setGameText("Deploy on any capital starship. When that starship is at a system or sector you control, your total power is +1 in battles at related sites.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.capital_starship;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition atSystemOrSectorYouControl = new AtCondition(self, Filters.and(Filters.system_or_sector, Filters.controls(playerId)));
        Filter relatedBattleSitesToAddPower = Filters.and(Filters.relatedSite(self), Filters.locationWherePowerCanBeAddedInBattleFromStarshipsControllingSystem(playerId));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, relatedBattleSitesToAddPower, atSystemOrSectorYouControl, 1, playerId));
        modifiers.add(new AttritionModifier(self, relatedBattleSitesToAddPower, new AndCondition(atSystemOrSectorYouControl,
                new GameTextModificationCondition(self, ModifyGameTextType.FEAR_WILL_KEEP_THEM_IN_LINE__ADDS_1_TO_ATTRITION)),
                1, opponent));
        return modifiers;
    }
}