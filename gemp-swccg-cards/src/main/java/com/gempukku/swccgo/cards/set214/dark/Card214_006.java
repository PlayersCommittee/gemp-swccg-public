package com.gempukku.swccgo.cards.set214.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.HyperspeedWhenMovingFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Location
 * Subtype: System
 * Title: Kijimi
 */
public class Card214_006 extends AbstractSystem {
    public Card214_006() {
        super(Side.DARK, Title.Kijimi, 6);
        setLocationDarkSideGameText("If you control, your starships are hyperspeed +1 when moving from here.");
        setLocationLightSideGameText("Poe and your smugglers may deploy here as a 'react.'");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_14, Icon.EPISODE_VII);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new HyperspeedWhenMovingFromLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.starship),
                new ControlsCondition(playerOnDarkSideOfLocation, self),1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy character as a react", null, Filters.or(Filters.Poe, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.smuggler)), self));
        return modifiers;
    }
}