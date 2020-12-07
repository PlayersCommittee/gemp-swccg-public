package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueIncreasedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Imperial
 * Title: Fifth Brother
 */
public class Card213_006 extends AbstractImperial {
    public Card213_006() {
        super(Side.DARK, 2, 4, 4, 5, 6, "Fifth Brother", Uniqueness.UNIQUE);
        setLore("Inquisitor.");
        setGameText("Power +3 and defense value -2 while with a Jedi, Padawan, or 'Hatred' card. Characters here may not have their forfeit increased.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeyword(Keyword.INQUISITOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition withJediPadawanOrHatredCardCondition = new OrCondition(
                new WithCondition(self, Filters.or(Filters.Jedi, Filters.padawan, Filters.hasStacked(Filters.hatredCard))),
                new AtCondition(self, Filters.hasStacked(Filters.hatredCard))
        );
        Filter charactersHere = Filters.and(Filters.character, Filters.here(self));

        modifiers.add(new PowerModifier(self, withJediPadawanOrHatredCardCondition, 3));
        modifiers.add(new DefenseValueModifier(self, withJediPadawanOrHatredCardCondition, -2));
        modifiers.add(new MayNotHaveForfeitValueIncreasedModifier(self, charactersHere));
        return modifiers;
    }
}
