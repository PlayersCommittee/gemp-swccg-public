package com.gempukku.swccgo.cards.set101.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AbilityRequiredToControlLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Premiere Introductory Two Player Game)
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Level 6 Core Shaft Corridor
 */
public class Card101_001 extends AbstractSite {
    public Card101_001() {
        super(Side.LIGHT, "Death Star: Level 6 Core Shaft Corridor", Title.Death_Star);
        setLocationDarkSideGameText("If you control, opponent needs 2 ability to control each Death Star site.");
        setLocationLightSideGameText("If you control, Luke and Obi-Wan are power +2 on Death Star.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.MOBILE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AbilityRequiredToControlLocationModifier(self, Filters.Death_Star_site,
                new ControlsCondition(playerOnDarkSideOfLocation, self), 2, game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.or(Filters.Luke, Filters.ObiWan), Filters.on(Title.Death_Star)),
                new ControlsCondition(playerOnLightSideOfLocation, self), 2));
        return modifiers;
    }
}