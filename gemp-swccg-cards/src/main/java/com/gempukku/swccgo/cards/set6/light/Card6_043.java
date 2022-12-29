package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.ControlsEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Tanus Spijek
 */
public class Card6_043 extends AbstractAlien {
    public Card6_043() {
        super(Side.LIGHT, 2, 3, 2, 1, 3, "Tanus Spijek", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Male Elom. Former spy for the Rebellion. Hired by the Alliance to carry messages between Alderaan and the Rebel base on Yavin 4.");
        setGameText("When at a Yavin 4 site, adds 1 to your Force drains at Alderaan system for each Yavin 4 site you control. While at Audience Chamber, all your other Elom are power and forfeit +1.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.SPY);
        setSpecies(Species.ELOM);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);
        Filter yourOtherElom = Filters.and(Filters.your(self), Filters.other(self), Filters.Elom);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.Alderaan_system, new AtCondition(self, Filters.Yavin_4_site),
                new ControlsEvaluator(playerId, Filters.Yavin_4_site), playerId));
        modifiers.add(new PowerModifier(self, yourOtherElom, atAudienceChamber, 1));
        modifiers.add(new ForfeitModifier(self, yourOtherElom, atAudienceChamber, 1));
        return modifiers;
    }
}
