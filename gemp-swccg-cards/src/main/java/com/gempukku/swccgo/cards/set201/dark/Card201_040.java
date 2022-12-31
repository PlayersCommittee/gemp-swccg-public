package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Starship
 * Subtype: Starfighter
 * Title: Slave I, Symbol Of Fear
 */
public class Card201_040 extends AbstractStarfighter {
    public Card201_040() {
        super(Side.DARK, 3, 3, 3, null, 5, 5, 5, "Slave I, Symbol Of Fear", Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setLore("Originally designed as a planetary defense craft. Uses restricted jamming technology, allowing it to appear out of nowhere. Contains many hidden armaments.");
        setGameText("May add 2 pilots and 2 passengers. Power +1 for each opponent's starship here. While Jango or Boba Fett piloting, immune to attrition < 5.");
        addPersona(Persona.SLAVE_I);
        addIcons(Icon.EPISODE_I, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_1);
        addModelType(ModelType.FIRESPRAY_CLASS_ATTACK_SHIP);
        setPilotCapacity(2);
        setPassengerCapacity(2);
        setMatchingPilotFilter(Filters.or(Filters.Jango_Fett, Filters.Boba_Fett));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new HereEvaluator(self, Filters.and(Filters.opponents(self), Filters.starship))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.or(Filters.Jango_Fett, Filters.Boba_Fett)), 5));
        return modifiers;
    }
}
