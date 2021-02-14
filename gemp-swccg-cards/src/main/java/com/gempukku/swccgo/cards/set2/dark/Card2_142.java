package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.*;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: We Have A Prisoner
 */
public class Card2_142 extends AbstractLostInterrupt {
    public Card2_142() {
        super(Side.DARK, 3, Title.We_Have_A_Prisoner);
        setLore("'You are part of the Rebel Alliance, and a traitor. Take her away!'");
        setGameText("Use 1 Force if opponent's character is about to be lost or forfeited from battle. It is captured instead (character is first restored to normal). OR Use X Force to capture all characters on board a captured starship, where X = twice the number of characters.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        TargetingReason targetingReason = TargetingReason.TO_BE_CAPTURED;
        Filter opponentsCharacterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.participatingInBattle, Filters.canBeTargetedBy(self, targetingReason));

        // Check condition(s)
        if (TriggerConditions.isAboutToBeLost(game, effectResult, opponentsCharacterFilter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardToBeLost();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Capture " + GameUtils.getFullName(cardToBeLost));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, cardToBeLost) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Capture " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            result.getPreventableCardEffect().preventEffectOnCard(finalTarget);
                                            action.appendEffect(
                                                    new RestoreCardToNormalEffect(action, finalTarget));
                                            action.appendEffect(
                                                    new CaptureCharacterOnTableEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (TriggerConditions.isAboutToBeForfeited(game, effectResult, opponentsCharacterFilter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final AboutToForfeitCardFromTableResult result = (AboutToForfeitCardFromTableResult) effectResult;
            final PhysicalCard cardToBeForfeited = result.getCardToBeForfeited();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Capture " + GameUtils.getFullName(cardToBeForfeited));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, cardToBeForfeited) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Capture " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            result.getForfeitCardEffect().preventEffectOnCard(finalTarget);
                                            action.appendEffect(
                                                    new RestoreCardToNormalEffect(action, finalTarget));
                                            action.appendEffect(
                                                    new CaptureCharacterOnTableEffect(action, finalTarget));
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
        //Use X Force to capture all characters on board a captured starship, where X = twice the number of characters.
        if (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.captured_starship, Filters.hasAboardExceptRelatedSites(self, SpotOverride.INCLUDE_CAPTIVE, Filters.character)))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);

            action.setText("Capture characters on a captured starship");
            action.setActionMsg("Capture all characters on board a captured starship");

            Collection<PhysicalCard> possibleStarships = Filters.filterActive(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.captured_starship, Filters.hasAboardExceptRelatedSites(self, SpotOverride.INCLUDE_CAPTIVE, Filters.character)));
            Collection<PhysicalCard> haveEnoughForce = new HashSet<PhysicalCard>();
            for(PhysicalCard starship: possibleStarships) {
                int charactersAboard = Filters.countAllOnTable(game, Filters.and(Filters.character, Filters.aboard(starship)));
                if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2*charactersAboard)) {
                    haveEnoughForce.add(starship);
                }
            }

            TargetingReason targetingReason = TargetingReason.TO_BE_CAPTURED;

            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose a captured starship", SpotOverride.INCLUDE_CAPTIVE, targetingReason, Filters.in(haveEnoughForce)) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    int forceToUse = 2*Filters.countAllOnTable(game, Filters.and(Filters.character, Filters.aboard(targetedCard)));
                    if (!game.getModifiersQuerying().isInterruptPlayForFree(game.getGameState(), self))
                        action.appendCost(new UseForceEffect(action, playerId, forceToUse));

                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard starship = targetingAction.getPrimaryTargetCard(targetGroupId);
                            Collection<PhysicalCard> charactersToCapture = Filters.filterAllOnTable(game, Filters.and(Filters.aboard(starship)));

                            action.appendEffect(new CaptureCharactersOnTableEffect(action, charactersToCapture));
                        }
                    });
                }
            });

            if (haveEnoughForce.size()>0)
                return Collections.singletonList(action);
        }

        return null;
    }
}