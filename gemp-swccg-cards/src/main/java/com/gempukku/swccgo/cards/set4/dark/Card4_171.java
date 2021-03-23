package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostWithPilotModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Starship
 * Subtype: Starfighter
 * Title: Punishing One
 */
public class Card4_171 extends AbstractStarfighter {
    public Card4_171() {
        super(Side.DARK, 3, 3, 2, null, 5, 3, 3, "Punishing One", Uniqueness.UNIQUE);
        setLore("Old Corellian Engineering Corporation starfighter. Has outdated Class Three hyperdrive, but high sublight speed capability. Easy to maintain. Owned by Dengar.");
        setGameText("May add 1 pilot (must be a smuggler or bounty hunter) and 1 passenger. Immune to attrition < 3 if Dengar piloting. Deploy -3 when deploying with Dengar. Boosted TIE Cannon may deploy aboard.");
        addIcons(Icon.DAGOBAH, Icon.INDEPENDENT, Icon.NAV_COMPUTER);
        addModelType(ModelType.CORELLIAN_JM_5000);
        addPersona(Persona.PUNISHING_ONE);
        setPilotCapacity(1);
        setPassengerCapacity(1);
        setMatchingPilotFilter(Filters.Dengar);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.smuggler, Filters.bounty_hunter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Dengar), 3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostWithPilotModifier(self, -3, Filters.Dengar));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.Boosted_TIE_Cannon), self));
        return modifiers;
    }
}
