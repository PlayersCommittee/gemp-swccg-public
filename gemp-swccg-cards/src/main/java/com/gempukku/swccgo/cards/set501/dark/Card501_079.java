package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.TotalAbilityMoreThanCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetTotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * SubType: Imperial
 * Title: Ninth Sister
 */
public class Card501_079 extends AbstractImperial {
    public Card501_079() {
        super(Side.DARK, 1, 5, 5, 5, 4, "Ninth Sister", Uniqueness.UNIQUE);
        setLore("Female Dowutin. Inquisitor.");
        setGameText("Defense value -2 if opponent's weapon here. Unless opponent has total ability > 4 at same site, opponent's total battle destiny here = 0.");
        setSpecies(Species.DOWUTIN);
        addKeywords(Keyword.INQUISITOR, Keyword.FEMALE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        setTestingText("Ninth Sister");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, new HereCondition(self, Filters.and(Filters.your(opponent), Filters.weapon)), -2));
        modifiers.add(new ResetTotalBattleDestinyModifier(self, Filters.sameSite(self), new AndCondition(new InBattleCondition(self),
                new UnlessCondition(new TotalAbilityMoreThanCondition(opponent, 4, Filters.sameSite(self)))), 0, opponent));
        return modifiers;
    }
}
