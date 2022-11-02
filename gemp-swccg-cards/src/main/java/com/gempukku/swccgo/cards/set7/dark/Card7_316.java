package com.gempukku.swccgo.cards.set7.dark;

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
public class Card7_316 extends AbstractCreatureVehicle {
    public Card7_316() {
        super(Side.DARK, 5, 2, 3, 3, null, 2, 3, "Ronto", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Beast of burden often used by Jawas. Excellent sense of smell. Poor vision. Slow moving. Tough hide. Easily startled by fast-moving speeders.");
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
