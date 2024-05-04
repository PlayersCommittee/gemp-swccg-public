package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHavePowerReducedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotSubstituteBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Character
 * Subtype: Republic
 * Title: Admiral Yularen
 */
public class Card221_045 extends AbstractRepublic {
    public Card221_045() {
        super(Side.LIGHT, 1, 3, 3, 3, 5, "Admiral Yularen", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLore("Leader.");
        setGameText("[Pilot] 3. While piloting a [Clone Army] capital starship, adds one battle destiny and its power may not be reduced by opponent. While with Anakin (or while piloting Resolute), opponent may not cancel or substitute battle destiny draws here.");
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.LEADER, Keyword.ADMIRAL);
        addPersona(Persona.YULAREN);
        setMatchingStarshipFilter(Filters.title("Resolute"));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition pilotingResoluteOrWithAnakin = new OrCondition(new PilotingCondition(self, Filters.title("Resolute")), new WithCondition(self, Filters.Anakin));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new PilotingCondition(self, Filters.and(Icon.CLONE_ARMY, Filters.capital_starship)), 1));
        modifiers.add(new MayNotHavePowerReducedModifier(self, Filters.and(Icon.CLONE_ARMY, Filters.capital_starship, Filters.hasPiloting(self)), opponent));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, Filters.here(self), pilotingResoluteOrWithAnakin, opponent));
        modifiers.add(new MayNotSubstituteBattleDestinyModifier(self, Filters.here(self), pilotingResoluteOrWithAnakin, opponent));
        return modifiers;
    }
}