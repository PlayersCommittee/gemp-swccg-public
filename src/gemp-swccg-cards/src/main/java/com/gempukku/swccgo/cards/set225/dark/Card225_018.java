package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionLimitedToModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 25
 * Type: Character
 * Subtype: Imperial
 * Title: DS-61-3 (V)
 */
public class Card225_018 extends AbstractImperial {
    public Card225_018() {
        super(Side.DARK, 2, 2, 3, 2, 4, Title.DS_61_3, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Pilot DS-61-3. Vader's right wingman. Flies Black 3. Reputation for ferocity in combat. Corellian pilot with excellent skills. Nicknamed 'Backstabber.'");
        setGameText("[Pilot] 3. While piloting a dreadnaught or starfighter, draws one battle destiny if unable to otherwise and opponent's immunity to attrition here is limited to < 7 (< 5 if piloting Black 3 or with Vader). Your non-[Coruscant] interrupts with 'Back' in the title are destiny +2.");
        addPersona(Persona.DS_61_3);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_25);
        addKeyword(Keyword.BLACK_SQUADRON);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Black_3);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingDreadnaughtOrStarfighter = new PilotingCondition(self, Filters.or(Filters.Dreadnaught, Filters.starfighter));
        Condition pilotingBlack3 = new PilotingCondition(self, Filters.Black_3);
        Condition withVader = new WithCondition(self, Filters.Vader);
        Condition pilotingBlack3OrWithVader = new OrCondition(pilotingBlack3, withVader);
        Filter yourBackInterrupts = Filters.and(Filters.your(self), Filters.not(Icon.CORUSCANT), Filters.titleContains("Back"));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingDreadnaughtOrStarfighter, 1));
        modifiers.add(new ImmunityToAttritionLimitedToModifier(self, Filters.and(Filters.opponents(self.getOwner()), Filters.here(self)), pilotingDreadnaughtOrStarfighter,
            new ConditionEvaluator(7, 5, pilotingBlack3OrWithVader)));
        modifiers.add(new DestinyModifier(self, yourBackInterrupts, 2));

        return modifiers;
    }
}
