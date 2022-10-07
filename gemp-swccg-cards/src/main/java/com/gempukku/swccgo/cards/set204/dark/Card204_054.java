package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Starship
 * Subtype: Capital
 * Title: Finalizer
 */
public class Card204_054 extends AbstractCapitalStarship {
    public Card204_054() {
        super(Side.DARK, 1, 13, 10, 8, null, 3, 13, Title.Finalizer, Uniqueness.UNIQUE);
        setAsHorizontal(true);
        setGameText("May add 6 pilots, 8 passengers, and 4 [First Order] starfighters. Permanent pilot provides ability of 2. Deploys -4 to [Episode VII] systems and adds 1 to your Force drains there. Immune to attrition < 5.");
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.NAV_COMPUTER, Icon.FIRST_ORDER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_4);
        addModelType(ModelType.RESURGENT_CLASS_STAR_DESTROYER);
        setPilotCapacity(6);
        setPassengerCapacity(8);
        setStarfighterCapacity(4, Icon.FIRST_ORDER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -4, Filters.and(Icon.EPISODE_VII, Filters.system)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.and(Icon.EPISODE_VII, Filters.sameSystem(self)), 1, playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
