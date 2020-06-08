package com.gempukku.swccgo.cards.set207.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DidNotDeployObjectiveCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.conditions.RepCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 7
 * Type: Character
 * Subtype: Alien
 * Title: Quiggold
 */
public class Card207_022 extends AbstractAlien {
    public Card207_022() {
        super(Side.DARK, 3, 3, 3, 2, 3, Title.Quiggold, Uniqueness.UNIQUE);
        setLore("Gabdorin pirate.");
        setGameText("[Pilot] 2. While piloting Meson Martinet, it is defense value +2. Forfeit +2 while present with Sidon. If your Rep is a pirate (or you did not deploy an Objective), Force drain +1 here.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_VII, Icon.VIRTUAL_SET_7);
        addKeywords(Keyword.PIRATE);
        setSpecies(Species.GABDORIN);
        setMatchingStarshipFilter(Filters.Meson_Martinet);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DefenseValueModifier(self, Filters.Meson_Martinet, new PilotingCondition(self, Filters.Meson_Martinet), 2));
        modifiers.add(new ForfeitModifier(self, new PresentWithCondition(self, Filters.Sidon), 2));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new OrCondition(new RepCondition(playerId, Filters.pirate),
                new DidNotDeployObjectiveCondition(playerId)), 1, playerId));
        return modifiers;
    }
}
