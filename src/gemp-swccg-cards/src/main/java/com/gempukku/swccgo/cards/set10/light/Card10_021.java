package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ShuffleHandAndUsedPileIntoReserveDeckEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardBeingPlayedForCancelingEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.SenseAlterDestinySuccessfulResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Sense & Recoil In Fear
 */
public class Card10_021 extends AbstractUsedOrLostInterrupt {
    public Card10_021() {
        super(Side.LIGHT, 3, "Sense & Recoil In Fear", Uniqueness.UNRESTRICTED, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        addComboCardTitles(Title.Sense, Title.Recoil_In_Fear);
        setGameText("USED: Target your highest-ability character and one just-played Interrupt. Draw destiny. If destiny < ability of target character, cancel target Interrupt. LOST: Use 3 Force. Each player counts cards in hand, then places entire hand and Used Pile onto Reserve Deck; reshuffle. Each player then draws from Reserve Deck the counted number of cards to create a new hand.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self) {
        final Filter characterFilter = Filters.and(Filters.your(self), Filters.highestAbilityCharacter(self, playerId), Filters.notPreventedFromApplyingAbilityForSenseAlterDestiny);
        Filter interruptFilter = Filters.and(Filters.Interrupt, Filters.not(Filters.dejarikHologramAtHolosite));
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.RECOIL_IN_FEAR__MAY_NOT_BE_PLAYED_EXCEPT_TO_CANCEL_INTERRUPT)) {
            interruptFilter = Filters.and(Filters.opponents(self), interruptFilter);
        }

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, interruptFilter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.canSpot(game, self, characterFilter)) {
            final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            String ownerText = (respondableEffect.getCard().getOwner().equals(playerId) ? "your " : "");
            action.setText("Draw destiny to cancel " + ownerText + GameUtils.getFullName(respondableEffect.getCard()));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardBeingPlayedForCancelingEffect(action, respondableEffect) {
                        @Override
                        protected void cardTargetedToBeCanceled(final PhysicalCard targetedEffect) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose a highest-ability character", characterFilter) {
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
                                                                                gameState.sendMessage("Result: Failed due to no highest-ability character");
                                                                                return;
                                                                            }

                                                                            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), finalCharacter);
                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                                            if (totalDestiny < ability) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new TriggeringResultEffect(action,
                                                                                                new SenseAlterDestinySuccessfulResult(playerId)));
                                                                                action.appendEffect(
                                                                                        new CancelCardBeingPlayedEffect(action, respondableEffect));
                                                                            }
                                                                            else {
                                                                                gameState.sendMessage("Result: Failed");
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
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasHand(game, opponent)
                && !GameConditions.hasGameTextModification(game, self, ModifyGameTextType.RECOIL_IN_FEAR__MAY_NOT_BE_PLAYED_EXCEPT_TO_CANCEL_INTERRUPT)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make each player redraw hand");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            GameState gameState = game.getGameState();
                            boolean canRemoveCardsFromOpponentsHand = !game.getModifiersQuerying().mayNotRemoveCardsFromOpponentsHand(gameState, self, playerId);

                            // Perform result(s)
                            int numCardsToDraw = game.getGameState().getHand(playerId).size();
                            action.appendEffect(
                                    new ShuffleHandAndUsedPileIntoReserveDeckEffect(action, playerId));
                            if (numCardsToDraw > 0) {
                                action.appendEffect(
                                        new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, numCardsToDraw));
                            }
                            if(canRemoveCardsFromOpponentsHand){
                                int numOpponentsCardsToDraw = game.getGameState().getHand(opponent).size();
                                action.appendEffect(
                                        new ShuffleHandAndUsedPileIntoReserveDeckEffect(action, opponent));
                                if (numOpponentsCardsToDraw > 0) {
                                    action.appendEffect(
                                            new DrawCardsIntoHandFromReserveDeckEffect(action, opponent, numOpponentsCardsToDraw));
                                }
                            }else{
                                game.getGameState().sendMessage(opponent + " is not allowed to remove cards from " + opponent + "'s hand");
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}