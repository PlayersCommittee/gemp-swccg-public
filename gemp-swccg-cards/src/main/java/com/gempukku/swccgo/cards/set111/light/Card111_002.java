package com.gempukku.swccgo.cards.set111.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentAstromech;
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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployForFreeForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeAboardModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Third Anthology)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Artoo-Detoo In Red 5
 */
public class Card111_002 extends AbstractStarfighter {
    public Card111_002() {
        super(Side.LIGHT, 0, 4, 3, null, 4, 5, 5, "Artoo-Detoo In Red 5", Uniqueness.UNIQUE, ExpansionSet.THIRD_ANTHOLOGY, Rarity.PM);
        setAlternateDestiny(7);
        setLore("R2-D2 saved Luke and his starfighter more times than the young pilot could count.");
        setGameText("May add 1 pilot. Permanent astromech aboard is *R2-D2, who adds 2 to power, maneuver and hyperspeed. Luke of ability < 5 deploys free aboard. Immune to attrition < 5 when Luke piloting.");
        addPersonas(Persona.RED_5);
        addIcons(Icon.PREMIUM, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.RED_SQUADRON);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Luke);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployForFreeForSimultaneouslyDeployingPilotModifier(self, Filters.and(Filters.Luke, Filters.abilityLessThan(5))));
        return modifiers;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeAboardModifier(self, Filters.and(Filters.Luke, Filters.abilityLessThan(5)), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, self, new HasPilotingCondition(self, Filters.Luke), 5));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentAstromech(Persona.R2D2) {
                        @Override
                        public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                            List<Modifier> modifiers = new LinkedList<Modifier>();
                            modifiers.add(new PowerModifier(self, 2));
                            modifiers.add(new ManeuverModifier(self, 2));
                            modifiers.add(new HyperspeedModifier(self, 2));
                            return modifiers;
                        }});
    }
}
