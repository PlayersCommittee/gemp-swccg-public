package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Ponda Baba
 */
public class Card1_190 extends AbstractAlien {
    public Card1_190() {
        super(Side.DARK, 3, 2, 2, 1, 3, Title.Ponda_Baba, Uniqueness.UNIQUE);
        setLore("A male Quara (or fingered Aqualish). Thug, smuggler and partner of Dr. Evazan. Has a poor quality cybernetic arm replacement.");
        setGameText("Power +3 when battling at same site as Dr. Evazan, unless opponent has a lightsaber present. Adds 2 to power of anything he pilots.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.AQUALISH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AndCondition(new InBattleCondition(self), new AtSameSiteAsCondition(self, Filters.Dr_Evazan),
                new UnlessCondition(new PresentCondition(self, Filters.and(Filters.opponents(self), Filters.lightsaber)))), 3));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }
}
