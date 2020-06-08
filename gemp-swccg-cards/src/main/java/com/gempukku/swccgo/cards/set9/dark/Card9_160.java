package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Onyx 1
 */
public class Card9_160 extends AbstractStarfighter {
    public Card9_160() {
        super(Side.DARK, 2, 3, 3, null, 4, 3, 4, "Onyx 1", Uniqueness.UNIQUE);
        setLore("Designed to emulate Rebel starfighter advantages. Production began shortly before the Battle of Endor. Armed with laser cannons, ion cannons and missile launchers.");
        setGameText("May deploy -2 with a pilot as a 'react' to same location as any Imperial-class Star Destroyer. May add 1 pilot. Any starship cannon may deploy aboard. Immune to attrition < 4 when Jendon piloting.");
        addPersona(Persona.ONYX_1);
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER);
        addKeywords(Keyword.ONYX_SQUADRON);
        addModelType(ModelType.TIE_DEFENDER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Jendon);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.sameLocationAs(self, Filters.Imperial_class_Star_Destroyer), -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.starship_cannon, Filters.starship_weapon_that_deploys_on_starfighters), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Jendon), 4));
        return modifiers;
    }
}
