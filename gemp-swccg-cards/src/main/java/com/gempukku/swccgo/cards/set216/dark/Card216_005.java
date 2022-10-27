package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationImmuneToCancelModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendModifierEffectsModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Defensive Shield
 * Title: Crossfire (V)
 */
public class Card216_005 extends AbstractDefensiveShield {
    public Card216_005() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Crossfire, ExpansionSet.SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLore("Scout walker pilots are trained to set up a deadly heavy fire zone. This tactic can be disrupted by enemy weapons fire.");
        setGameText("Plays on table. S-foils and Maneuvering Flaps are suspended where you have either a weapon present or a starship (or vehicle) with maneuver > 3 present. Your Force generation at opponent's Endor system may not be canceled unless opponent's [Endor] objective on table.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.locationAndCardsAtLocation(Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.or(Filters.weapon,
                Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.maneuverMoreThan(3))))));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new SuspendModifierEffectsModifier(self, filter, Filters.or(Filters.S_foils, Filters.Maneuvering_Flaps)));
        modifiers.add(new ForceGenerationImmuneToCancelModifier(self, Filters.and(Filters.opponents(self), Filters.Endor_system),
                new UnlessCondition(new OnTableCondition(self, Filters.and(Filters.opponents(self), Icon.ENDOR, Filters.Objective))), Filters.opponents(self), self.getOwner()));
        return modifiers;
    }
}