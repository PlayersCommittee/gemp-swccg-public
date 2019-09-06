package com.gempukku.swccgo.cards.set301.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Premium Set
 * Type: Character
 * Subtype: Rebel
 * Title: Puck
 */
public class Card301_007 extends AbstractRebel {
    public Card301_007() {
        super(Side.LIGHT, 1, 2, 2, 2, 4, Title.Puck, Uniqueness.UNIQUE);
        setLore("Puck Naeco. Green and Red Squadron.");
        setGameText("[Pilot] 2. When choosing the destiny value of this card, must use 3 Force to choose 7. May deploy as a 'react.' Your Rebel pilots aboard snub fighters here are forfeit +1.");
        setAlternateDestiny(7);
        setAlternateDestinyCost(3);
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_P, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.RED_SQUADRON, Keyword.GREEN_SQUADRON);
        addPersona(Persona.PUCK);
        setMatchingStarshipFilter(Filters.Red_12);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter rebelPilotsAboardSnubFightersHere = Filters.and(Filters.Rebel_pilot, Filters.aboard(Filters.snub_fighter), Filters.here(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForfeitModifier(self, rebelPilotsAboardSnubFightersHere, 1));
        return modifiers;
    }
}
