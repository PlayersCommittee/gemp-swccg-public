package com.gempukku.swccgo.cards.set303.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetArmorModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Shadow Academy
 * Type: Device
 * Title: Crystal Armor
 */
public class Card303_030 extends AbstractCharacterDevice {
    public Card303_030() {
        super(Side.LIGHT, 3, "Crystal Armor", Uniqueness.UNIQUE, ExpansionSet.SA, Rarity.V);
        setLore("Created from the remains of the Crystaline Creatures this armor provide robust protection to the wearer.");
        setGameText("Use 3 Force to deploy on any alien or Rebel. Character is power +2, has armor = 5 and is immune to attrition < 3.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.and(Filters.or(Filters.Rebel, Filters.alien)));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.or(Filters.Rebel, Filters.alien));
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