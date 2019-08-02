package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


public class Card211_041 extends AbstractSite {
    public Card211_041() {
        super(Side.LIGHT, "Clone Training Center", "Kamino");
        setLocationDarkSideGameText("Fetts deploy -1 here.");
        setLocationLightSideGameText("If you control, your non-unique clone troopers are power and forfeit + 1.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.EPISODE_I, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Fett, -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition youControl = new ControlsCondition(playerOnLightSideOfLocation, self);
        Filter nonUniqueCloneTroopers = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.non_unique, Filters.clone, Filters.trooper);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, nonUniqueCloneTroopers, youControl, 1));
        modifiers.add(new ForfeitModifier(self, nonUniqueCloneTroopers, youControl, 1));

        return modifiers;
    }

}
