package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Commander Luke Skywalker
 */
public class Card3_003 extends AbstractRebel {
    public Card3_003() {
        super(Side.LIGHT, 1, 4, 4, 4, 7, "Commander Luke Skywalker", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R1);
        setLore("Hero of Yavin. Promoted to Commander in his third year of military training with the Alliance. Squadron flight leader at Echo Base during the Battle of Hoth.");
        setGameText("Deploys only on Hoth. Adds 3 to power of anything he pilots. When piloting Rogue 1, also adds 2 to maneuver. Immune to attrition < 3. Adds 1 to forfeit of each other Rogue Squadron pilot or gunner at same Hoth site.");
        addPersona(Persona.LUKE);
        addIcons(Icon.HOTH, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER, Keyword.ROGUE_SQUADRON);
        setMatchingVehicleFilter(Filters.Rogue_1);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Hoth;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.Rogue_1, Filters.hasPiloting(self)), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.other(self), Filters.or(Filters.Rogue_Squadron_pilot,
                Filters.Rogue_Squadron_gunner), Filters.atSameSite(self)), new AtCondition(self, Filters.Hoth_site), 1));
        return modifiers;
    }
}
