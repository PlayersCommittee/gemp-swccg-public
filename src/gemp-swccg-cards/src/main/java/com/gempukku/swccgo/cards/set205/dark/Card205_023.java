package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.EscortingCaptiveCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;

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
        super(Side.DARK, 1, 3, 3, 2, 5, "Dengar", Uniqueness.UNIQUE, ExpansionSet.SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Corellian bounty hunter. Assassin trained by the Empire. Has reflex-enhancing cyber-implants. Gravely injured during a swoop race in the crystal swamp of Agrilat. Blames Han Solo.");
        setGameText("[Pilot] 2. Adds one battle destiny if escorting a captive (or piloting Punishing One). Characters about to be 'hit' by Dengar may be captured instead. Once per game, may take Bounty (or a blaster) into hand from Reserve deck; reshuffle.");
        addPersona(Persona.DENGAR);
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_5);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.ASSASSIN);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Punishing_One);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new OrCondition(new EscortingCaptiveCondition(self), new PilotingCondition(self, Filters.Punishing_One)), 1));
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
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DENGAR__UPLOAD_BOUNTY_OR_BLASTER;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Bounty (or a blaster) into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Bounty, Filters.blaster), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
