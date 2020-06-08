package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Corellian Engineering Corporation
 */
public class Card7_056 extends AbstractNormalEffect {
    public Card7_056() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Corellian_Engineering_Corporation, Uniqueness.UNIQUE);
        setLore("Only one of the 'Big Three' starship manufacturers to sell primarily to civilians. CEC employees take great delight in calling their highly modifiable designs 'stock'.");
        setGameText("Deploy on Corellia system. All your freighters are deploy -1 and hyperspeed +1. Also, all your Quad Laser Cannons deploy free and add 2 to each of their weapon destiny draws. Suspended while opponent controls Corellia. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Corellia_system;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourFreighters = Filters.and(Filters.your(self), Filters.freighter);
        Filter yourQuadLaserCannons = Filters.and(Filters.your(self), Filters.Quad_Laser_Cannon);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, yourFreighters, -1));
        modifiers.add(new HyperspeedModifier(self, yourFreighters, 1));
        modifiers.add(new DeploysFreeModifier(self, yourQuadLaserCannons));
        modifiers.add(new EachWeaponDestinyModifier(self, yourQuadLaserCannons, 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, self, new ControlsCondition(opponent, Filters.Corellia_system)));
        return modifiers;
    }
}