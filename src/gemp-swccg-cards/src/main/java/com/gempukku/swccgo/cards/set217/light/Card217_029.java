package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.cards.evaluators.OutOfPlayEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddBattleDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Epic Event
 * Title: Be With Me
 */
public class Card217_029 extends AbstractEpicEventDeployable {
    public Card217_029() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Be_With_Me, Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setGameText("If an Ahch-To location on table, deploy on table. " +
                "A Thousand Generations Live In You Now: Rey is power and forfeit +1 for each Jedi on table or out of play. " +
                "Bring Back The Balance, Rey, As I Did: While Rey in battle, your battle destiny draws and Rey's weapon destiny draws are +1. " +
                "Feel The Force Flowing Through You: Rey and characters with her may not add battle destiny draws. " +
                "Rey, The Force Will Be With You, Always: Unless Rey is on Jakku, she adds 1 to your total Force generation.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.AhchTo_location);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Condition reyOnTable = new OnTableCondition(self, Filters.Rey);
        Condition reyNotOnJakku = new NotCondition(new OnCondition(self, Filters.Rey, Title.Jakku));

        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new PowerModifier(self, Filters.Rey, new AddEvaluator(new OnTableEvaluator(self, Filters.Jedi), new OutOfPlayEvaluator(self, Filters.Jedi))));
        modifiers.add(new ForfeitModifier(self, Filters.Rey, new AddEvaluator(new OnTableEvaluator(self, Filters.Jedi), new OutOfPlayEvaluator(self, Filters.Jedi))));
        modifiers.add(new EachBattleDestinyModifier(self, new InBattleCondition(self, Filters.Rey), 1, playerId));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, new InBattleCondition(self, Filters.Rey), Filters.Rey, 1));
        modifiers.add(new MayNotAddBattleDestinyDrawsModifier(self, Filters.or(Filters.Rey, Filters.and(Filters.character, Filters.with(self, Filters.Rey)))));
        modifiers.add(new TotalForceGenerationModifier(self, new AndCondition(reyOnTable, reyNotOnJakku), 1, playerId));
        return modifiers;
    }
}
