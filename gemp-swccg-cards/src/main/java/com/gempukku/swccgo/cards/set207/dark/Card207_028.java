package com.gempukku.swccgo.cards.set207.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 7
 * Type: Starship
 * Subtype: Starfighter
 * Title: Meson Martinet
 */
public class Card207_028 extends AbstractStarfighter {
    public Card207_028() {
        super(Side.DARK, 3, 3, 3, null, 4, 4, 4, Title.Meson_Martinet, Uniqueness.UNIQUE, ExpansionSet.SET_7, Rarity.V);
        setGameText("May add 2 pilots and 3 passengers. During battle, your total battle destiny is +1 for each pirate aboard. While Quiggold or Sidon piloting, immune to attrition < 5.");
        addIcons(Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.EPISODE_VII, Icon.VIRTUAL_SET_7);
        addModelType(ModelType.FREIGHTER);
        setPilotCapacity(2);
        setPassengerCapacity(3);
        setMatchingPilotFilter(Filters.or(Filters.Quiggold, Filters.Sidon));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.or(Filters.Quiggold, Filters.Sidon)), 5));
        modifiers.add(new TotalBattleDestinyModifier(self, new InBattleEvaluator(self, Filters.and(Filters.pirate, Filters.aboard(self))), playerId));
        return modifiers;
    }
}
