package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Starship
 * Subtype: Starfighter
 * Title: Colonel Jendon In Vader's Personal Shuttle
 */
public class Card221_001 extends AbstractStarfighter {
    public Card221_001() {
        super(Side.DARK, 3, 5, 5, null, 5, 3, 5, "Colonel Jendon In Vader's Personal Shuttle", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLore("Customized transport of Lord Vader. Employs advanced sensor jamming gear. Modified with enhanced tactical displays constructed to the Dark Lord's specifications.");
        setGameText("Deploys -2 to Mustafar. May add 1 pilot and 3 passengers. Permanent pilot is •Jendon, who provides ability of 2. If Vader armed with a lightsaber weapon card at a battleground, Force drain +1 here.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_21);
        addModelTypes(ModelType.LAMBDA_CLASS_SHUTTLE);
        addPersona(Persona.VADERS_PERSONAL_SHUTTLE);
        setPilotCapacity(1);
        setPassengerCapacity(3);
        setMatchingPilotFilter(Filters.Vader);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Mustafar_location));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.JENDON, 2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new OnTableCondition(self, Filters.and(Filters.Vader, Filters.at(Filters.battleground), Filters.armedWith(Filters.and(Filters.lightsaber, Filters.weapon)))), 1, self.getOwner()));
        return modifiers;
    }
}
