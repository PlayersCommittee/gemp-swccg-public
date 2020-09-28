package com.gempukku.swccgo.cards.set209.light;

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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.*;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 9
 * Type: Interrupt
 * Subtype: Used
 * Title: Odin Nesloor & First Aid
 */
public class Card209_021 extends AbstractUsedInterrupt {
    public Card209_021() {
        super(Side.LIGHT, 4, "Odin Nesloor & First Aid", Uniqueness.UNIQUE);
        addComboCardTitles(Title.Odin_Nesloor, Title.First_Aid);
        setGameText("If your character is about to be hit, use 1 Force (free if by a [Permanent Weapon] weapon) to prevent its forfeit from being reduced (and character is immune to Dr. Evazan) for remainder of turn. OR Cancel Disarmed. [Immune to Sense.] OR Cancel a 'react.' OR During your move phase, target any or all of your characters at one site to 'transport' (relocate) to an exterior or battleground site. Draw destiny. Use that much Force to 'transport,' or place Interrupt in Lost Pile.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_9, Icon.CORUSCANT);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // In this section, we:
        //    -   Cancel reacts before they resolve.
        //    -   Cancel Disarmed before it resolves.

        // Part 1: Cancel react (playable when opponent reacts)
        if (TriggerConditions.isReact(game, effect)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel 'react'");
            // Allow responses
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform results
                            action.appendEffect(new CancelReactEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        // Part 2: Cancel Disarmed as it's being played, before it resolves.
        Filter disarmedFilter = Filters.title("Disarmed");
        if (TriggerConditions.isPlayingCard(game, effect, disarmedFilter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final Filter charactersWhoAreDisarmed = Filters.Disarmed;

        // Part 2 of the disarmed canceler: Cancel Disarmed While it is already on table, as a top level action.
        if (GameConditions.canTarget(game, self, charactersWhoAreDisarmed)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Disarmed On This Character");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Disarmed Character", charactersWhoAreDisarmed) {

                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses("Restore " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(new RearmCharacterEffect(action, targetedCard));
                                        }
                                    });
                        }
                    }
            );
            action.setImmuneTo(Title.Sense);

            actions.add(action);
        }

        // Move Phase Nabrun, copied from Lana Dobreed, (playable as a top level action during move phase)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)) {
            final Filter exteriorOrBGSites = Filters.or(Filters.exterior_site, Filters.battleground_site);
            Collection<PhysicalCard> fromSites = Filters.filterTopLocationsOnTable(game,
                    Filters.and(Filters.site, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.character, Filters.atLocation(Filters.any), Filters.canBeRelocatedToLocation(exteriorOrBGSites, true, 0)))));

            if (!fromSites.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("'Transport' characters");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose site to 'transport' from", Filters.in(fromSites)) {
                            @Override
                            protected void cardSelected(final PhysicalCard fromSite) {
                                final Collection<PhysicalCard> charactersToRelocate = Filters.filterActive(game, self,
                                        Filters.and(Filters.your(self), Filters.character, Filters.atLocation(fromSite), Filters.canBeRelocatedToLocation(exteriorOrBGSites, true, 0)));
                                Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game, Filters.and(exteriorOrBGSites, Filters.not(fromSite)));
                                Collection<PhysicalCard> validSites = new LinkedList<PhysicalCard>();
                                // Figure out which sites any of the cards can be relocated to
                                for (PhysicalCard otherSite : otherSites) {
                                    for (PhysicalCard characterToRelocate : charactersToRelocate) {
                                        if (Filters.canBeRelocatedToLocation(otherSite, true, 0).accepts(game, characterToRelocate)) {
                                            validSites.add(otherSite);
                                            break;
                                        }
                                    }
                                }
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose site to 'transport' to", Filters.in(validSites)) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard toSite) {
                                                Collection<PhysicalCard> validCharactersToRelocate = new LinkedList<PhysicalCard>();
                                                // Figure out which characters can be relocated to the other site
                                                for (PhysicalCard characterToRelocate : charactersToRelocate) {
                                                    if (Filters.canBeRelocatedToLocation(toSite, true, 0).accepts(game, characterToRelocate)) {
                                                        validCharactersToRelocate.add(characterToRelocate);
                                                    }
                                                }
                                                action.appendTargeting(
                                                        new TargetCardsOnTableEffect(action, playerId, "Choose characters to 'transport'", 1, Integer.MAX_VALUE, Filters.in(validCharactersToRelocate)) {
                                                            @Override
                                                            protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> cardsToRelocate) {
                                                                action.addAnimationGroup(cardsToRelocate);
                                                                action.addAnimationGroup(toSite);

                                                                // At this point, mark the card as 'played'. If the subsequent costs
                                                                // fail, we need to make sure we still can't play the card again this turn.
                                                                // (by default, gemp records cards played only after all costs have been successfully paid)
                                                                action.appendCost(new RecordCardsBeingPlayedEffect(action, Collections.singleton(self)));

                                                                // Pay cost(s)
                                                                // Draw destiny to determine cost to 'transport'
                                                                action.appendCost(
                                                                        new DrawDestinyEffect(action, playerId) {
                                                                            @Override
                                                                            protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                                final GameState gameState = game.getGameState();
                                                                                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                                                if (totalDestiny == null) {
                                                                                    gameState.sendMessage("Result: 'Transport' cost not paid due to failed destiny draw");
                                                                                    action.appendCost(
                                                                                            new PutCardInVoidEffect(action, self));
                                                                                    action.appendCost(
                                                                                            new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                    action.appendCost(
                                                                                            new FailCostEffect(action));
                                                                                    return;
                                                                                }

                                                                                final float moveCost = modifiersQuerying.getRelocateBetweenLocationsCost(gameState, cardsToRelocate, fromSite, toSite, totalDestiny);
                                                                                gameState.sendMessage("'Transport' cost: " + GuiUtils.formatAsString(moveCost));

                                                                                if (!GameConditions.canUseForce(game, playerId, moveCost)) {

                                                                                    gameState.sendMessage("Result: Player unable to pay 'transport' cost");
                                                                                    action.appendCost(
                                                                                            new PutCardInVoidEffect(action, self));
                                                                                    action.appendCost(
                                                                                            new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                    action.appendCost(
                                                                                            new FailCostEffect(action));
                                                                                    return;
                                                                                }

                                                                                gameState.sendMessage("Result: Player may choose to 'transport'");
                                                                                // Ask player to use Force for transport or Interrupt is lost
                                                                                action.appendCost(
                                                                                        new PlayoutDecisionEffect(action, playerId,
                                                                                                new YesNoDecision((moveCost > 0) ? "Do you want to use " + GuiUtils.formatAsString(moveCost) + " Force to 'transport'?" : "Do you want to 'transport'?") {
                                                                                                    @Override
                                                                                                    protected void yes() {
                                                                                                        gameState.sendMessage(playerId + " chooses to 'transport' using " + GameUtils.getCardLink(self));
                                                                                                        if (moveCost > 0) {
                                                                                                            action.appendCost(
                                                                                                                    new UseForceEffect(action, playerId, moveCost));
                                                                                                        }
                                                                                                        // Allow response(s)
                                                                                                        action.allowResponses("'Transport' " + GameUtils.getAppendedNames(cardsToRelocate) + " to " + GameUtils.getCardLink(toSite),
                                                                                                                new RespondablePlayCardEffect(action) {
                                                                                                                    @Override
                                                                                                                    protected void performActionResults(Action targetingAction) {
                                                                                                                        // Get the targeted card(s) from the action using the targetGroupId.
                                                                                                                        // This needs to be done in case the target(s) were changed during the responses.
                                                                                                                        Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId);

                                                                                                                        // Perform result(s)
                                                                                                                        action.appendEffect(
                                                                                                                                new RelocateBetweenLocationsEffect(action, finalCharacters, toSite));
                                                                                                                    }
                                                                                                                }
                                                                                                        );
                                                                                                    }

                                                                                                    @Override
                                                                                                    protected void no() {
                                                                                                        gameState.sendMessage(playerId + " chooses to not 'transport' using " + GameUtils.getCardLink(self));
                                                                                                        action.appendCost(
                                                                                                                new PutCardInVoidEffect(action, self));
                                                                                                        action.appendCost(
                                                                                                                new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                                        action.appendCost(
                                                                                                                new FailCostEffect(action));
                                                                                                    }
                                                                                                }
                                                                                        )
                                                                                );
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final Filter yourCharacter = Filters.and(Filters.character, Filters.your(self));

        //check conditions
        if (TriggerConditions.isAboutToBeHit(game, effectResult, yourCharacter)
                && GameConditions.canUseForce(game, playerId, TriggerConditions.isAboutToBeHitBy(game, effectResult, yourCharacter, Filters.character_with_permanent_character_weapon) ? 0 : 1)) {

            final PhysicalCard cardAboutToBeHit = ((AboutToBeHitResult) effectResult).getCardToBeHit();
            if (GameConditions.canTarget(game, self, cardAboutToBeHit)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);

                action.setText("Prevent Forfeit From Being Reduced And Make Immune To Dr. Evazan");
                action.setImmuneTo(Title.Sense);
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Target", cardAboutToBeHit) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);

                                action.appendCost(
                                        new UseForceEffect(action, playerId, TriggerConditions.isAboutToBeHitBy(game, effectResult, yourCharacter, Filters.character_with_permanent_character_weapon) ? 0 : 1));

                                // Allow response(s)
                                action.allowResponses("Prevent forfeit from being reduced and make immune to Dr. Evazan",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new MayNotHaveForfeitValueReducedModifier(self, finalTarget),
                                                                "Make " + GameUtils.getCardLink(finalTarget) + "'s forfeit not be able to be reduced until end of turn"));
                                                action.appendEffect(new ImmuneToUntilEndOfTurnEffect(action, finalTarget, Title.Dr_Evazan));
                                            }
                                        }
                                );
                            }

                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
