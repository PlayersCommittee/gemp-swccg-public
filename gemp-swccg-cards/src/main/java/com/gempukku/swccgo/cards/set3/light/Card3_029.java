package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseAnyNumberOfDevicesModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseAnyNumberOfWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Device
 * Title: Echo Trooper Backpack
 */
public class Card3_029 extends AbstractCharacterDevice {
    public Card3_029() {
        super(Side.LIGHT, 5, "Echo Trooper Backpack");
        setLore("Rebels carry a variety of equipment to be fully prepared for enemies and the elements. Weather-resistant backpacks keep gear protected and operational.");
        setGameText("Deploy on your trooper. May use any number of weapons and devices. Trooper is immune to attrition < 3 when at a planet site.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.trooper);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.trooper;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayUseAnyNumberOfWeaponsModifier(self, hasAttached));
        modifiers.add(new MayUseAnyNumberOfDevicesModifier(self, hasAttached));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(hasAttached, Filters.at(Filters.planet_site)), 3));
        return modifiers;
    }
}