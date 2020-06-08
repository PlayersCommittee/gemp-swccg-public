package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Shistavanen Wolfman
 */
public class Card1_030 extends AbstractAlien {
    public Card1_030() {
        super(Side.LIGHT, 2, 3, 2, 1, 2, "Shistavanen Wolfman", Uniqueness.RESTRICTED_3);
        setLore("Lak Sivrak is a typical Shistavanen male. Ferocious, but not aggressive. Often trained as scouts at Imperial academies, but they despise the ambitions of the New Order.");
        setGameText("May move to an adjacent site as a 'react'.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.SHISTAVANEN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationModifier(self, Filters.adjacentSite(self)));
        return modifiers;
    }
}
