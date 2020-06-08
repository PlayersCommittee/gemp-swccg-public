package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Green Squadron Pilot
 */
public class Card9_017 extends AbstractRebel {
    public Card9_017() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Green Squadron Pilot", Uniqueness.RESTRICTED_3);
        setLore("Many top X-wing pilots transferred to A-wing squadrons when the new starfighter entered service. The best A-wing pilots were selected to fly for Green Squadron at the Battle of Endor.");
        setGameText("Adds 2 to power of anything he pilots. When piloting an A-wing, draws one battle destiny if not able to otherwise and is forfeit +2.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.GREEN_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingAwing = new PilotingCondition(self, Filters.A_wing);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingAwing, 1));
        modifiers.add(new ForfeitModifier(self, pilotingAwing, 2));
        return modifiers;
    }
}
