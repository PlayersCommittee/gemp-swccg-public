package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawRaceDestinyEffect;
import com.gempukku.swccgo.logic.effects.FinishPodraceEffect;
import com.gempukku.swccgo.logic.effects.InitiatePodraceEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Epic Event
 * Title: Boonta Eve Podrace
 */
public class Card11_079 extends AbstractEpicEventDeployable {
    public Card11_079() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.Boonta_Eve_Podrace);
        setGameText("Deploy on Podrace Arena. Once per game, either player may initiate a Podrace: During your control phase, each player may draw one race destiny. Each player may place any drawn race destiny on their Podracer (or on Podrace Arena if they have none) or in their Used Pile. During any move phase, winner is any player with a race total > 24 and greater than opponent's highest race total. Winner retrieves 6 Force and loser loses 6 Force. Place all race destiny cards in owner's Used Piles and all Podracers are lost.");
        addIcons(Icon.EPISODE_I, Icon.TATOOINE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Podrace_Arena;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.BOONTA_EVE_PODRACE__INITIATE_PODRACE;

        // Check condition(s)
        if (!GameConditions.isDuringPodrace(game)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Initiate a Podrace");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new InitiatePodraceEffect(action));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringPodraceInitiatedByCard(game, self)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canDrawRaceDestiny(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw race destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawRaceDestinyEffect(action));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.BOONTA_EVE_PODRACE__INITIATE_PODRACE;

        // Check condition(s)
        if (!GameConditions.isDuringPodrace(game)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Initiate a Podrace");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new InitiatePodraceEffect(action));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringPodraceInitiatedByCard(game, self)
                && GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canDrawRaceDestiny(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw race destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawRaceDestinyEffect(action));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringPodraceInitiatedByCard(game, self)
                && !GameConditions.isPodraceFinishing(game)
                && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            float playersRaceTotal = modifiersQuerying.getHighestRaceTotal(gameState, playerId);
            float opponentsRaceTotal = modifiersQuerying.getHighestRaceTotal(gameState, opponent);
            String winner = null;
            String loser = null;
            if (playersRaceTotal > 24 && playersRaceTotal > opponentsRaceTotal) {
                winner = playerId;
                loser = opponent;
            }
            else if (opponentsRaceTotal > 24 && opponentsRaceTotal > playersRaceTotal) {
                winner = opponent;
                loser = playerId;
            }
            if (winner != null && loser != null) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Finish Podrace");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FinishPodraceEffect(action, 6, 6));
                return Collections.singletonList(action);

            }
        }
        return null;
    }
}