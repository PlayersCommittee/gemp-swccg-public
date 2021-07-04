package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
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
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.SenseAlterDestinySuccessfulResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Alter & Friendly Fire
 */
public class Card10_001 extends AbstractUsedOrLostInterrupt {
    public Card10_001() {
        super(Side.LIGHT, 4, "Alter & Friendly Fire");
        addComboCardTitles(Title.Alter, Title.Friendly_Fire);
        setGameText("USED: Target your highest-ability character and one Effect. Draw destiny. If destiny < ability of target character, cancel target Effect. LOST: If a battle was just initiated at a site where opponent has at least two characters and one weapon, draw destiny. If destiny is < number of opponent's characters at that site, opponent chooses one to be lost.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final Filter effectFilter = Filters.and(Filters.Effect, Filters.not(Filters.dejarikHologramAtHolosite));
        final Filter characterFilter = Filters.and(Filters.your(self), Filters.highestAbilityCharacter(self, playerId), Filters.notPreventedFromApplyingAbilityForSenseAlterDestiny);

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, effectFilter)
                && GameConditions.canSpot(game, self, characterFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Draw destiny to cancel Effect");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Effect", TargetingReason.TO_BE_CANCELED, effectFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedEffect) {
                            action.addAnimationGroup(targetedEffect);
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose a highest-ability character", characterFilter) {
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
                                                                                        new CancelCardOnTableEffect(action, finalEffect));
                                                                            }
                                                                            else {
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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.sameSiteAs(self, Filters.and(Filters.opponents(self), Filters.weapon)))
                && GameConditions.canSpot(game, self, 2, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setActionMsg("Cause an accident");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
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
                                            Collection<PhysicalCard> characters = Filters.filterActive(game, self,
                                                    Filters.and(Filters.opponents(self), Filters.character, Filters.participatingInBattle));
                                            int numberOfCharacters = characters.size();
                                            gameState.sendMessage("Number of characters: " + numberOfCharacters);

                                            if (totalDestiny < numberOfCharacters) {
                                                gameState.sendMessage("Result: Succeeded");
                                                String playerToChoose = modifiersQuerying.getPlayerToChooseCardTargetAtLocation(gameState, self, gameState.getBattleLocation(), opponent);
                                                action.appendEffect(
                                                        new TriggeringResultEffect(action,
                                                                new SenseAlterDestinySuccessfulResult(playerId)));
                                                action.appendEffect(
                                                        new ChooseCardToLoseFromTableEffect(action, playerToChoose, Filters.in(characters)));
                                            }
                                            else {
                                                gameState.sendMessage("Result: Failed");
                                            }
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}