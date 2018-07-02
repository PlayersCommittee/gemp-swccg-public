package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Alien
 * Title: Prince Xizor
 */
public class Card10_045 extends AbstractAlien {
    public Card10_045() {
        super(Side.DARK, 1, 4, 5, 3, 6, Title.Xizor, Uniqueness.UNIQUE);
        setLore("Falleen gangster and leader. Black Sun agent. Dark Prince of the Black Sun crime syndicate. Fortifies his personal defenses with information gathered by his agents.");
        setGameText("Adds 2 to power of anything he pilots. When in battle at a site, draws one battle destiny if not able to otherwise and total ability of 6 or more required for opponent to draw battle destiny here. Immune to attrition < 5 when Vader not here.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GANGSTER, Keyword.LEADER, Keyword.BLACK_SUN_AGENT);
        setSpecies(Species.FALLEEN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition inBattleAtSite = new InBattleAtCondition(self, Filters.site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, inBattleAtSite, 1));
        modifiers.add(new AbilityRequiredForBattleDestinyModifier(self, Filters.here(self), inBattleAtSite, 6, game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new NotCondition(new HereCondition(self, Filters.Vader)), 5));
        return modifiers;
    }
}
