package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalCarbonFreezingDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Device
 * Title: Carbonite Chamber Console
 */
public class Card5_107 extends AbstractDevice {
    public Card5_107() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Carbonite_Chamber_Console, Uniqueness.UNIQUE);
        setLore("Most often used to freeze Tibanna gas for transport. Modified by Ugloste to work on humans. Intended to capture Luke Skywalker, the Emperor's prize.");
        setGameText("Deploy on Carbonite Chamber. Adds 3 to Carbon-Freezing destiny. Also, once during each of your turns, you may use 1 Force to take one Ugnaught, Prepare The Chamber or Carbon-Freezing into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Carbonite_Chamber;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalCarbonFreezingDestinyModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CARBONITE_CHAMBER_CONSOLE__UPLOAD_UGNAUGHT_PREPARE_THE_CHAMBER_OR_CARBON_FREEZING;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take an Ugnaught, Prepare The Chamber, or Carbon-Freezing into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Ugnaught, Filters.Prepare_The_Chamber, Filters.Carbon_Freezing), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}