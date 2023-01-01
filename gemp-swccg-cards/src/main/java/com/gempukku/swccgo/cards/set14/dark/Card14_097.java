package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Effect
 * Title: Fighters Straight Ahead
 */
public class Card14_097 extends AbstractNormalEffect {
    public Card14_097() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Fighters Straight Ahead", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("Sometimes a swarm of starfighters can look more imposing than it really is. Mostly though, it's just best to avoid them altogether.");
        setGameText("Deploy on table. Your Force generation is +1 at systems you control. Opponent's non-unique starfighters are deploy +1 (or +2 to a system you occupy). (Immune to Alter.)");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter opponentsNonuniqueStarfighters = Filters.and(Filters.opponents(self), Filters.non_unique, Filters.starfighter);
        Filter systemYouOccupy = Filters.and(Filters.system, Filters.occupies(playerId));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.system, Filters.controls(playerId)), 1, playerId));
        modifiers.add(new DeployCostToLocationModifier(self, opponentsNonuniqueStarfighters, 1, Filters.not(systemYouOccupy)));
        modifiers.add(new DeployCostToLocationModifier(self, opponentsNonuniqueStarfighters, 2, systemYouOccupy));
        return modifiers;
    }
}