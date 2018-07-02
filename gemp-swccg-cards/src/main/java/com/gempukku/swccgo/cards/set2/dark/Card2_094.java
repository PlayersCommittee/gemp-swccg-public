package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Lirin Car'n
 */
public class Card2_094 extends AbstractAlien {
    public Card2_094() {
        super(Side.DARK, 3, 2, 1, 1, 3, Title.Lirin_Carn, Uniqueness.UNIQUE);
        setLore("Bith mercenary. Male back-up to Kloo Horn player in Figrin D'an's band. Only musician, besides Doikk, who has not lost ownership of his instrument to Figrin.");
        setGameText("For each other musician present, adds a 'cover charge' of 1 to the Force required to move or deploy each character to same site.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.MUSICIAN);
        setSpecies(Species.BITH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Evaluator forEachOtherMusicianPresent = new PresentEvaluator(self, Filters.and(Filters.other(self), Filters.musician));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.character, forEachOtherMusicianPresent, Filters.sameSite(self)));
        modifiers.add(new MoveCostToLocationModifier(self, Filters.character, forEachOtherMusicianPresent, Filters.sameSite(self)));
        return modifiers;
    }
}
