package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Macroscan
 */
public class Card1_224 extends AbstractNormalEffect {
    public Card1_224() {
        super(Side.DARK, 3, PlayCardZoneOption.OPPONENTS_SIDE_OF_TABLE, "Macroscan");
        setLore("Electrobinocular view. Readouts list object's true and relative azimuth, elevation and range. Built-in night vision.");
        setGameText("Use 2 Force to deploy near opponent's Reserve Deck. At any time, you may use 1 Force to peek at the top card of that deck. If 'nighttime conditions' exist anywhere on table, you may peek at the top three cards.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasReserveDeck(game, opponent)) {
            int numCards = GameConditions.canSpotLocation(game, Filters.under_nighttime_conditions) ? 3 : 1;

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top of opponent's Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckEffect(action, playerId, opponent, numCards));
            return Collections.singletonList(action);
        }
        return null;
    }
}