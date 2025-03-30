package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.evaluators.OutOfPlayEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresDeploymentRestrictionsFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddBattleDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Epic Event
 * Title: Be With Me
 */
public class Card217_029 extends AbstractEpicEventDeployable {
    public Card217_029() {
        super(Side.LIGHT, PlayCardZoneOption.ATTACHED, Title.Be_With_Me, Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setGameText("Deploy on an Ahch-To location. Opponent generates no Force here. " +
                "A Thousand Generations Live In You Now: Rey is power and forfeit +1 for each Jedi out of play. " +
                "Bring Back The Balance, Rey, As I Did: While [Set 14] Rey in battle, your battle destiny draws and Rey's weapon destiny draws are +1. " +
                "Feel The Force Flowing Through You: [Set 14] Rey and characters with her may not add battle destiny draws. " +
                "Rey, The Force Will Be With You, Always: [Set 14] Rey ignores your [Episode VII] objective deployment restrictions.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.AhchTo_location;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter set14Rey = Filters.and(Icon.VIRTUAL_SET_14, Filters.Rey);

        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new GenerateNoForceModifier(self, Filters.hasAttached(self), game.getOpponent(self.getOwner())));
        modifiers.add(new PowerModifier(self, Filters.Rey, new OutOfPlayEvaluator(self, Filters.Jedi)));
        modifiers.add(new ForfeitModifier(self, Filters.Rey, new OutOfPlayEvaluator(self, Filters.Jedi)));
        modifiers.add(new EachBattleDestinyModifier(self, new InBattleCondition(self, set14Rey), 1, self.getOwner()));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, new InBattleCondition(self, set14Rey), set14Rey, 1));
        modifiers.add(new MayNotAddBattleDestinyDrawsModifier(self, Filters.or(set14Rey, Filters.and(Filters.character, Filters.with(self, set14Rey)))));
        modifiers.add(new IgnoresDeploymentRestrictionsFromCardModifier(self, set14Rey, null, self.getOwner(), Filters.and(Filters.your(self), Icon.EPISODE_VII, Filters.Objective)));
        return modifiers;
    }
}