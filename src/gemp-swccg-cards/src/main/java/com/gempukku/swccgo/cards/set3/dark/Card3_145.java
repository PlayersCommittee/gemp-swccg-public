package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.CommencePrimaryIgnitionTargetingCondition;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
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
import com.gempukku.swccgo.logic.modifiers.CommencePrimaryIgnitionTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationToLocationModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Echo Command Center (War Room)
 */
public class Card3_145 extends AbstractSite {
    public Card3_145() {
        super(Side.DARK, Title.Echo_Command_Center, Title.Hoth, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.U2);
        setLocationDarkSideGameText("Add 1 to total of Commence Primary Ignition when targeting the Hoth system.");
        setLocationLightSideGameText("If you control, your starship movement from Hoth sites to the Hoth system is free.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.HOTH, Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeywords(Keyword.WAR_ROOM);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CommencePrimaryIgnitionTotalModifier(self, new CommencePrimaryIgnitionTargetingCondition(Filters.Hoth_system), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeFromLocationToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship),
                new ControlsCondition(playerOnLightSideOfLocation, self), Filters.Hoth_site, Filters.Hoth_system));
        return modifiers;
    }
}