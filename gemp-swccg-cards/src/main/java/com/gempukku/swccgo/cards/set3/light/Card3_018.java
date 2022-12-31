package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Romas "Lock' Navander
 */
public class Card3_018 extends AbstractRebel {
    public Card3_018() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Romas \"Lock\" Navander", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.U2);
        setLore("Corellian pilot. Defected from the Empire shortly after graduation from the Academy. Tech communications officer at Echo Base. Relays orders to nearby Rebel starships.");
        setGameText("Adds 1 to power and maneuver of anything he pilots. Opponent may not 'react' to or from same location.");
        addIcons(Icon.HOTH, Icon.PILOT);
        setSpecies(Species.CORELLIAN);
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
