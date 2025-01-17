package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Zev Senesca
 */
public class Card3_027 extends AbstractRebel {
    public Card3_027() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, Title.Zev, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R2);
        setLore("Born on Kestic Station near the Bestine system. Daring pilot who can fly anything. Found Luke and Han in the Hoth wasteland. Piloted Rogue 2 at the Battle of Hoth.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Rogue 2, also adds 3 to maneuver and may draw one battle destiny if not able to otherwise.");
        addIcons(Icon.HOTH, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ROGUE_SQUADRON);
        setMatchingVehicleFilter(Filters.Rogue_2);
        addPersona(Persona.ZEV);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.Rogue_2, Filters.hasPiloting(self)), 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Rogue_2), 1));
        return modifiers;
    }
}
