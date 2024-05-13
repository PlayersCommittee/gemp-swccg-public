package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Location
 * Subtype: System
 * Title: Endor
 */
public class Card8_068 extends AbstractSystem {
    public Card8_068() {
        super(Side.LIGHT, Title.Endor, 8, ExpansionSet.ENDOR, Rarity.U);
        setLocationDarkSideGameText("If you have no Imperials on Endor, Force drain -1 Here.");
        setLocationLightSideGameText("Force drain -1 here. To move your starship here from Sullust, Rendezvous Point or Haven requires -1 Force.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new CantSpotCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.Imperial, Filters.on(Title.Endor))), -1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, -1, playerOnLightSideOfLocation));
        modifiers.add(new MoveCostFromLocationToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship),
                        -1, Filters.or(Filters.Sullust_system, Filters.Rendezvous_Point, Filters.hasAttached(Filters.Haven)), self));
        return modifiers;
    }
}