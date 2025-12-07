package com.gempukku.swccgo.cards.set223.dark;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeConvertedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

/**
 * Set: Set 23
 * Type: Location
 * Subtype: System
 * Title: Bespin (V)
 */
public class Card223_008 extends AbstractSystem {
    public Card223_008() {
        super(Side.DARK, Title.Bespin, 6, ExpansionSet.SET_23, Rarity.V);
        setLocationDarkSideGameText("Executor may not move from here unless Vader aboard. If your [Cloud City] objective on table, Executor deploys -8 here.");
        setLocationLightSideGameText("You lose no more than 2 Force to Cloud City Occupation. May not be converted.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.PLANET, Icon.VIRTUAL_SET_23);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition vaderAboardExecutor = new OnTableCondition(self, Filters.and(Filters.Vader, Filters.aboard(Filters.Executor)));
        Condition unlessVaderAboardExecutor = new UnlessCondition(vaderAboardExecutor);
        Condition yourCloudCityObjOnTable = new OnTableCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Icon.CLOUD_CITY, Filters.Objective));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveFromLocationModifier(self, Filters.Executor, unlessVaderAboardExecutor, self));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Executor, yourCloudCityObjOnTable, -8, Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new LimitForceLossFromCardModifier(self, Filters.Cloud_City_Occupation, 2, playerOnLightSideOfLocation));
        modifiers.add(new MayNotBeConvertedModifier(self));
        return modifiers;
    }
}
