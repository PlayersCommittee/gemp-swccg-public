package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Republic
 * Title: Anakin Skywalker, Padawan Learner
 */
public class Card200_002 extends AbstractRepublic {
    public Card200_002() {
        super(Side.LIGHT, 1, 5, 5, 5, 8, "Anakin Skywalker, Padawan Learner", Uniqueness.UNIQUE);
        setGameText("[Pilot] 3. Deploys -2 aboard Azure Angel. Adds 2 to maneuver of anything he pilots. Players may initiate battles here for free. During battle, your battle destiny draws and Anakin's weapon destiny draws are +1. Immune to attrition < 4.");
        addPersona(Persona.ANAKIN);
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.PADAWAN);
        setMatchingStarshipFilter(Filters.Azure_Angel);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, -2, Filters.Azure_Angel));
        return modifiers;
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
