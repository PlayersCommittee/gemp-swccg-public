package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
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
 * Title: We Didn't Hit It
 */
public class Card14_038 extends AbstractNormalEffect {
    public Card14_038() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "We Didn't Hit It", Uniqueness.UNIQUE);
        setLore("When severely outnumbered by opponents, any help is good help.");
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