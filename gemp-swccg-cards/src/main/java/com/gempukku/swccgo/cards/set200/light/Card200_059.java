package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Starship
 * Subtype: Capital
 * Title: Acclamator-Class Assault Ship
 */
public class Card200_059 extends AbstractCapitalStarship {
    public Card200_059() {
        super(Side.LIGHT, 2, 5, 6, 5, null, 4, 6, "Acclamator-Class Assault Ship");
        setGameText("May add 4 pilots, 4 passengers, and 4 vehicles. Adds 1 to attrition against opponent here for each piloted [Republic] starship here. Permanent pilot provides ability of 2. Concussion Missiles may deploy aboard.");
        addModelType(ModelType.ACCLAMATOR_CLASS_ASSAULT_SHIP);
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER, Icon.CLONE_ARMY, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_0);
        setPilotCapacity(4);
        setPassengerCapacity(4);
        setVehicleCapacity(4);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, Filters.here(self), new HereEvaluator(self, Filters.and(Filters.piloted, Icon.REPUBLIC, Filters.starship)), opponent));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.Concussion_Missiles), self));
        return modifiers;
    }
}
