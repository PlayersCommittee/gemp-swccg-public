package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Starship
 * Subtype: Capital
 * Title: Hound's Tooth
 */
public class Card4_168 extends AbstractCapitalStarship {
    public Card4_168() {
        super(Side.DARK, 2, 6, 5, 4, null, 4, 4, "Hound's Tooth", Uniqueness.UNIQUE);
        setLore("Controlled by state-of-the-art-voice-activated X10-D computers. Internal sensors and security systems monitor prisoner activity. Modified for Bossk's Trandoshan physiology.");
        setGameText("May add 1 pilot (must be smuggler or bounty hunter), 6 passengers and 1 vehicle. Immune to attrition < 4 if Bossk piloting. Deploys and moves like a starfighter. Has ship-docking capability.");
        addIcons(Icon.DAGOBAH, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MODIFIED_CORELLIAN_FREIGHTER);
        addPersona(Persona.HOUNDS_TOOTH);
        setPilotCapacity(1);
        setPassengerCapacity(6);
        setVehicleCapacity(1);
        setMatchingPilotFilter(Filters.Bossk);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.smuggler, Filters.bounty_hunter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Bossk), 4));
        return modifiers;
    }

    @Override
    public boolean isDeploysAndMovesLikeStarfighter() {
        return true;
    }
}
