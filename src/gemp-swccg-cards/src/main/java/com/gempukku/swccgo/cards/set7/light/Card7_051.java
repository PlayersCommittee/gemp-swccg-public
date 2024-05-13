package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Rebel
 * Title: Yavin 4 Trooper
 */
public class Card7_051 extends AbstractRebel {
    public Card7_051() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, "Yavin 4 Trooper", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.F);
        setLore("Elite troop force assigned to Massassi Base in the jungles of Yavin 4. Responsible for monitoring the perimeter of the Rebel outpost.");
        setGameText("Deploys only to a Yavin 4 site or any jungle. Power -1 when not at a Yavin 4 site. Warrior's Courage and Blast The Door, Kid! played at same site are Used Interrupts. Forfeit +2 when present with a Rebel leader.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.TROOPER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.locationAndCardsAtLocation(Filters.or(Filters.Yavin_4_site, Filters.jungle));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new NotCondition(new AtCondition(self, Filters.Yavin_4_site)), -1));
        modifiers.add(new UsedInterruptModifier(self, Filters.or(Filters.Warriors_Courage, Filters.Blast_The_Door_Kid),
                new DuringBattleAtCondition(Filters.sameSite(self))));
        modifiers.add(new ForfeitModifier(self, new PresentWithCondition(self, Filters.Rebel_leader), 2));
        return modifiers;
    }
}
