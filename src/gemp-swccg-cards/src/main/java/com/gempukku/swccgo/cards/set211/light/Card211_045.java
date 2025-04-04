package com.gempukku.swccgo.cards.set211.light;

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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DestinyDrawForActionSourceModifier;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToFireWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Location
 * Subtype: Site
 * Title: Ahch-To: Jedi Village
 */

public class Card211_045 extends AbstractSite {
    public Card211_045() {
        super(Side.LIGHT, Title.AhchTo_Jedi_Village, Title.Ahch_To, Uniqueness.UNIQUE, ExpansionSet.SET_11, Rarity.V);
        setLocationDarkSideGameText("While Luke here, destiny draws for Alter and Sense are +2.");
        setLocationLightSideGameText("While Luke here, opponent must first use 1 Force to fire a weapon anywhere.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EXTERIOR_SITE, Icon.EPISODE_VII, Icon.VIRTUAL_SET_11);
    }


    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Condition lukeHere = new HereCondition(self, Filters.Luke);
        modifiers.add(new ExtraForceCostToFireWeaponModifier(self, Filters.opponents(playerOnLightSideOfLocation), lukeHere, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        String playerId = null; // Affects both players
        Condition lukeHere = new HereCondition(self, Filters.Luke);
        modifiers.add(new DestinyDrawForActionSourceModifier(self, Filters.and(Filters.or(Filters.Sense, Filters.Alter), Filters.canBeTargetedBy(self)), lukeHere, 2, playerId));
        return modifiers;
    }

}