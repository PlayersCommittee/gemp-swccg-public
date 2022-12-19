package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotExistAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Location
 * Subtype: Site
 * Title: Endor: Ewok Village (V)
 */
public class Card208_023 extends AbstractSite {
    public Card208_023() {
        super(Side.LIGHT, Title.Ewok_Village, Title.Endor, Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Force drain -1 here. While opponent's [Set 8] Effect with Luke here, your Force generation is -1 here.");
        setLocationLightSideGameText("No starships or vehicles here. While Prophecy Of The Force with Luke here, it may not relocate.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, -1, playerOnDarkSideOfLocation));
        modifiers.add(new ForceGenerationModifier(self, new AndCondition(new HereCondition(self, Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Icon.VIRTUAL_SET_8, Filters.Effect)), new HereCondition(self, Filters.Luke)), -1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotExistAtLocationModifier(self, Filters.or(Filters.starship, Filters.vehicle), self));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Prophecy_Of_The_Force, new AndCondition(new HereCondition(self, Filters.Prophecy_Of_The_Force), new HereCondition(self, Filters.Luke)), ModifyGameTextType.PROPHECY_OF_THE_FORCE__MAY_NOT_BE_RELOCATED));
        return modifiers;
    }
}