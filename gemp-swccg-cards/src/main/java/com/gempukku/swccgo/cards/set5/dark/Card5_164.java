package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Location
 * Subtype: System
 * Title: Bespin
 */
public class Card5_164 extends AbstractSystem {
    public Card5_164() {
        super(Side.DARK, Title.Bespin, 6);
        setLocationDarkSideGameText("If you control, opponent's characters and vehicles deploy +1 to Cloud City locations");
        setLocationLightSideGameText("If you control, your characters and vehicles deploy -1 to Cloud City locations.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.CLOUD_CITY, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.or(Filters.character, Filters.vehicle)),
                new ControlsCondition(playerOnDarkSideOfLocation, self), 1, Filters.Cloud_City_location));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.character, Filters.vehicle)),
                new ControlsCondition(playerOnLightSideOfLocation, self), -1, Filters.Cloud_City_location));
        return modifiers;
    }
}