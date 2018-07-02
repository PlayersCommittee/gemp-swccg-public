package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: System
 * Title: Fondor
 */
public class Card7_283 extends AbstractSystem {
    public Card7_283() {
        super(Side.DARK, Title.Fondor, 6);
        setLocationDarkSideGameText("Executor deploys -5 here. If you occupy, all opponent's Corellian corvettes are forfeit -4 and deploy +1.");
        setLocationLightSideGameText("Force drain -1 here. If you control, opponent may not deploy Executor.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition youOccupy = new OccupiesCondition(playerOnDarkSideOfLocation, self);
        Filter opponentsCorellianCorvettes = Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.Corellian_corvette);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Executor, -5, self));
        modifiers.add(new ForfeitModifier(self, opponentsCorellianCorvettes, youOccupy, -4));
        modifiers.add(new DeployCostModifier(self, opponentsCorellianCorvettes, youOccupy, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, -1, playerOnLightSideOfLocation));
        modifiers.add(new MayNotDeployModifier(self, Filters.Executor, new ControlsCondition(playerOnLightSideOfLocation, self),
                game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}