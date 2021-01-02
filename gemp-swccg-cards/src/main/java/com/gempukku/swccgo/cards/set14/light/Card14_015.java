package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.PilotingAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Lieutenant Arven Wendik
 */
public class Card14_015 extends AbstractRepublic {
    public Card14_015() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, Title.Arven, Uniqueness.UNIQUE);
        setLore("Bravo Squadron pilot who assisted in the attack at the battle of Naboo. Tactical expert of capital starship shield capabilities.");
        setGameText("Adds 2 to power of anything he pilots. While piloting Bravo 3 at same system as a battleship, adds one destiny to attrition only.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT);
        addKeywords(Keyword.BRAVO_SQUADRON);
        setMatchingStarshipFilter(Filters.Bravo_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsDestinyToAttritionModifier(self, new PilotingAtCondition(self, Filters.Bravo_3,
                Filters.sameSystemAs(self, Filters.battleship)), 1));
        return modifiers;
    }
}
