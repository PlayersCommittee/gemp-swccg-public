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
 * Title: Bren Quersey
 */
public class Card7_007 extends AbstractRebel {
    public Card7_007() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Bren Quersey", Uniqueness.UNIQUE);
        setLore("Piloted Red 8 at the Battle of Yavin. Dreamed of attending the prestigious Raithal Academy. Trained on X-wings by Wedge Antilles.");
        setGameText("Adds 2 to anything he pilots. When piloting Red 8 during battle, adds one destiny to total power only. When at Raithal system, adds 1 to each of your Force drains there.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.RED_SQUADRON);
        setMatchingStarshipFilter(Filters.Red_8);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsDestinyToPowerModifier(self, new PilotingCondition(self, Filters.Red_8), 1));
        modifiers.add(new ForceDrainModifier(self, Filters.Raithal_system, new AtCondition(self, Filters.Raithal_system), 1, self.getOwner()));
        return modifiers;
    }
}
