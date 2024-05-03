package com.gempukku.swccgo.cards.set304.dark;

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
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Hammer
 */
public class Card304_016 extends AbstractAlien {
    public Card304_016() {
        super(Side.DARK, 2, 3, 3, 1, 2, "Hammer", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("A former member of the Scholae Palatinae Legion, Hammer served in the Battle of Antei and was saved by Thran. Hammer is always the last to leave the battlefield.");
        setGameText("Each of your squadmates (Thran and Thran's personal guard) present is defense value +3. Power +4 while defending a battle. Adds 2 to his total weapon destiny when firing a blaster. Your characters present may not have their forfeit value reduced.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.THRAN_GUARD, Keyword.MALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.Thran, Filters.THRAN_GUARD), Filters.present(self)), 3));
        modifiers.add(new PowerModifier(self, new DefendingBattleCondition(self), 4));
        modifiers.add(new TotalWeaponDestinyForWeaponFiredByModifier(self, 2, Filters.blaster));
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.present(self))));
        return modifiers;
    }
}
