package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtSameLocationAsCondition;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Derek 'Hobbie' Klivian
 */
public class Card3_005 extends AbstractRebel {
    public Card3_005() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, Title.Hobbie, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.U1);
        setLore("Defected from the Empire with Biggs Darklighter. Aided in mutiny aboard the transport Rand Ecliptic. Served in the Ecliptic Evaders. Luke's wingman at the Battle of Hoth.");
        setGameText("Power +2 when at same site as Biggs. Adds 2 to power of anything he pilots (3 if a Star Destroyer is at same location). When piloting Rogue 4, also adds 2 to maneuver.");
        addIcons(Icon.HOTH, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ROGUE_SQUADRON);
        setMatchingVehicleFilter(Filters.Rogue_4);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, Filters.Biggs), 2));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new ConditionEvaluator(2, 3, new AtSameLocationAsCondition(self, Filters.Star_Destroyer))));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.Rogue_4, Filters.hasPiloting(self)), 2));
        return modifiers;
    }
}
