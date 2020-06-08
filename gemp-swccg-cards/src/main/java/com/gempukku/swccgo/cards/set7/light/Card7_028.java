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
 * Title: Lieutenant Lepira
 */
public class Card7_028 extends AbstractRebel {
    public Card7_028() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Lieutenant Lepira", Uniqueness.UNIQUE);
        setLore("Piloted Gold 4 at the Battle of Yavin. Learned to fly a starfighter by racing through the Anoat system.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Gold 4 during battle, adds one destiny to total power only. When at Anoat system, adds 1 to each of your Force drains there.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.GOLD_SQUADRON);
        setMatchingStarshipFilter(Filters.Gold_4);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsDestinyToPowerModifier(self, new PilotingCondition(self, Filters.Gold_4), 1));
        modifiers.add(new ForceDrainModifier(self, Filters.Anoat_system, new AtCondition(self, Filters.Anoat_system), 1, self.getOwner()));
        return modifiers;
    }
}
