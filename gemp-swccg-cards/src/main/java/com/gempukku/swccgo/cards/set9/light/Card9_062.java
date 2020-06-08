package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: A-wing
 */
public class Card9_062 extends AbstractStarfighter {
    public Card9_062() {
        super(Side.LIGHT, 3, 2, 3, null, 5, 4, 3, "A-wing");
        setLore("Created by General Dodonna and Rebel engineer Walex Blissex. Designed to combat the speed and maneuverability of the TIE interceptor. Jamming suite disrupts enemy communications.");
        setGameText("Permanent pilot provides ability of 1. Opponent may not 'react' to or from here. Power -1 when opponent has a starfighter present with higher maneuver.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.A_WING);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter here = Filters.here(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotReactToLocationModifier(self, here, opponent));
        modifiers.add(new MayNotReactFromLocationModifier(self, here, opponent));
        modifiers.add(new PowerModifier(self, new PresentCondition(self, Filters.and(Filters.opponents(self),
                Filters.starfighter, Filters.maneuverHigherThanManeuverOf(self))), -1));
        return modifiers;
    }
}
