package com.gempukku.swccgo.cards.set224.light;

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
import com.gempukku.swccgo.logic.modifiers.AbilityRequiredForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 24
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Dune Sea (V)
 */

public class Card224_022 extends AbstractSite {
    public Card224_022() {
        super(Side.LIGHT, Title.Dune_Sea, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLocationLightSideGameText("While Stolen Data Tapes here, Obi-Wan deploys -3 here.");
        setLocationDarkSideGameText("Total ability of 6 required for you to draw battle destiny here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EXTERIOR_SITE, Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_24);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.ObiWan, new HereCondition(self, Filters.Stolen_Data_Tapes), -3, Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AbilityRequiredForBattleDestinyModifier(self, self, 6, playerOnDarkSideOfLocation));
        return modifiers;
    }

}
