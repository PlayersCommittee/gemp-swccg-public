package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Starship
 * Subtype: Starfighter
 * Title: IG-2000
 */
public class Card4_169 extends AbstractStarfighter {
    public Card4_169() {
        super(Side.DARK, 4, 4, 3, null, 3, 3, 3, "IG-2000", Uniqueness.UNIQUE);
        setLore("IG-88's assault starfighter. Custom designed. Boasts a Kuat Galaxy-15 engine from a Nebulon-B frigate. Heavy ion cannon often used to disable starships before boarding.");
        setGameText("May add 1 pilot (must be a smuggler or bounty hunter) and 2 passengers. Maneuver +3 and immune to attrition < 3 if IG-88 piloting. Ion Cannon may deploy aboard.");
        addIcons(Icon.DAGOBAH, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.TRILON_AGGRESSOR);
        addPersona(Persona.IG2000);
        setPilotCapacity(1);
        setPassengerCapacity(2);
        setMatchingPilotFilter(Filters.IG88);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.smuggler, Filters.bounty_hunter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition ig88Piloting = new HasPilotingCondition(self, Filters.IG88);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, ig88Piloting, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, ig88Piloting, 3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.Ion_Cannon, self));
        return modifiers;
    }
}
