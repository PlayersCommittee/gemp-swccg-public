package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Character
 * Subtype: Resistance
 * Title: Admiral U.O. Statura
 */
public class Card208_001 extends AbstractResistance {
    public Card208_001() {
        super(Side.LIGHT, 3, 3, 2, 2, 5, "Admiral U.O. Statura", Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setLore("Leader.");
        setGameText("At same location, Resistance characters and [Resistance] starships are each defence value and forfeit +1. While at an [Episode VII] location with a Resistance leader, adds one battle destiny.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_8);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.or(Filters.Resistance_character, Filters.and(Filters.starship, Icon.RESISTANCE)), Filters.atSameLocation(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, filter, 1));
        modifiers.add(new ForfeitModifier(self, filter, 1));
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new AtCondition(self,
                Filters.and(Filters.location, Icon.EPISODE_VII)), new WithCondition(self, Filters.Resistance_leader)), 1));
        return modifiers;
    }
}
