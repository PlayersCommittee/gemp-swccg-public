package com.gempukku.swccgo.cards.set214.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfCardPlayedModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardBeingPlayedForCancelingEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsAtSameLocationEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.SenseAlterDestinySuccessfulResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Alter & Friendly Fire (V)
 */
public class Card214_013 extends AbstractUsedOrLostInterrupt {
    public Card214_013() {
        super(Side.LIGHT, 4, "Alter & Friendly Fire", Uniqueness.UNRESTRICTED, ExpansionSet.SET_14, Rarity.V);
        addComboCardTitles(Title.Alter, Title.Friendly_Fire);
        setVirtualSuffix(true);
        setGameText("USED: Cancel Sense." +
                "LOST: Target an Effect, Political Effect or Utinni Effect, and one of your characters on table. Draw destiny. If destiny < character's ability, target Effect is canceled. " +
                "OR If a battle was just initiated as a site where opponent has four or more characters, draw destiny. If destiny < numbers of opponent's character at that site, opponent chooses one to be lost (you lose no Force to There is no Try). (Immune to opponent's Objective).");
        addIcons(Icon.REFLECTIONS_II, Icon.VIRTUAL_SET_14);
        setImmuneToOpponentsObjective(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final Filter senseFilter = Filters.Sense;
        final Filter effectFilter = Filters.and(Filters.or(Filters.Effect, Filters.Political_Effect, Filters.Utinni_Effect), Filters.not(Filters.dejarikHologramAtHolosite));
        final Filter characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.notPreventedFromApplyingAbilityForSenseAlterDestiny);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, senseFilter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, effectFilter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.canSpot(game, self, characterFilter)) {
            final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            String ownerText = (respondableEffect.getCard().getOwner().equals(playerId) ? "your " : "");
            action.setText("Draw destiny to cancel " + ownerText + GameUtils.getFullName(respondableEffect.getCard()));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardBeingPlayedForCancelingEffect(action, respondableEffect) {
                        @Override
                        protected void cardTargetedToBeCanceled(final PhysicalCard targetedEffect) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose a character", characterFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCharacter) {
                                            action.addAnimationGroup(targetedCharacter);
                                            // Allow response(s)
                                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedEffect) + " by drawing destiny against " + GameUtils.getCardLink(targetedCharacter),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalCharacter = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            return finalCharacter != null ? Collections.singletonList(finalCharacter) : Collections.<PhysicalCard>emptyList();
                                                                        }

                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            GameState gameState = game.getGameState();
                                                                            if (totalDestiny == null) {
                                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                                return;
                                                                            }
                                                                            if (finalCharacter == null) {
                                                                                gameState.sendMessage("Result: Failed due to no character");
                                                                                return;
                                                                            }

                                                                            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), finalCharacter);
                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                                            if (totalDestiny < ability) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new AddUntilEndOfCardPlayedModifierEffect(action, self,
                                                                                                new NoForceLossFromCardModifier(self, Filters.There_Is_No_Try, playerId), null));
                                                                                action.appendEffect(
                                                                                        new TriggeringResultEffect(action,
                                                                                                new SenseAlterDestinySuccessfulResult(playerId)));
                                                                                action.appendEffect(
                                                                                        new CancelCardBeingPlayedEffect(action, respondableEffect));
                                                                            } else {
                                                                                gameState.sendMessage("Result: Failed");
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.and(Filters.site, Filters.canBeTargetedBy(self)))) {
            TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
            Filter characterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.at(Filters.battleLocation));
            if (GameConditions.canTarget(game, self, 4, targetingReason, characterFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setActionMsg("Make opponent lose a character");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardsAtSameLocationEffect(action, playerId, "Choose characters", 4, Integer.MAX_VALUE, targetingReason, Filters.and(Filters.opponents(self), Filters.character, Filters.at(Filters.battleLocation))) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }

                            @Override
                            protected boolean isTargetAll() {
                                return true;
                            }

                            @Override
                            protected void cardsTargeted(final int targetGroupId1, Collection<PhysicalCard> targetedCharacters) {
                                action.addAnimationGroup(targetedCharacters);
                                // Set secondary target filter(s)
                                action.addSecondaryTargetFilter(Filters.battleLocation);
                                // Allow response(s)
                                action.allowResponses("Make opponent lose one of the following characters: " + GameUtils.getAppendedNames(targetedCharacters),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the final targeted card(s)
                                                final Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId1);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId) {
                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, final List<Float> destinyDrawValues, Float totalDestiny) {
                                                                final GameState gameState = game.getGameState();
                                                                final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                                if (totalDestiny == null) {
                                                                    gameState.sendMessage("Result: No result due to failed destiny draw");
                                                                    return;
                                                                }

                                                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                int numberOfCharacters = finalCharacters.size();
                                                                gameState.sendMessage("Number of characters: " + numberOfCharacters);

                                                                if (totalDestiny < numberOfCharacters) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    String playerToChoose = modifiersQuerying.getPlayerToChooseCardTargetAtLocation(gameState, self, gameState.getBattleLocation(), opponent);
                                                                    action.appendEffect(
                                                                            new AddUntilEndOfCardPlayedModifierEffect(action, self,
                                                                                    new NoForceLossFromCardModifier(self, Filters.There_Is_No_Try, playerId), null));
                                                                    action.appendEffect(
                                                                            new TriggeringResultEffect(action,
                                                                                    new SenseAlterDestinySuccessfulResult(playerId)));
                                                                    action.appendEffect(
                                                                            new ChooseCardToLoseFromTableEffect(action, playerToChoose, Filters.in(finalCharacters)));
                                                                } else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final Filter effectFilter = Filters.or(Filters.Effect, Filters.Utinni_Effect, Filters.Political_Effect);
        final Filter characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.notPreventedFromApplyingAbilityForSenseAlterDestiny);

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, effectFilter)
                && GameConditions.canSpot(game, self, characterFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Draw destiny to cancel Effect, Political Effect, or Utinni Effect");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Effect, Political Effect, or Utinni Effect", TargetingReason.TO_BE_CANCELED, effectFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedEffect) {
                            action.addAnimationGroup(targetedEffect);
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose a character", characterFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedCharacter) {
                                            action.addAnimationGroup(targetedCharacter);
                                            // Allow response(s)
                                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedEffect) + " by drawing destiny against " + GameUtils.getCardLink(targetedCharacter),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalEffect = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard finalCharacter = targetingAction.getPrimaryTargetCard(targetGroupId2);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            return finalCharacter != null ? Collections.singletonList(finalCharacter) : Collections.<PhysicalCard>emptyList();
                                                                        }

                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            GameState gameState = game.getGameState();
                                                                            if (totalDestiny == null) {
                                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                                return;
                                                                            }
                                                                            if (finalCharacter == null) {
                                                                                gameState.sendMessage("Result: Failed due to no character");
                                                                                return;
                                                                            }

                                                                            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), finalCharacter);
                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                                            if (totalDestiny < ability) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new AddUntilEndOfCardPlayedModifierEffect(action, self,
                                                                                                new NoForceLossFromCardModifier(self, Filters.There_Is_No_Try, playerId), null));
                                                                                action.appendEffect(
                                                                                        new TriggeringResultEffect(action,
                                                                                                new SenseAlterDestinySuccessfulResult(playerId)));
                                                                                action.appendEffect(
                                                                                        new CancelCardOnTableEffect(action, finalEffect));
                                                                            } else {
                                                                                gameState.sendMessage("Result: Failed");
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}