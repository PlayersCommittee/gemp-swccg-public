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
 * Title: Hol Okand
 */
public class Card7_021 extends AbstractRebel {
    public Card7_021() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Hol Okand", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("Piloted Gold 6 at the Battle of Yavin. Flew as Dutch's wingman during an attack on an Imperial supply outpost at Kashyyyk. Befriended by Chewbacca before leaving Yavin 4.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Gold 6 during battle, adds one destiny to total power only. When at Kashyyyk system, adds 1 to each of your Force drains there.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.GOLD_SQUADRON);
        setMatchingStarshipFilter(Filters.Gold_6);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsDestinyToPowerModifier(self, new PilotingCondition(self, Filters.Gold_6), 1));
        modifiers.add(new ForceDrainModifier(self, Filters.Kashyyyk_system, new AtCondition(self, Filters.Kashyyyk_system), 1, self.getOwner()));
        return modifiers;
    }
}
