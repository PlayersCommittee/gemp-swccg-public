package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractAlienRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtSameLocationAsCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien/Rebel
 * Title: Chewbacca
 */
public class Card2_003 extends AbstractAlienRebel {
    public Card2_003() {
        super(Side.LIGHT, 1, 4, 6, 2, 6, "Chewbacca", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R2);
        setLore("Wookiee smuggler from Kashyyyk. Over 200 years old. Top-notch mechanic and pilot. Jabba has large bounty on this 'walking carpet.' Friends call him Chewie...or Fuzzball.");
        setGameText("Power +1 at same location as Han. Adds 2 to power of anything he pilots. When piloting Falcon, also adds 1 to maneuver. Your vehicles, starships and droids at same site go to Used Pile (rather than Lost Pile) when they are 'hit.'");
        addPersona(Persona.CHEWIE);
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.WOOKIEE);
        setMatchingStarshipFilter(Filters.Falcon);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtSameLocationAsCondition(self, Filters.Han), 1));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), new PilotingCondition(self, Filters.Falcon), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final TargetingReason targetingReason = TargetingReason.TO_BE_USED_INSTEAD_OF_LOST;
        final Filter hitAtSameSite = Filters.and(Filters.your(self), Filters.or(Filters.vehicle, Filters.starship, Filters.droid),
                Filters.hit, Filters.atSameSite(self));

        // Check condition(s) - about to be lost (not an all-cards situation)
        if (TriggerConditions.isAboutToBeLost(game, effectResult, hitAtSameSite)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardToBeLost();
            if (GameConditions.canTarget(game, self, targetingReason, cardToBeLost)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setPerformingPlayer(playerId);
                action.setText("Place " + GameUtils.getFullName(cardToBeLost) + " in Used Pile");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target card to place in Used Pile instead of Lost Pile", targetingReason, cardToBeLost) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s) - e.g. Turn It Off! Turn It Off!
                                action.allowResponses("Place " + GameUtils.getCardLink(cardTargeted) + " in Used Pile",
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                // Perform result(s)
                                                result.getPreventableCardEffect().preventEffectOnCard(finalTarget);
                                                action.appendEffect(
                                                        new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }

        // Check condition(s) - about to be forfeited to Lost Pile
        if (TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, hitAtSameSite)) {
            final AboutToForfeitCardFromTableResult result = (AboutToForfeitCardFromTableResult) effectResult;
            final PhysicalCard cardToBeForfeited = result.getCardToBeForfeited();
            if (GameConditions.canTarget(game, self, targetingReason, cardToBeForfeited)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setPerformingPlayer(playerId);
                action.setText("Place " + GameUtils.getFullName(cardToBeForfeited) + " in Used Pile");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target card to place in Used Pile instead of Lost Pile", targetingReason, cardToBeForfeited) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s) - e.g. Turn It Off! Turn It Off!
                                action.allowResponses("Place " + GameUtils.getCardLink(cardTargeted) + " in Used Pile when forfeited",
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                result.getForfeitCardEffect().setForfeitToUsedPile();
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
}
