package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Device
 * Title: Mandalorian Armor
 */
public class Card5_109 extends AbstractCharacterDevice {
    public Card5_109() {
        super(Side.DARK, 3, Title.Mandalorian_Armor, Uniqueness.UNIQUE);
        setLore("Contains wrist lasers, rocket dart launchers, a flame projector, a projected grappling hook, a concussion grenade launcher, four kinds of sensors and a broad band antenna.");
        setGameText("Use 3 Force to deploy on any Imperial or alien (except Vader or Boba Fett). Character is power +2, has armor = 5 and is immune to attrition < 3.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.and(Filters.or(Filters.Imperial, Filters.alien), Filters.except(Filters.or(Filters.Vader, Filters.Boba_Fett))));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.or(Filters.Imperial, Filters.alien), Filters.except(Filters.or(Filters.Vader, Filters.Boba_Fett)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, hasAttached, 2));
        modifiers.add(new ResetArmorModifier(self, hasAttached, 5));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, hasAttached, 3));
        return modifiers;
    }
}