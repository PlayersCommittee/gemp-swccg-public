package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.HasAttachedCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeChokedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Character
 * Subtype: Rebel
 * Title: Jyn Erso, Heroic Rebel
 */
public class Card223_005 extends AbstractRebel {
    public Card223_005() {
        super(Side.LIGHT, 2, 4, 4, 3, 5, "Jyn Erso, Heroic Rebel", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Female leader and spy.");
        setGameText("While Stardust on your spy, Jyn adds one battle destiny. " +
                "While Stardust on Jyn and present at a battleground, " +
                "Force drain +1 here and non-trooper Rebel spies here may not be 'choked.' If just lost, " +
                "opponent loses 1 Force.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_23);
        addKeywords(Keyword.FEMALE, Keyword.LEADER, Keyword.SPY);
        addPersona(Persona.JYN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        HasAttachedCondition hasStardustAttached = new HasAttachedCondition(self, Filters.Stardust);
        PresentAtCondition presentAtBattleground = new PresentAtCondition(self, Filters.battleground);
        modifiers.add(new AddsBattleDestinyModifier(self, new OnTableCondition(self, Filters.and(Filters.spy, Filters.hasAttached(Filters.Stardust))), 1));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new AndCondition(hasStardustAttached, presentAtBattleground), 1, self.getOwner()));
        modifiers.add(new MayNotBeChokedModifier(self, Filters.and(Filters.here(self), Filters.and(Filters.not(Filters.trooper), Filters.Rebel, Filters.spy))));
        return modifiers;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
