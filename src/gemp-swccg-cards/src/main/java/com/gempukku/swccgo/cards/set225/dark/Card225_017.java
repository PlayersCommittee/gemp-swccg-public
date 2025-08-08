package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Location
 * Subtype: Site
 * Title: Crait: Salt Plateau
 */
public class Card225_017 extends AbstractSite {
    public Card225_017() {
        super(Side.DARK, Title.Crait_Salt_Plateau, Title.Crait, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLocationDarkSideGameText("Your combat vehicles are power and defense value +1 here.");
        setLocationLightSideGameText("While The Resistance Is Doomed on table, Force drains here may not be canceled or reduced.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_VII, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.combat_vehicle, Filters.here(self)), 1));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.combat_vehicle, Filters.here(self)), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition theResistanceIsDoomedOnTable = new OnTableCondition(self, Filters.The_Resistance_Is_Doomed);
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.here(self), theResistanceIsDoomedOnTable));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, Filters.here(self), theResistanceIsDoomedOnTable));
        return modifiers;
    }
}