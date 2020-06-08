package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Vehicle
 * Subtype: Combat
 * Title: Chewie's AT-ST
 */
public class Card8_081 extends AbstractCombatVehicle {
    public Card8_081() {
        super(Side.LIGHT, 4, 2, 3, 4, null, 3, 5, "Chewie's AT-ST", Uniqueness.UNIQUE);
        setLore("Enclosed. Chewie's daring capture of an Imperial AT-ST helped turn the tide in the Battle of Endor.");
        setGameText("Deploy only to Endor or same site as Chewie or opponent's AT-ST. May add 2 pilots or passengers. Subtracts 1 from power of other AT-STs at same site. Immune to attrition < 4 when Chewie piloting.");
        addModelType(ModelType.AT_ST);
        addIcons(Icon.ENDOR, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotOrPassengerCapacity(2);
        setMatchingPilotFilter(Filters.Chewie);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_on_Endor, Filters.locationAndCardsAtLocation(Filters.sameSiteAs(self, Filters.or(Filters.Chewie, Filters.and(Filters.opponents(self), Filters.AT_ST)))));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.other(self), Filters.AT_ST, Filters.atSameSite(self)), -1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Chewie), 4));
        return modifiers;
    }
}
