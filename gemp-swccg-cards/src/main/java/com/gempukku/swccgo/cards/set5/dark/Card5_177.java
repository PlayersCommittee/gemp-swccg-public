package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Starship
 * Subtype: Starfighter
 * Title: Slave I
 */
public class Card5_177 extends AbstractStarfighter {
    public Card5_177() {
        super(Side.DARK, 1, 5, 4, null, 4, 4, 4, "Slave I", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Originally designed as a planetary defense craft. Uses restricted jamming technology, allowing it to appear out of nowhere. Contains many hidden armaments.");
        setGameText("May add 1 pilot (must be a bounty hunter) and 3 passengers. May deploy with a pilot as a 'react'. Immune to attrition < 5 if Boba Fett piloting. Has ship-docking capability.");
        addIcons(Icon.CLOUD_CITY, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.FIRESPRAY_CLASS_ATTACK_SHIP);
        addPersona(Persona.SLAVE_I);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY);
        setPilotCapacity(1);
        setPassengerCapacity(3);
        setMatchingPilotFilter(Filters.Boba_Fett);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.bounty_hunter;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Boba_Fett), 5));
        return modifiers;
    }
}
