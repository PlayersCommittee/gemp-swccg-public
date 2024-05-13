package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyForWeaponFiredByModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Alien
 * Title: Chokk
 */
public class Card12_099 extends AbstractAlien {
    public Card12_099() {
        super(Side.DARK, 3, 3, 4, 1, 3, "Chokk", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setArmor(4);
        setLore("Klatooinian bodyguard currently in the paid service of Jabba the Hutt. Chokk takes pride in his work, and he is very good at it.");
        setGameText("Each of your alien leaders present is defense value +2. Power +3 while defending a battle. Adds 2 to his total weapon destiny when firing a blaster. Your characters present may not have their forfeit value reduced.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
        addKeyword(Keyword.BODYGUARD);
        setSpecies(Species.KLATOOINIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.your(self), Filters.alien_leader, Filters.present(self)), 2));
        modifiers.add(new PowerModifier(self, new DefendingBattleCondition(self), 3));
        modifiers.add(new TotalWeaponDestinyForWeaponFiredByModifier(self, 2, Filters.blaster));
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.present(self))));
        return modifiers;
    }
}
