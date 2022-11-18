package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: System
 * Title: Lothal
 */
public class Card219_038 extends AbstractSystem {
    public Card219_038() {
        super(Side.LIGHT, Title.Lothal, 6, ExpansionSet.SET_19, Rarity.V);
        setLocationDarkSideGameText("Unless Chimaera or Thrawn here, Force drain -1 here");
        setLocationLightSideGameText("Ghost deploys -2 here. While Ghost piloted here, opponent's battle destiny draws are -1 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, new UnlessCondition(new HereCondition(self, Filters.or(Filters.Thrawn, Filters.Chimaera))), -1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Ghost, -2, Filters.here(self)));
        modifiers.add(new EachBattleDestinyModifier(self, self, new HereCondition(self, Filters.and(Filters.Ghost, Filters.piloted)), -1, game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}
