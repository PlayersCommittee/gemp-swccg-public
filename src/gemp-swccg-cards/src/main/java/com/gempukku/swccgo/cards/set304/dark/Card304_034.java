package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Kamjin Lap'lamiz, Sith Battlemaster
 */
public class Card304_034 extends AbstractImperial {
    public Card304_034() {
        super(Side.DARK, 1, 4, 4, 5, 5, "Kamjin Lap'lamiz, Sith Battlemaster", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Following the clanning of Scholae Palatinae Kamjin has risen to the position of Aedile of House Acclivis Draco. Structured like the Emperor's Royal Guard, Kamjin has taken to wearing their red armor.");
		setGameText("Adds 3 to power and 2 to maneuver of anything he pilots. Players may initiate battles here for free. During battle, your battle destiny draws and Kamjin's weapon destiny draws are +1. Immune to attrition < 4.");
        addPersona(Persona.KAMJIN);
        addIcons(Icon.CSP, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.CSP_ROYAL_GUARD, Keyword.LEADER, Keyword.MALE);
        setMatchingStarshipFilter(Filters.Guardian_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter here = Filters.here(self);
        Condition duringBattle = new DuringBattleCondition();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), 2));
        modifiers.add(new MayInitiateBattlesForFreeModifier(self, here, playerId));
        modifiers.add(new MayInitiateBattlesForFreeModifier(self, here, opponent));
        modifiers.add(new EachBattleDestinyModifier(self, here, 1, playerId));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, duringBattle, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
