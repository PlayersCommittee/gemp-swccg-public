package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ArmorModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedWhenMovingFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ShuttlesFreeFromLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Location
 * Subtype: System
 * Title: Crait
 */
public class Card225_015 extends AbstractSystem {
    public Card225_015() {
        super(Side.DARK, Title.Crait, 8, ExpansionSet.SET_25, Rarity.V);
        setLocationDarkSideGameText("You shuttle from here for free. While you occupy, gains one [Dark Side] icon.");
        setLocationLightSideGameText("Your capital starships here are armor +1 and are hyperspeed +1 when moving from here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EPISODE_VII, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ShuttlesFreeFromLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.or(Filters.character, Filters.vehicle)), self));
        modifiers.add(new IconModifier(self, new OccupiesCondition(playerOnDarkSideOfLocation, self), Icon.DARK_FORCE, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter yourCapitalStarshipsHere = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.capital_starship, Filters.here(self));
        modifiers.add(new ArmorModifier(self, yourCapitalStarshipsHere, 1));
        modifiers.add(new HyperspeedWhenMovingFromLocationModifier(self, yourCapitalStarshipsHere, 1, self));
        return modifiers;
    }
}