package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotSubstituteBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A Better Tomorrow
 * Type: Character
 * Subtype: Alien
 * Title: Kalen Joss
 */
public class Card305_045 extends AbstractAlien {
    public Card305_045() {
        super(Side.LIGHT, 1, 3, 3, 5, 4, "Kalen Joss", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.U);
        setLore("Lost for over 70 years in a distant galaxy, it is a mystery how Kalen Joss survived. An experienced Jedi pilot who served with the Republic who has joined the Brotherhood.");
        setGameText("[Pilot] 3. Matching pilot for any 'snub' fighter. While piloting a 'snub' fighter, draws one battle destiny if unable to otherwise and opponent may not substitute battle destiny draws here.");
        addIcons(Icon.ABT, Icon.PLAG, Icon.PILOT, Icon.WARRIOR);
        setMatchingStarshipFilter(Filters.snub_fighter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition whilePilotingSnubFighter = new PilotingCondition(self, Filters.snub_fighter);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, whilePilotingSnubFighter, 1));
        modifiers.add(new MayNotSubstituteBattleDestinyModifier(self, Filters.here(self), whilePilotingSnubFighter, opponent));
        return modifiers;
    }
}
