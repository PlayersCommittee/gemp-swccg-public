package com.gempukku.swccgo.cards.set220.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ShuttlesFreeFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ShuttlesFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 20
 * Type: Location
 * Subtype: System
 * Title: Ralltiir (V)
 */
public class Card220_003 extends AbstractSystem {
    public Card220_003() {
        super(Side.DARK, Title.Ralltiir, 3, ExpansionSet.SET_20, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("If you control, your total power is +1 at Ralltiir sites.");
        setLocationLightSideGameText("Your characters shuttle to and from here for free.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.A_NEW_HOPE, Icon.PLANET, Icon.VIRTUAL_SET_20);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalPowerModifier(self, Filters.Ralltiir_site, new ControlsCondition(playerOnDarkSideOfLocation, self), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ShuttlesFreeFromLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.character), self));
        modifiers.add(new ShuttlesFreeToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.character), self));
        return modifiers;
    }
}