package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.evaluators.ForceIconsAtLocationEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Character
 * Subtype: Alien
 * Title: Djas Puhr (V)
 */
public class Card201_024 extends AbstractAlien {
    public Card201_024() {
        super(Side.DARK, 1, 4, 2, 4, 4, "Djas Puhr", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Male Sakiyan, a race often employed as assassins. Bounty hunter. Incredible infrared peripheral vision. Has excellent aural and olfactory senses. Often tracks by scent.");
        setGameText("Power, defense value, and forfeit +1 for each [Dark Force] icon here. Your total ability may not be reduced at same site. Your Force drains here may not be canceled by [Reflections III] Leia.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setSpecies(Species.SAKIYAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Evaluator darkSideForceIconsHere = new ForceIconsAtLocationEvaluator(self, true, false);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, darkSideForceIconsHere));
        modifiers.add(new DefenseValueModifier(self, darkSideForceIconsHere));
        modifiers.add(new ForfeitModifier(self, darkSideForceIconsHere));
        modifiers.add(new MayNotHaveTotalAbilityReducedModifier(self, Filters.sameSite(self), playerId));
        modifiers.add(new ForceDrainsMayNotBeCanceledByModifier(self, Filters.and(Icon.REFLECTIONS_III, Filters.Leia), playerId, Filters.here(self)));
        return modifiers;
    }
}
