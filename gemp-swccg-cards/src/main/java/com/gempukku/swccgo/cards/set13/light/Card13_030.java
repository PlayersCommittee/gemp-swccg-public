package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: Let's Keep A Little Optimism Here
 */
public class Card13_030 extends AbstractDefensiveShield {
    public Card13_030() {
        super(Side.LIGHT, Title.Lets_Keep_A_Little_Optimism_Here);
        setLore("The heroes of the Rebellion know that where there is life, there is hope.");
        setGameText("Plays on table. While you occupy a Renegade planet location, operatives are forfeit = 0, operatives do not add to Force drains and your Force drains may not be reduced. (Renegade planet is defined on the Objective card Imperial Occupation.)");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition occupyRenegadePlanetLocation = new OccupiesCondition(playerId, Filters.Renegade_planet_location);
        Filter operatives = Filters.and(Filters.operative, Filters.canBeTargetedBy(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetForfeitModifier(self, operatives, occupyRenegadePlanetLocation, 0));
        modifiers.add(new CancelForceDrainBonusesFromCardModifier(self, operatives, occupyRenegadePlanetLocation));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, occupyRenegadePlanetLocation, playerId));
        return modifiers;
    }
}