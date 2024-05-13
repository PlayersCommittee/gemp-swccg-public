package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
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
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 22
 * Type: Starship
 * Subtype: Starfighter
 * Title: Night Buzzard
 */
public class Card222_011 extends AbstractStarfighter {
    public Card222_011() {
        super(Side.DARK, 2, 4, 4, 5, null, 4, 7, "Night Buzzard", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setGameText("May add 1 pilot and 3 passengers. Permanent pilot is •Kuruk, who provides ability of 3. " +
                "Kylo and Knights of Ren deploy -1 to same and related locations. Immune to attrition < 4.");
        addIcons(Icon.EPISODE_VII, Icon.SCOMP_LINK, Icon.FIRST_ORDER, Icon.PILOT, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_22);
        addModelType(ModelType.OUBLIETTE_CLASS_TRANSPORT);
        setPilotCapacity(1);
        setPassengerCapacity(3);
        setMatchingPilotFilter(Filters.Kylo);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(Persona.KURUK, 3) {
                });
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.or(Filters.Kylo, Filters.Knight_of_Ren), -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.or(Filters.Kylo, Filters.Knight_of_Ren), -1, Filters.sameOrRelatedLocation(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
