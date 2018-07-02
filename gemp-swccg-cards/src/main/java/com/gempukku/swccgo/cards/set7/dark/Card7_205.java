package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Narthax
 */
public class Card7_205 extends AbstractImperial {
    public Card7_205() {
        super(Side.DARK, 3, 2, 3, 2, 3, "Sergeant Narthax", Uniqueness.UNIQUE);
        setLore("Commanded a squad of snowtroopers in the Battle of Hoth. Trains snowtroopers in extreme-weather survival tactics. Originally from Ukio.");
        setGameText("While on Hoth, makes all your snowtroopers immune to Ice Storm. When in battle on Hoth with another snowtrooper, adds one battle destiny. Precise Attack targeting at same or related site is a Used Interrupt. Power -1 when not on Hoth.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.SNOWTROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition onHothCondition = new OnCondition(self, Title.Hoth);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.snowtrooper), onHothCondition, Title.Ice_Storm));
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new InBattleWithCondition(self, Filters.snowtrooper),
                onHothCondition), 1));
        modifiers.add(new UsedInterruptModifier(self, Filters.Precise_Attack, new DuringBattleAtCondition(Filters.sameOrRelatedSite(self))));
        modifiers.add(new PowerModifier(self, new NotCondition(onHothCondition), -1));
        return modifiers;
    }
}
