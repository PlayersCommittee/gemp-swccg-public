package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
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
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Royal Guard
 */
public class Card9_119 extends AbstractImperial {
    public Card9_119() {
        super(Side.DARK, 3, 2, 3, 2, 3, "Royal Guard");
        setLore("Member of Emperor's Royal Guard. Completely subservient. Royal Guards must fight one of their own to the death in order to complete their training.");
        setGameText("Deploys only on Coruscant or to Emperor's site (or related site). When armed with a Force pike and defending a battle, adds one battle destiny. Emperor may not be targeted by weapons unless all Royal Guards present with Emperor are 'hit'.");
        addIcons(Icon.DEATH_STAR_II, Icon.WARRIOR);
        addKeywords(Keyword.ROYAL_GUARD);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_on_Coruscant, Filters.locationAndCardsAtLocation(Filters.sameOrRelatedSiteAs(self, Filters.Emperor)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new ArmedWithCondition(self, Filters.Force_pike),
                new DefendingBattleCondition(self)), 1));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.Emperor, new OnTableCondition(self, Filters.and(Filters.Royal_Guard,
                Filters.not(Filters.hit), Filters.presentWith(self, Filters.Emperor)))));
        return modifiers;
    }
}
