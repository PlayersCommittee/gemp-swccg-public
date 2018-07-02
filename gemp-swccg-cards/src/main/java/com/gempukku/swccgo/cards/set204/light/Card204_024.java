package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Location
 * Subtype: System
 * Title: Endor (V)
 */
public class Card204_024 extends AbstractSystem {
    public Card204_024() {
        super(Side.LIGHT, Title.Endor, 8);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("If you have no Imperials on Endor, Force drain -1 here.");
        setLocationLightSideGameText("If you have no Rebels on Endor, Force drain -1 here. If you control, each of opponent's biker scouts is forfeit -1.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.PLANET, Icon.VIRTUAL_SET_4);
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
        modifiers.add(new ForceDrainModifier(self, new CantSpotCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.Rebel, Filters.on(Title.Endor))), -1, playerOnLightSideOfLocation));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.biker_scout),
                new ControlsCondition(playerOnLightSideOfLocation, self), -1));
        return modifiers;
    }
}