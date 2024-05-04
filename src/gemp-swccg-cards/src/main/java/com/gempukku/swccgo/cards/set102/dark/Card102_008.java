package com.gempukku.swccgo.cards.set102.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.MayNotMoveUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.MovingResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premium (Jedi Pack)
 * Type: Interrupt
 * Subtype: Lost
 * Title: Gravity Shadow
 */
public class Card102_008 extends AbstractLostInterrupt {
    public Card102_008() {
        super(Side.DARK, 4, Title.Gravity_Shadow, Uniqueness.UNIQUE, ExpansionSet.JEDI_PACK, Rarity.PM);
        setLore("'Traveling through hyperspace ain't like dustin' crops, boy!' Gravitational phenomena cast shadows in hyperspace, posing a serious threat to lightspeed navigation.");
        setGameText("If opponent's starship is about to move through hyperspace, target that starship and its highest-ability pilot. Draw destiny. If destiny > pilot's ability, starship may not move this turn. If destiny = pilot's ability, starship is lost.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter filter = Filters.and(Filters.opponents(self), Filters.starship, Filters.canBeTargetedBy(self, targetingReason));

        if (TriggerConditions.movingThroughHyperspace(game, effectResult, filter)) {
            final MovingResult movingResult = (MovingResult) effectResult;
            final PhysicalCard starship = movingResult.getCardMoving();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);

            final float highestAbilityPiloting = game.getModifiersQuerying().getHighestAbilityPiloting(game.getGameState(), starship, false, false);
            float highestAbilityCharacterPiloting = game.getModifiersQuerying().getHighestAbilityPiloting(game.getGameState(), starship, false, true);
            float highestAbilityPermanentPilotPiloting = game.getModifiersQuerying().getHighestAbilityPiloting(game.getGameState(), starship, true, false);

            Filter targetFilter;
            String text;

            if (highestAbilityCharacterPiloting == highestAbilityPermanentPilotPiloting) {
                targetFilter = Filters.or(starship, Filters.and(Filters.piloting(starship), Filters.abilityEqualTo(highestAbilityCharacterPiloting)));
                text = "starship (permanent pilot) or pilot character aboard";
            } else if (highestAbilityCharacterPiloting > highestAbilityPermanentPilotPiloting) {
                targetFilter = Filters.and(Filters.piloting(starship), Filters.abilityEqualTo(highestAbilityCharacterPiloting));
                text = "pilot character aboard";
            } else {
                targetFilter = Filters.sameCardId(starship);
                text = "starship (permanent pilot)";
            }

            action.setText("Target " + text);
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose " + text, targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);


                            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "", Filters.and(starship)) {
                                @Override
                                protected boolean getUseShortcut() {
                                    return true;
                                }

                                @Override
                                protected void cardTargeted(final int starshipTargetGroupId, PhysicalCard targetedStarship) {

                                    // Allow response(s)
                                    action.allowResponses("Draw destiny while targeting " + GameUtils.getCardLink(targetedCard),
                                            new

                                                    RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {

                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            if (Filters.character.accepts(game, finalTarget)) {
                                                                                return Collections.singletonList(finalTarget);
                                                                            }
                                                                            return null;
                                                                        }

                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            GameState gameState = game.getGameState();
                                                                            if (totalDestiny == null) {
                                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                                return;
                                                                            }

                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            gameState.sendMessage("Ability: " + GuiUtils.formatAsString(highestAbilityPiloting));

                                                                            if (totalDestiny > highestAbilityPiloting) {
                                                                                gameState.sendMessage("Result: Starship returns to original location");
                                                                                movingResult.getPreventableCardEffect().preventEffectOnCard(starship);
                                                                                action.appendEffect(
                                                                                        new MayNotMoveUntilEndOfTurnEffect(action, starship));
                                                                            } else if (totalDestiny == highestAbilityPiloting) {
                                                                                gameState.sendMessage("Result: Starship is lost");
                                                                                movingResult.getPreventableCardEffect().preventEffectOnCard(starship);
                                                                                action.appendEffect(
                                                                                        new LoseCardFromTableEffect(action, starship));
                                                                            } else {
                                                                                gameState.sendMessage("Result: No result");
                                                                            }
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    }
                                    );
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