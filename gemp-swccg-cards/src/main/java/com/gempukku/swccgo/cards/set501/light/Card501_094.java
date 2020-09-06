package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: System
 * Title: Kessel (V)
 */
public class Card501_094 extends AbstractSystem {
    public Card501_094() {
        super(Side.LIGHT, Title.Kessel, 8);
        setLocationLightSideGameText("While you occupy with Chewie, Han, Lando, or Qi'ra, Kessel Run may not be canceled.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET);
        setVirtualSuffix(true);
        setTestingText("Kessel (V)");
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeCanceledModifier(self, Filters.Kessel_Run, new OccupiesWithCondition(playerOnLightSideOfLocation, self, Filters.or(Filters.Chewie, Filters.Han, Filters.Lando, Filters.Qira))));
        return modifiers;
    }
}
