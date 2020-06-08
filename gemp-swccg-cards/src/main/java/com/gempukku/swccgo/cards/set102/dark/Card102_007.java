package com.gempukku.swccgo.cards.set102.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Jedi Pack)
 * Type: Location
 * Subtype: System
 * Title: Eriadu
 */
public class Card102_007 extends AbstractSystem {
    public Card102_007() {
        super(Side.DARK, Title.Eriadu, 1);
        setLocationDarkSideGameText("Tarkin deploys free here. If you control, all Imperials with ability > 2 on table are forfeit +2.");
        setLocationLightSideGameText("If you control, Force drain +1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PREMIUM, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Tarkin, self));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Imperial, Filters.abilityMoreThan(2), Filters.onTable),
                new ControlsCondition(playerOnDarkSideOfLocation, self), 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnLightSideOfLocation, self), 1, playerOnLightSideOfLocation));
        return modifiers;
    }
}