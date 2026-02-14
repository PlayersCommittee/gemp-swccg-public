package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Comm Chief
 */
public class Card4_094 extends AbstractImperial {
    public Card4_094() {
        super(Side.DARK, 2, 3, 1, 2, 3, Title.Comm_Chief, Uniqueness.RESTRICTED_3, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("Chief Hudiss is one of the Imperial fleet's many communication specialists. Coordinates Star Destroyer fleet movement during challenging tactical situations.");
        setGameText("Adds 2 to power of anything he pilots, and that starship or vehicle moves for free.");
        addIcons(Icon.DAGOBAH, Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MovesForFreeModifier(self, Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.hasPiloting(self))));
        return modifiers;
    }
}
