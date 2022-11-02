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
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextAbilityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Vehicle
 * Subtype: Creature
 * Title: URoRRuR'R'R's Bantha
 */
public class Card7_317 extends AbstractCreatureVehicle {
    public Card7_317() {
        super(Side.DARK, 3, 1, 2, null, 2, 1, 4, "URoRRuR'R'R's Bantha", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Raised by RR'uruurr for personal use by URoRRuR'R'R. Alpha male of his bantha herd. Has trampled many Jawas. Nicknamed 'Rrr'ur'R.");
        setGameText("May add 2 'riders' (passengers). Ability = 1/2. Adds 1 to power of each of your Tusken Raiders and other banthas present. When ridden by URoRRuR'R'R, adds one battle destiny.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.BANTHA);
        setPassengerCapacity(2);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextAbilityModifier(self, 0.5));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.Tusken_Raider,
                Filters.and(Filters.other(self), Filters.bantha)), Filters.present(self)), 1));
        modifiers.add(new AddsBattleDestinyModifier(self, new HasAboardCondition(self, Filters.URoRRuRRR), 1));
        return modifiers;
    }
}
