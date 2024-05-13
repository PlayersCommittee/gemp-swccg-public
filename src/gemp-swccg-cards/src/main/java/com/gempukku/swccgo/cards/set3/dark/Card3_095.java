package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.conditions.TargetingTheMainGeneratorCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayTargetTwoSitesAwayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TargetTheMainGeneratorTotalModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Device
 * Title: Electro-Rangefinder
 */
public class Card3_095 extends AbstractDevice {
    public Card3_095() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "Electro-Rangefinder", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.U1);
        setLore("Long-range stereoscopic sighting device connected to the cannons of an Imperial walker. Calibrated to allow the AT-AT commander to accurately fire at distant targets.");
        setGameText("Use 2 Force to deploy on any AT-AT Cannon. It may fire at targets two sites away. Also, when used to Target The Main Generator, adds 1 to total.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.AT_AT_Cannon);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.AT_AT_Cannon;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayTargetTwoSitesAwayModifier(self, hasAttached));
        modifiers.add(new TargetTheMainGeneratorTotalModifier(self, new TargetingTheMainGeneratorCondition(hasAttached), 1));
        return modifiers;
    }
}