package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AbilityRequiredForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Location
 * Subtype: System
 * Title: Kashyyyk
 */
public class Card2_064 extends AbstractSystem {
    public Card2_064() {
        super(Side.LIGHT, Title.Kashyyyk, 6, ExpansionSet.A_NEW_HOPE, Rarity.C1);
        setLocationDarkSideGameText("Total ability of 6 or more required for you to draw battle destiny here.");
        setLocationLightSideGameText("Your Wookiees and smugglers deploy -2 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.A_NEW_HOPE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AbilityRequiredForBattleDestinyModifier(self, self, 6, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List< Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.or(Filters.Wookiee, Filters.smuggler)), -2, self));
        return modifiers;
    }
}