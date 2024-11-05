package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.DrawDestinyFromBottomOfDeckModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Starship
 * Subtype: Starfighter
 * Title: Guardian 1
 */
public class Card304_032 extends AbstractStarfighter {
    public Card304_032() {
        super(Side.DARK, 3, 3, 3, null, 4, 2, 3, "Guardian 1", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("First production run of TIE defender design. Powerful P-sz9.7 sublight engines. Fire-linked laser cannons. Aft blind spot exploited by maneuverable enemy starfighters.");
        setGameText("May add 1 pilot. May deploy as a 'react'. Any starship cannon may deploy aboard. While Kamjin piloting, immune to attrition < 4 and during battle, players draw destiny from the bottom of their Reserve Deck.");
        addPersona(Persona.GUARDIAN_1);
		addIcons(Icon.CSP, Icon.NAV_COMPUTER);
        addModelTypes(ModelType.TIE_DEFENDER);
		setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Kamjin);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Kamjin), 4));
        modifiers.add(new DrawDestinyFromBottomOfDeckModifier(self, new AndCondition(new HasPilotingCondition(self, Filters.Kamjin), new DuringBattleWithParticipantCondition(self))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.starship_cannon, Filters.starship_weapon_that_deploys_on_starfighters), self));
        return modifiers;
    }
}
