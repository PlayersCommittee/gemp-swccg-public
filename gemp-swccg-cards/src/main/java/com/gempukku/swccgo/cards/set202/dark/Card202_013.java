package com.gempukku.swccgo.cards.set202.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 2
 * Type: Starship
 * Subtype: Capital
 * Title: Binder
 */
public class Card202_013 extends AbstractCapitalStarship {
    public Card202_013() {
        super(Side.DARK, 1, 6, 5, 5, null, 3, 6, "Binder", Uniqueness.UNIQUE);
        setGameText("May add 6 pilots or passengers. Permanent pilot provides ability of 2. Your starships may move to here as a 'react'. For opponent to move a starship from here requires +1 Force. Immune to attrition < 4.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_2);
        addModelType(ModelType.INTERDICTOR_CLASS_STAR_DESTROYER);
        setPilotOrPassengerCapacity(6);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move a starship as a 'react'", self.getOwner(), Filters.starship, Filters.here(self)));
        modifiers.add(new MoveCostFromLocationModifier(self, Filters.and(Filters.opponents(self), Filters.starship), 1, Filters.here(self)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
