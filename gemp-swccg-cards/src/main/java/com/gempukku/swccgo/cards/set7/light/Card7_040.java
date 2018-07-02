package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Rebel
 * Title: Ryle Torsyn
 */
public class Card7_040 extends AbstractRebel {
    public Card7_040() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Ryle Torsyn", Uniqueness.UNIQUE);
        setLore("Piloted Gold 3 at the Battle of Yavin. Recruited by Garven Dreis. Found hidden Imperial tracking device that forced the evacuation of Rebel base on Dantooine.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Gold 3 during battle, adds one destiny to total power only. When at Dantooine system, adds 1 to each of your Force drains there.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.GOLD_SQUADRON);
        setMatchingStarshipFilter(Filters.Gold_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsDestinyToPowerModifier(self, new PilotingCondition(self, Filters.Gold_3), 1));
        modifiers.add(new ForceDrainModifier(self, Filters.Dantooine_system, new AtCondition(self, Filters.Dantooine_system), 1, self.getOwner()));
        return modifiers;
    }
}
