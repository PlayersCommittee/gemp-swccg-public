package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Green Squadron A-wing
 */
public class Card9_073 extends AbstractStarfighter {
    public Card9_073() {
        super(Side.LIGHT, 3, 4, 3, null, 5, 4, 3, "Green Squadron A-wing", Uniqueness.RESTRICTED_3);
        setLore("A-wing starfighters from Green Squadron. Assigned mission of screening Rebel capital starships from enemy fighters and bombers.");
        setGameText("Deploy -1 to Sullust or same system or sector as Green Leader. Permanent pilot provides ability of 2 and adds 2 to power. Power -1 when opponent has a starfighter present with a higher maneuver.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.A_WING);
        addKeywords(Keyword.GREEN_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.or(Filters.Deploys_at_Sullust, Filters.sameSystemOrSectorAs(self, Filters.Green_Leader))));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(2) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 2));
                        return modifiers;
                    }});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new PresentCondition(self, Filters.and(Filters.opponents(self),
                Filters.starfighter, Filters.maneuverHigherThanManeuverOf(self))), -1));
        return modifiers;
    }
}
