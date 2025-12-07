package com.gempukku.swccgo.cards.set226.dark;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedWhenMovingFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedWhenMovingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

/**
 * Set: Set 26
 * Type: Location
 * Subtype: System
 * Title: Coruscant (V)
 */
public class Card226_002 extends AbstractSystem {
    public Card226_002() {
        super(Side.DARK, Title.Coruscant, 0, ExpansionSet.SET_26, Rarity.V);
        setLocationDarkSideGameText("Invisible Hand is hyperspeed +3 when moving to or from here. Your [Episode I] pilots deploy -1 here.");
        setLocationLightSideGameText("If Invisible Hand controls this system, Menace Fades is suspended.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EPISODE_I, Icon.VIRTUAL_SET_26);
        setVirtualSuffix(true);
    }
    
    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourEp1Pilots = Filters.and(Filters.your(playerOnDarkSideOfLocation), Icon.EPISODE_I, Filters.pilot);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new HyperspeedWhenMovingToLocationModifier(self, Filters.Invisible_Hand, 3, self));
        modifiers.add(new HyperspeedWhenMovingFromLocationModifier(self, Filters.Invisible_Hand, 3, self));
        modifiers.add(new DeployCostToLocationModifier(self, yourEp1Pilots, -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, Filters.title(Title.Menace_Fades), new ControlsWithCondition(game.getOpponent(playerOnLightSideOfLocation), self, Filters.Invisible_Hand)));

        return modifiers;
    }
}
