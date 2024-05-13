package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red Squadron 6
 */
public class Card208_028 extends AbstractStarfighter {
    public Card208_028() {
        super(Side.LIGHT, 6, 2, 3, null, 4, 5, 4, "Red Squadron 6", Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setGameText("May add 1 pilot. While Ello Asty piloting, attrition against opponent is +1 here.");
        addIcons(Icon.EPISODE_VII, Icon.NAV_COMPUTER, Icon.RESISTANCE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_8);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        addKeywords(Keyword.RED_SQUADRON);
        setMatchingPilotFilter(Filters.Ello_Asty);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, Filters.here(self), new HasPilotingCondition(self, Filters.Ello_Asty), 1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
