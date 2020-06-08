package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Character
 * Subtype: Alien
 * Title: Dengar (V)
 */
public class Card205_023 extends AbstractAlien {
    public Card205_023() {
        super(Side.DARK, 1, 3, 3, 2, 5, "Dengar", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Corellian bounty hunter. Assassin trained by the Empire. Has reflex-enhancing cyber-implants. Gravely injured during a swoop race in the crystal swamp of Agrilat. Blames Han Solo.");
        setGameText("[Pilot] 2. Characters about to be 'hit' by Dengar may be captured instead. If Dengar is about to be 'hit' while escorting a captive, captive is hit instead. If Dengar just seized a Rebel or a smuggler, opponent loses 1 Force (2 if Han).");
        addPersona(Persona.DENGAR);
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_5);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isAboutToBeHitBy(game, effectResult, Filters.and(Filters.character, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_CAPTURED)), self)) {
            AboutToBeHitResult aboutToBeHitResult = (AboutToBeHitResult) effectResult;
            PhysicalCard cardToBeHit = aboutToBeHitResult.getCardToBeHit();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Capture " + GameUtils.getFullName(cardToBeHit) + " instead");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", SpotOverride.INCLUDE_ALL, TargetingReason.TO_BE_CAPTURED, cardToBeHit) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Allow response(s)
                            action.allowResponses("Capture " + GameUtils.getCardLink(cardTargeted) + " instead",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalCharacter = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            ((AboutToBeHitResult) effectResult).getPreventableCardEffect().preventEffectOnCard(finalCharacter);
                                            action.appendEffect(
                                                    new CaptureCharacterOnTableEffect(action, finalCharacter));
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isAboutToBeHit(game, effectResult, self)) {
            Collection<PhysicalCard> validCaptives = Filters.filter(game.getGameState().getCaptivesOfEscort(self), game, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_HIT));
            if (!validCaptives.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make captive 'hit' instead");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose captive", SpotOverride.INCLUDE_CAPTIVE, TargetingReason.TO_BE_HIT, Filters.in(validCaptives)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s)
                                action.allowResponses("Make " + GameUtils.getCardLink(cardTargeted) + " 'hit' instead",
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard captiveToHit = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                ((AboutToBeHitResult) effectResult).getPreventableCardEffect().preventEffectOnCard(self);
                                                action.appendEffect(
                                                        new HitCardEffect(action, captiveToHit, self));
                                            }
                                        });
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (TriggerConditions.seizedBy(game, effectResult, Filters.or(Filters.Rebel, Filters.smuggler), self)) {
            int numForce = Filters.Han.accepts(game, ((CaptureCharacterResult) effectResult).getCapturedCard()) ? 2 : 1;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose " + numForce + " Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, numForce));
            return Collections.singletonList(action);
        }
        return null;
    }
}
