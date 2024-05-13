package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractCreatureVehicle;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextAbilityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Vehicle
 * Subtype: Creature
 * Title: Eopie
 */
public class Card11_048 extends AbstractCreatureVehicle {
    public Card11_048() {
        super(Side.LIGHT, 5, 1, 1, null, 2, 2, 3, "Eopie", Uniqueness.UNRESTRICTED, ExpansionSet.TATOOINE, Rarity.C);
        setLore("Herd animal native to Tatooine. Adults are used as beasts of burden, while the young and elderly eopies are useful for consuming desert weeds.");
        setGameText("May add 2 'riders' (passengers). Ability = 1/4. Moves for free. While 'ridden' by Amidala in battle, adds one battle destiny.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
        addKeywords(Keyword.EOPIE);
        setPassengerCapacity(2);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextAbilityModifier(self, 0.25));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesForFreeModifier(self));
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new HasAboardCondition(self, Filters.Amidala),
                new InBattleCondition(self)), 1));
        return modifiers;
    }
}
