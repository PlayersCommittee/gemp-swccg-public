package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.LeaderModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Ortugg
 */
public class Card6_118 extends AbstractAlien {
    public Card6_118() {
        super(Side.DARK, 3, 4, 4, 2, 3, "Ortugg", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Gamorrean in charge of the Gamorreans at Jabba's palace. Posted to stand guard at the entrance cavern. Assigned by Jabba to keep an eye on Tessek.");
        setGameText("Deploys only on Tatooine. Functions as a leader if present with another Gamorrean. While at Audience Chamber, all your other Gamorreans are forfeit +2.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.GAMORREAN);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LeaderModifier(self, new PresentWithCondition(self, Filters.and(Filters.other(self), Filters.Gamorrean))));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Gamorrean),
                new AtCondition(self, Filters.Audience_Chamber), 2));
        return modifiers;
    }
}
