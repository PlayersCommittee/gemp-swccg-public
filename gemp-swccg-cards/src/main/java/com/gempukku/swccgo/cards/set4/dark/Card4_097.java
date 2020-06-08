package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Nemet
 */
public class Card4_097 extends AbstractImperial {
    public Card4_097() {
        super(Side.DARK, 2, 2, 1, 2, 3, "Commander Nemet", Uniqueness.UNIQUE);
        setLore("Logistics officer for the Avenger, member of the Line Branch of the Imperial Navy. Relays important scanner information to Captain Needa. Fiercely competitive.");
        setGameText("Adds 1 to power and maneuver of anything he pilots. Opponent may not 'react' to or from same location.");
        addIcons(Icon.DAGOBAH, Icon.PILOT);
        addKeywords(Keyword.COMMANDER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), 1));
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.sameLocation(self), opponent));
        modifiers.add(new MayNotReactFromLocationModifier(self, Filters.sameLocation(self), opponent));
        return modifiers;
    }
}
