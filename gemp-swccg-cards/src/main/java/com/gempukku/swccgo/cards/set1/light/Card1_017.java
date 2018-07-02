package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Rebel
 * Title: Leia Organa
 */
public class Card1_017 extends AbstractRebel {
    public Card1_017() {
        super(Side.LIGHT, 1, 3, 3, 3, 6, "Leia Organa", Uniqueness.UNIQUE);
        setLore("Strong-willed princess from Alderaan. Youngest Imperial Senator ever. Used diplomatic immunity to spy for Rebels. Led relief effort on Ralltir. Natural leader.");
        setGameText("Adds 1 to power of each Rebel present with her at a Death Star site. 'Diplomatic' immunity to attrition < 2.");
        addPersona(Persona.LEIA);
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.SENATOR, Keyword.SPY, Keyword.LEADER, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Rebel, Filters.presentWith(self)), new AtCondition(self, Filters.Death_Star_site), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 2));
        return modifiers;
    }
}
