package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DuringForceDrainAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Mosep
 */
public class Card2_097 extends AbstractAlien {
    public Card2_097() {
        super(Side.DARK, 3, 3, 1, 1, 5, Title.Mosep, Uniqueness.UNIQUE);
        setLore("Jabba's Nimbanel accountant. Inside contacts allow him to disrupt the cash flow of the Hutt's enemies. He knows a mistake could make him the next write-off.");
        setGameText("When opponent is losing Force from Force drains at the same or an adjacent site, lost Force must come from Reserve Deck if possible.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.ACCOUNTANT);
        setSpecies(Species.NIMBANEL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, new DuringForceDrainAtCondition(Filters.sameOrAdjacentSite(self)),
                ModifierFlag.FORCE_DRAIN_LOST_FROM_RESERVE_DECK, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
