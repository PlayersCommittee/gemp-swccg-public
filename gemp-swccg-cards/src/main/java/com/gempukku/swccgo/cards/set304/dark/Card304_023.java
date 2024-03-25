package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Scholae Palatinae, Royal Guard
 */
public class Card304_023 extends AbstractImperial {
    public Card304_023() {
        super(Side.DARK, 3, 2, 3, 2, 3, "Scholae Palatinae, Royal Guard", Uniqueness.UNRESTRICTED, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Similar to the Imperial Royal Guard, they are completely subservient to Scholae Palatinae's Emperor. Their training is equally as brutual with only one in four trainees surviving to complete the trianing.");
        setGameText("Deploys only on Seraph or to CSP Emperor's site (or related site). When armed with a Force pike and defending a battle, adds one battle destiny. CSP Emperor may not be targeted by weapons unless all Royal Guards present with CSP Emperor are 'hit'.");
        addIcons(Icon.CSP, Icon.WARRIOR);
        addKeywords(Keyword.CSP_ROYAL_GUARD);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_on_Seraph, Filters.locationAndCardsAtLocation(Filters.sameOrRelatedSiteAs(self, Filters.CSP_EMPEROR)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new ArmedWithCondition(self, Filters.Force_pike),
                new DefendingBattleCondition(self)), 1));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.CSP_EMPEROR, new OnTableCondition(self, Filters.and(Filters.CSP_ROYAL_GUARD,
                Filters.not(Filters.hit), Filters.presentWith(self, Filters.CSP_EMPEROR)))));
        return modifiers;
    }
}
