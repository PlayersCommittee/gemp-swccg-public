package com.gempukku.swccgo.cards.set205.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 5
 * Type: Starship
 * Subtype: Starfighter
 * Title: Ric In Queen's Royal Starship
 */
public class Card205_009 extends AbstractStarfighter {
    public Card205_009() {
        super(Side.LIGHT, 2, 5, 6, 5, null, 7, 7, "Ric In Queen's Royal Starship", Uniqueness.UNIQUE);
        setLore("Chromium-plated, sleek transport ship used by the royalty of the Naboo. Spaceframe was designed around a J-type configuration.");
        setGameText("May add 1 pilot and 5 passengers. Permanent pilot is •Ric, who provides ability of 3. R2-D2 deploys -2 aboard. While We'll Need A New One on table, adds one battle destiny. Immune to attrition < 5.");
        addPersona(Persona.QUEENS_ROYAL_STARSHIP);
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_5);
        addModelType(ModelType.J_TYPE_327_NUBIAN);
        setPilotCapacity(1);
        setPassengerCapacity(5);
        setMatchingPilotFilter(Filters.R2D2);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.RIC, 3) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.R2D2, -2, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new OnTableCondition(self, Filters.Well_Need_A_New_One), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
