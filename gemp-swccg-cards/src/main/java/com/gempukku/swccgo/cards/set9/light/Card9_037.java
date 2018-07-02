package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Let's Keep A Little Optimism Here
 */
public class Card9_037 extends AbstractNormalEffect {
    public Card9_037() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Lets_Keep_A_Little_Optimism_Here, Uniqueness.UNIQUE);
        setLore("The heroes of the Rebellion know that where there is life, there is hope.");
        setGameText("Deploy on table. While you occupy a Renegade planet location, operatives are forfeit = 0, operatives do not add to Force drains and your Force drains may not be reduced. At any time, you may place Effect out of play to retrieve 1 Force. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition occupyRenegadePlanetLocation = new OccupiesCondition(playerId, Filters.Renegade_planet_location);
        Filter operatives = Filters.and(Filters.operative, Filters.canBeTargetedBy(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetForfeitModifier(self, operatives, occupyRenegadePlanetLocation, 0));
        modifiers.add(new CancelForceDrainBonusesFromCardModifier(self, operatives, occupyRenegadePlanetLocation));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, occupyRenegadePlanetLocation, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
        action.setText("Place out of play to retrieve 1 Force");
        action.setActionMsg("Retrieve 1 Force");
        // Pay cost(s)
        action.appendCost(
                new PlaceCardOutOfPlayFromTableEffect(action, self));
        // Perform result(s)
        action.appendEffect(
                new RetrieveForceEffect(action, playerId, 1));
        return Collections.singletonList(action);
    }
}