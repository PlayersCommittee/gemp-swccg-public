package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Title: Another Pathetic Lifeform
 */
public class Card12_039 extends AbstractNormalEffect {
    public Card12_039() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Another Pathetic Lifeform", Uniqueness.UNIQUE);
        setLore("Young Obi-Wan has much to learn about the living Force. Patience with others is also high on that list.");
        setGameText("Deploy on table. Once during each of opponent's turns, if you occupy a battleground site, may activate 1 Force. While opponent has a non-unique alien or non-unique starfighter in battle, opponent may not draw more than two battle destiny. (Immune to Alter.)");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canSpotLocation(game, Filters.and(Filters.battleground_site, Filters.occupies(playerId)))
                && GameConditions.canActivateForce(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Activate 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, new InBattleCondition(self, Filters.and(Filters.opponents(self),
                Filters.non_unique, Filters.or(Filters.alien, Filters.starfighter))), 2, opponent));
        return modifiers;
    }
}