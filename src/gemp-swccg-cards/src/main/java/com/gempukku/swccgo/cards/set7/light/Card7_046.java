package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
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
 * Title: Theron Nett
 */
public class Card7_046 extends AbstractRebel {
    public Card7_046() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Theron Nett", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("Experienced smuggler from Ord Mantell. Piloted Red 10 at the Battle of Yavin. Long-time wingman of Garven Dreis.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Red 10 during battle, adds one destiny to total power only. When at Ord Mantell system, adds 1 to each of your Force drains there.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.RED_SQUADRON, Keyword.SMUGGLER);
        setMatchingStarshipFilter(Filters.Red_10);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsDestinyToPowerModifier(self, new PilotingCondition(self, Filters.Red_10), 1));
        modifiers.add(new ForceDrainModifier(self, Filters.Ord_Mantell_system, new AtCondition(self, Filters.Ord_Mantell_system), 1, self.getOwner()));
        return modifiers;
    }
}
