package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Card211_021 extends AbstractUniqueStarshipSite {
    public Card211_021() {
        super(Side.DARK, Title.Invisible_Hand_Docking_Bay, Persona.INVISIBLE_HAND);
        setLocationDarkSideGameText("Your docking bay transit from here requires 1 Force (free if your [Presence] droid present).");
        setLocationLightSideGameText("Your docking bay transit from here requires 3 force (free if Palpatine present).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SCOMP_LINK, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.VIRTUAL_SET_11, Icon.SCOMP_LINK);
        addKeywords(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition yourPresenceDroidPresent = new PresentCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.and(Icon.PRESENCE, Filters.droid)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, new NotCondition(yourPresenceDroidPresent), 1, playerOnDarkSideOfLocation));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, yourPresenceDroidPresent, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition yourPalpatinePresent = new PresentCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Palpatine));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, new NotCondition(yourPalpatinePresent), 3, playerOnLightSideOfLocation));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, yourPalpatinePresent, playerOnLightSideOfLocation));
        return modifiers;
    }
}
