package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Hutt Canyon
 */
public class Card6_086 extends AbstractSite {
    public Card6_086() {
        super(Side.LIGHT, "Tatooine: Hutt Canyon", Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLocationDarkSideGameText("If you occupy with a Tusken Raider, opponent may not draw battle destiny here.");
        setLocationLightSideGameText("If you occupy with a Jawa, opponent may not draw battle destiny here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.JABBAS_PALACE, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeyword(Keyword.CANYON);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawBattleDestinyModifier(self, self, new OccupiesWithCondition(playerOnDarkSideOfLocation, self, Filters.Tusken_Raider),
                game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawBattleDestinyModifier(self, self, new OccupiesWithCondition(playerOnLightSideOfLocation, self, Filters.Jawa),
                game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}