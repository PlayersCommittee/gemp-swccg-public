package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Jeroen Webb
 */
public class Card3_011 extends AbstractRebel {
    public Card3_011() {
        super(Side.LIGHT, 2, 2, 1, 2, 4, "Jeroen Webb", Uniqueness.UNIQUE);
        setLore("Native of Ralltiir. Spy for Ralltiir's underground network after his homeworld was subjugated.");
        setGameText("Adds 2 to power of anything he pilots. When in battle with a Rebel leader, subtracts 1 from opponent's total battle destiny.");
        addIcons(Icon.HOTH, Icon.PILOT);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new TotalBattleDestinyModifier(self, new InBattleWithCondition(self, Filters.Rebel_leader), -1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
