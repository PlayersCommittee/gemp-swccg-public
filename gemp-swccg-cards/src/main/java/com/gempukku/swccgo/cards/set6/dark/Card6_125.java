package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeAboardModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Thul Fain
 */
public class Card6_125 extends AbstractAlien {
    public Card6_125() {
        super(Side.DARK, 2, 2, 2, 1, 3, "Thul Fain", Uniqueness.UNIQUE);
        setLore("Gambler who bets on how long the Rancor's victims will last. Formerly an Imperial pilot who worked with Lieutenant Tanbris. Now a smuggler for Jabba.");
        setGameText("Deploys free to your [Independent Starship] starship. Adds 2 to power of anything he pilots. When with Lieutenant Tanbris in a battle at a system, adds 2 to each of your battle destiny draws.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT);
        addKeywords(Keyword.GAMBLER, Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeAboardModifier(self, Filters.and(Filters.your(self), Icon.INDEPENDENT, Filters.starship)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new EachBattleDestinyModifier(self, new AndCondition(new InBattleWithCondition(self, Filters.Tanbris),
                new InBattleAtCondition(self, Filters.system)), 2, self.getOwner()));
        return modifiers;
    }
}
