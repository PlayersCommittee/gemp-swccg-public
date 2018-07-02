package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.BlownAwayCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Venka
 */
public class Card4_105 extends AbstractImperial {
    public Card4_105() {
        super(Side.DARK, 2, 2, 2, 2, 3, "Lieutenant Venka", Uniqueness.UNIQUE);
        setLore("Worked hard for a transfer to the Executor. One of the many noncommissioned personnel promoted to replace the vast number of officers lost during the Death Star disaster.");
        setGameText("Power +2 when at an Executor site or same site as Tarkin or Chief Bast, or if the Death Star has been 'blown away.' Fear Will Keep Them In Line is destiny +2 and, when it adds 1 to your power, also adds 1 to attrition against opponent.");
        addIcons(Icon.DAGOBAH, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OrCondition(new AtCondition(self, Filters.Executor_site),
                new AtSameSiteAsCondition(self, Filters.or(Filters.Tarkin, Filters.Chief_Bast)), new BlownAwayCondition(Filters.Death_Star_system)), 2));
        modifiers.add(new DestinyModifier(self, Filters.Fear_Will_Keep_Them_In_Line, 2));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Fear_Will_Keep_Them_In_Line, ModifyGameTextType.FEAR_WILL_KEEP_THEM_IN_LINE__ADDS_1_TO_ATTRITION));
        return modifiers;
    }
}
