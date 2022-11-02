package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.InSenateMajorityCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.AtSameSiteEvaluator;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.CardState;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeSuspendedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.PoliticsModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 17
 * Type: Character
 * Subtype: Republic
 * Title: Passel Argente (V)
 */
public class Card217_018 extends AbstractRepublic {
    public Card217_018() {
        super(Side.DARK, 2, 3, 1, 2, 3, "Passel Argente", Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setPolitics(2);
        setVirtualSuffix(true);
        setLore("A senator known for his ability to deflect blame. It is rumored that Argente receives kickbacks from a few corporations to thwart other companies' developments.");
        setGameText("Agendas: ambition, taxation. While in a senate majority, if your only [Coruscant] Political Effect on table is not This Is Outrageous!, it may not be canceled or suspended. Argente is politics +X, where X = number of opponent's characters here.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.VIRTUAL_SET_17);
        addKeywords(Keyword.SENATOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AgendaModifier(self, Agenda.AMBITION, Agenda.TAXATION));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition inSenateMajority = new InSenateMajorityCondition(self);
        final Filter active = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.getCardState(gameState, physicalCard, false, false, false,
                        false, false, false, false, false) == CardState.ACTIVE;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return modifiersQuerying.getCardState(gameState, builtInCardBlueprint.getPhysicalCard(gameState.getGame()), false, false, false,
                        false, false, false, false, false) == CardState.ACTIVE;
            }
        };

        Filter yourCoruscantPoliticalEffectOnTable = Filters.and(Filters.your(self), Filters.onTable, Filters.Political_Effect, Icon.CORUSCANT);
        Condition exactlyOneOnTable = new OnTableCondition(self, 1, true, yourCoruscantPoliticalEffectOnTable);
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeSuspendedModifier(self, Filters.and(active, yourCoruscantPoliticalEffectOnTable, Filters.not(Filters.title(Title.This_Is_Outrageous))),
                new AndCondition(inSenateMajority, exactlyOneOnTable)));
        modifiers.add(new MayNotBeCanceledModifier(self, Filters.and(active, yourCoruscantPoliticalEffectOnTable, Filters.not(Filters.title(Title.This_Is_Outrageous))),
                new AndCondition(inSenateMajority, exactlyOneOnTable)));

        modifiers.add(new PoliticsModifier(self, new AtSameSiteAsCondition(self, Filters.and(Filters.opponents(self), Filters.character)), new AtSameSiteEvaluator(self, Filters.and(Filters.opponents(self), Filters.character))));
        return modifiers;
    }
}
