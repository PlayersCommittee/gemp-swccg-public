package com.gempukku.swccgo.cards.set7.light;

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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextAbilityModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Vehicle
 * Subtype: Creature
 * Title: Ronto
 */
public class Card7_155 extends AbstractCreatureVehicle {
    public Card7_155() {
        super(Side.LIGHT, 5, 2, 3, 3, null, 2, 3, "Ronto", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("Creatures used as beasts of burden by Jawas. Tremendous strength makes them excellent pack animals. Very skittish around repulsorlift vehicles.");
        setGameText("May add 2 'riders' (passengers). Ability = 1/4. When 'ridden' by a Jawa, moves for free and draws one battle destiny if not able to otherwise.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.RONTO);
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
        Condition riddenByJawa = new HasAboardCondition(self, Filters.Jawa);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesForFreeModifier(self, riddenByJawa));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, riddenByJawa, 1));
        return modifiers;
    }
}
