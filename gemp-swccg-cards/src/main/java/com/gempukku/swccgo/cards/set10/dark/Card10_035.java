package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ModifyManeuverUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Interrupt
 * Subtype: Used
 * Title: Dark Maneuvers & Tallon Roll
 */
public class Card10_035 extends AbstractUsedInterrupt {
    public Card10_035() {
        super(Side.DARK, 6, "Dark Maneuvers & Tallon Roll");
        addComboCardTitles(Title.Dark_Maneuvers, Title.Tallon_Roll);
        setGameText("Add 2 to maneuver and 1 to power of any TIE for the remainder of this turn. (Interrupt may even affect the result immediately after a destiny draw targeting the TIE's maneuver.) OR Target two starfighters (your TIE/ln and any Rebel starfighter) present at same system or sector. Each player draws destiny. Opponent totals destiny and starship's power. You total destiny, TIE's power, and TIE's maneuver. Lowest total loses starfighter (if tied, neither starfighter is lost).");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter filter = Filters.and(Filters.TIE, Filters.hasManeuver, Filters.piloted);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            // Generate action using common method
            PlayInterruptAction action = generatePlayInterruptAction(playerId, game, self, filter);
            if (action != null) {
                actions.add(action);
            }
        }

        final String opponent = game.getOpponent(playerId);
        Filter opponentsStarfighterFilter = Filters.and(Filters.opponents(self), Filters.Rebel_starfighter, Filters.presentAt(Filters.system_or_sector), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST));
        final Filter yourStarfighterFilter = Filters.and(Filters.your(self), CardSubtype.STARFIGHTER, Filters.piloted, Filters.TIE_ln, Filters.presentWith(self, opponentsStarfighterFilter));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, yourStarfighterFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target two starfighters");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target your starfighter", yourStarfighterFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard yourStarfighter) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Target opponent's starfighter", TargetingReason.TO_BE_LOST,
                                            Filters.and(Filters.opponents(self), Filters.Rebel_starfighter, Filters.presentWith(yourStarfighter))) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, final PhysicalCard opponentsStarfighter) {
                                            action.addAnimationGroup(yourStarfighter, opponentsStarfighter);
                                            // Allow response(s)
                                            action.allowResponses("Target " + GameUtils.getCardLink(yourStarfighter) + " and " + GameUtils.getCardLink(opponentsStarfighter),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard yourFinalTarget = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard opponentsFinalTarget = targetingAction.getPrimaryTargetCard(targetGroupId2);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            return Collections.singletonList(yourFinalTarget);
                                                                        }
                                                                        @Override
                                                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> playersDestinyCardDraws, List<Float> playersDestinyDrawValues, final Float playersTotalDestiny) {
                                                                            action.appendEffect(
                                                                                    new DrawDestinyEffect(action, opponent) {
                                                                                        @Override
                                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                                            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.TALLON_ROLL__OPPONENT_ADDS_MANEUVER_AND_ABILITY)) {
                                                                                                return Collections.singletonList(opponentsFinalTarget);
                                                                                            }
                                                                                            return Collections.emptyList();
                                                                                        }
                                                                                        @Override
                                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> opponentsDestinyCardDraws, List<Float> opponentsDestinyDrawValues, Float opponentsTotalDestiny) {
                                                                                            GameState gameState = game.getGameState();
                                                                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                                                                                            gameState.sendMessage(playerId + "'s destiny: " + (playersTotalDestiny != null ? GuiUtils.formatAsString(playersTotalDestiny) : "Failed destiny draw"));
                                                                                            gameState.sendMessage(opponent + "'s destiny: " + (opponentsTotalDestiny != null ? GuiUtils.formatAsString(opponentsTotalDestiny) : "Failed destiny draw"));
                                                                                            float yourPower = modifiersQuerying.getPower(gameState, yourFinalTarget);
                                                                                            gameState.sendMessage(GameUtils.getCardLink(yourFinalTarget) + "'s power: " + GuiUtils.formatAsString(yourPower));
                                                                                            float yourManeuver = modifiersQuerying.getManeuver(gameState, yourFinalTarget);
                                                                                            gameState.sendMessage(GameUtils.getCardLink(yourFinalTarget) + "'s maneuver: " + GuiUtils.formatAsString(yourManeuver));

                                                                                            float opponentsPower = modifiersQuerying.getPower(gameState, opponentsFinalTarget);
                                                                                            gameState.sendMessage(GameUtils.getCardLink(opponentsFinalTarget) + "'s power: " + GuiUtils.formatAsString(opponentsPower));
                                                                                            // Determine if opponent adds maneuver and pilots ability
                                                                                            float opponentsManeuver = 0;
                                                                                            float opponentsAbility = 0;

                                                                                            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.TALLON_ROLL__OPPONENT_ADDS_MANEUVER_AND_ABILITY)) {
                                                                                                opponentsManeuver = modifiersQuerying.getManeuver(gameState, opponentsFinalTarget);
                                                                                                gameState.sendMessage(GameUtils.getCardLink(opponentsFinalTarget) + "'s maneuver: " + GuiUtils.formatAsString(opponentsManeuver));
                                                                                                opponentsAbility = modifiersQuerying.getHighestAbilityPiloting(gameState, opponentsFinalTarget, false, false);
                                                                                                gameState.sendMessage(GameUtils.getCardLink(opponentsFinalTarget) + "'s pilot's ability: " + GuiUtils.formatAsString(opponentsAbility));
                                                                                            }

                                                                                            float playersTotal = (playersTotalDestiny != null ? playersTotalDestiny : 0) + yourPower + yourManeuver;
                                                                                            playersTotal = modifiersQuerying.getCalculationTotalTargetingCard(gameState, self, yourFinalTarget, playersTotal);
                                                                                            gameState.sendMessage(playerId + "'s total: " + GuiUtils.formatAsString(playersTotal));
                                                                                            float opponentsTotal = (opponentsTotalDestiny != null ? opponentsTotalDestiny : 0) + opponentsPower + opponentsManeuver + opponentsAbility;
                                                                                            opponentsTotal = modifiersQuerying.getCalculationTotalTargetingCard(gameState, self, opponentsFinalTarget, opponentsTotal);
                                                                                            gameState.sendMessage(opponent + "'s total: " + GuiUtils.formatAsString(opponentsTotal));
                                                                                            if (playersTotal > opponentsTotal) {
                                                                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(opponentsFinalTarget) + " to be lost");
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, opponentsFinalTarget));
                                                                                            }
                                                                                            else if (opponentsTotal > playersTotal) {
                                                                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(yourFinalTarget) + " to be lost");
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, yourFinalTarget));
                                                                                            }
                                                                                            else {
                                                                                                gameState.sendMessage("Result: No result");
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            );
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.TIE, Filters.hasManeuver, Filters.piloted);

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(game, effectResult, filter)) {
            Collection<PhysicalCard> targetedCards = ((DestinyDrawnResult) effectResult).getAbilityManeuverOrDefenseValueTargeted();

            // Generate action using common method
            PlayInterruptAction action = generatePlayInterruptAction(playerId, game, self, Filters.and(Filters.in(targetedCards), filter));
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private PlayInterruptAction generatePlayInterruptAction(final String playerId, final SwccgGame game, final PhysicalCard self, Filter filter) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Add 2 to maneuver and 1 to power");
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose TIE", Integer.MAX_VALUE, true, filter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.addAnimationGroup(targetedCard);
                        // Allow response(s)
                        action.allowResponses("Add 2 to maneuver and 1 to power of " + GameUtils.getCardLink(targetedCard),
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new ModifyManeuverUntilEndOfTurnEffect(action, finalTarget, 2));
                                        action.appendEffect(
                                                new ModifyPowerUntilEndOfTurnEffect(action, finalTarget, 1));
                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }
}