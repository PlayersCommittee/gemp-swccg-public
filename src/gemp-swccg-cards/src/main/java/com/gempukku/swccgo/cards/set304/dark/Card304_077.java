package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractMobileSystem;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;


import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Location
 * Subtype: System
 * Title: SARLaC
 */
public class Card304_077 extends AbstractMobileSystem {
    public Card304_077() {
        super(Side.DARK, Title.SARLAC, 6, Title.Seraph, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLocationDarkSideGameText("X = parsec of current position. Must deploy orbiting Seraph. S.A.R.L.A.c locations are immune to Revolution. Opponent's Force drains +3 here. If you occupy, opponent's ships may not leave.");
        addIcon(Icon.DARK_FORCE, 3);
        addPersona(Persona.SARLAC);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition youOccupy = new OccupiesCondition(playerOnDarkSideOfLocation, self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.SARLAC_location, Title.Revolution));
        modifiers.add(new ForceDrainModifier(self, 3, game.getOpponent(playerOnDarkSideOfLocation)));
        modifiers.add(new MayNotMoveFromLocationModifier(self, Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.starship),
                youOccupy, self));
        return modifiers;
    }
}