package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: System
 * Title: Lothal
 */
public class Card219_010 extends AbstractSystem {
    public Card219_010() {
        super(Side.DARK, Title.Lothal, 6, ExpansionSet.SET_19, Rarity.V);
        setLocationDarkSideGameText("While you occupy, gains one [Dark Side] icon.");
        setLocationLightSideGameText("Rebel starships here are power and forfeit +1 and immune to Gravity Shadow.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new IconModifier(self, new OccupiesCondition(playerOnDarkSideOfLocation, self), Icon.DARK_FORCE, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter pilotedRebelStarshipsHere = Filters.and(Filters.Rebel_starship, Filters.piloted, Filters.here(self));
        modifiers.add(new PowerModifier(self, pilotedRebelStarshipsHere, 1));
        modifiers.add(new ForfeitModifier(self, pilotedRebelStarshipsHere, 1));
        modifiers.add(new ImmuneToTitleModifier(self, pilotedRebelStarshipsHere, Title.Gravity_Shadow));
        return modifiers;
    }
}