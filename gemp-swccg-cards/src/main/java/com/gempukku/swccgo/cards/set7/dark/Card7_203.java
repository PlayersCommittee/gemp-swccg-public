package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Major Enfield
 */
public class Card7_203 extends AbstractImperial {
    public Card7_203() {
        super(Side.DARK, 2, 3, 3, 2, 3, "Sergeant Major Enfield", Uniqueness.UNIQUE);
        setLore("Commander of a platoon of Death Star troopers. Maintains security around the detention block area. Often serves as Lt. Shann Childsen's attache.");
        setGameText("When in a battle with another Death Star trooper, adds one battle destiny. Your Death Star troopers are deploy -1 to same Death Star site. Trooper Charge targeting at same or related site is a Used Interrupt. Power -1 when not on Death Star.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.COMMANDER, Keyword.DEATH_STAR_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new InBattleWithCondition(self, Filters.Death_Star_trooper), 1));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.not(self), Filters.Death_Star_trooper),
                -1, Filters.and(Filters.sameSite(self), Filters.Death_Star_site)));
        modifiers.add(new UsedInterruptModifier(self, Filters.or(Filters.Trooper_Charge), new DuringBattleAtCondition(Filters.sameOrRelatedSite(self))));
        modifiers.add(new PowerModifier(self, new NotCondition(new OnCondition(self, Title.Death_Star)), -1));
        return modifiers;
    }
}
