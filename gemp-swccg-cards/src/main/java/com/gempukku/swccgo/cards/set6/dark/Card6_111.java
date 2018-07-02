package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.evaluators.AbilityOfHighestAbilityCharacterPresentEvaluator;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextPowerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Kithaba
 */
public class Card6_111 extends AbstractAlien {
    public Card6_111() {
        super(Side.DARK, 2, 4, null, 2, 2, "Kithaba", Uniqueness.UNIQUE);
        setLore("Famous Klatooinian assassin. After disposing of a few more prisoners, plans to quit the killing business and become a full-time musician. Rises to the challenges he faces.");
        setGameText("Deploys only on Tatooine. Adds 1 to power of anything he pilots. Power = 1 + ability of opponent's highest-ability character present.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT, Icon.WARRIOR);
        setSpecies(Species.KLATOOINIAN);
        addKeywords(Keyword.MUSICIAN);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new DefinedByGameTextPowerModifier(self, new AddEvaluator(new AbilityOfHighestAbilityCharacterPresentEvaluator(self, Filters.opponents(self)), 1)));
        return modifiers;
    }
}
