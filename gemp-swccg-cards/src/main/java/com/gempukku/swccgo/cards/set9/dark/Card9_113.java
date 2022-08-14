package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Lord Vader
 */
public class Card9_113 extends AbstractImperial {
    public Card9_113() {
        super(Side.DARK, 1, 8, 7, 6, 8, "Lord Vader", Uniqueness.UNIQUE);
        setLore("Forgiving administrator of Imperial policy. Emperor Palpatine's most trusted leader. Believes converting Skywalker is key to the Alliance's downfall.");
        setGameText("Deploys -2 to Executor, Death Star II or Endor. Adds 3 to power of anything he pilots. While armed with a lightsaber, adds 2 to his defense value and 1 to each of his lightsaber weapon destiny draws. Immune to Uncontrollable Fury and attrition < 6.");
        addPersona(Persona.VADER);
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER);
        setMatchingStarshipFilter(Filters.Executor);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, -2, Filters.or(Filters.Deploys_aboard_Executor, Filters.Deploys_at_Death_Star_II, Filters.Deploys_at_Endor)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition armedWithLightsaber = new ArmedWithCondition(self, Filters.lightsaber);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DefenseValueModifier(self, armedWithLightsaber, 2));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 1, Filters.lightsaber, Filters.any));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Uncontrollable_Fury));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }
}
