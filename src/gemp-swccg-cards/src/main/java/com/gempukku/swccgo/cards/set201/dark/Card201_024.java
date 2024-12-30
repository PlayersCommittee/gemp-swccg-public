package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.evaluators.ForceIconsAtLocationEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledByModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveTotalAbilityReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

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
        super(Side.DARK, 1, 4, 2, 4, 4, "Djas Puhr", Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Male Sakiyan, a race often employed as assassins. Bounty hunter. Incredible infrared peripheral vision. Has excellent aural and olfactory senses. Often tracks by scent.");
        setGameText("Assassin. Power, defense value, and forfeit +1 for each [Dark Force] icon here. Your total ability may not be reduced at same site. Your Force drains here may not be canceled by [Reflections III] Leia.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.ASSASSIN);
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
