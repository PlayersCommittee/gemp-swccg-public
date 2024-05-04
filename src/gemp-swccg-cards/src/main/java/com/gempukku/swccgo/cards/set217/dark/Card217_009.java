package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Location
 * Subtype: System
 * Title: Eriadu (V)
 */
public class Card217_009 extends AbstractSystem {
    public Card217_009() {
        super(Side.DARK, Title.Eriadu, 1, ExpansionSet.SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("If Imperials control three battlegrounds, Force drain +1 here.");
        setLocationLightSideGameText("If Tarkin here, your starships deploy +1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PREMIUM, Icon.PLANET, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, new OrCondition(new ControlsWithCondition(self, playerOnDarkSideOfLocation, 3, Filters.battleground, Filters.Imperial), new ControlsWithCondition(self, game.getOpponent(playerOnDarkSideOfLocation), 3, Filters.battleground, Filters.Imperial)), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship), new HereCondition(self, Filters.Tarkin), 1, Filters.here(self)));
        return modifiers;
    }
}