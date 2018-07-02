package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Green Leader
 */
public class Card9_016 extends AbstractRebel {
    public Card9_016() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Green Leader", Uniqueness.UNIQUE);
        setLore("Leader. Assigned to fly fighter screen for General Calrissian at Endor.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Green Squadron 1, draws on battle destiny if not able to otherwise. Adds 3 to total weapon destiny of any starfighter he pilots firing at a starfighter with lower maneuver.");
        addPersona(Persona.GREEN_LEADER);
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.GREEN_SQUADRON);
        setMatchingStarshipFilter(Filters.Green_Squadron_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starfighterPiloted = Filters.and(Filters.starfighter, Filters.hasPiloting(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Green_Squadron_1), 1));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.any, starfighterPiloted, 3,
                Filters.and(Filters.starfighter, Filters.maneuverLowerThanManeuverOf(self, starfighterPiloted))));
        return modifiers;
    }
}
