package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.evaluators.PresentWithEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.InitiateForceDrainCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Ickabel G'ont
 */
public class Card2_011 extends AbstractAlien {
    public Card2_011() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Ickabel G'ont", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.U2);
        setLore("Male Bith musician. His favorite cantina song is 'Tears of Aquanna' (mainly because it features him on Fanfar).");
        setGameText("Opponent must use X Force when Force draining at any adjacent site, where X = the number of other musicians present with Ickabel.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.MUSICIAN);
        setSpecies(Species.BITH);
        addPersona(Persona.ICKABEL_GONT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new InitiateForceDrainCostModifier(self, Filters.adjacentSite(self), new PresentWithEvaluator(self, Filters.musician),
                game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
