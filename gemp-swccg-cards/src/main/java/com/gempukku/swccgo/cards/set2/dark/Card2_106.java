package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PlayerToSelectCardTargetAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Imperial
 * Title: Trooper Davin Felth
 */
public class Card2_106 extends AbstractImperial {
    public Card2_106() {
        super(Side.DARK, 2, 1, 2, 2, 3, "Trooper Davin Felth", Uniqueness.UNIQUE);
        setLore("Dispatched to Tatooine to apprehend renegade droids fleeing the Tantive IV. Suspected to have misgivings about Imperial methods. Allegedly shot his commander in the back.");
        setGameText("While on Tatooine, Local Trouble and Look Sir, Droids are Used Interrupts. Opponent may select target when using Friendly Fire at same site as Felth.");
        addIcons(Icon.A_NEW_HOPE, Icon.WARRIOR);
        addKeywords(Keyword.SANDTROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new UsedInterruptModifier(self, Filters.or(Filters.Local_Trouble, Filters.Look_Sir_Droids),
                new OnCondition(self, Title.Tatooine)));
        modifiers.add(new PlayerToSelectCardTargetAtLocationModifier(self, Filters.Friendly_Fire, new AtCondition(self, Filters.site),
                game.getOpponent(self.getOwner()), Filters.sameSite(self)));
        return modifiers;
    }
}
