package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealOpponentsHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.DisableScompLinkModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Shocking Information & Grimtaash
 */
public class Card10_022 extends AbstractUsedOrLostInterrupt {
    public Card10_022() {
        super(Side.LIGHT, 2, "Shocking Information & Grimtaash", Uniqueness.UNIQUE);
        addComboCardTitles(Title.Shocking_Information, Title.Grimtaash);
        setGameText("USED: Target a location. Scomp links there cannot be used for remainder of turn. OR If opponent is about to scan or otherwise look through your hand (unless using Monnok), opponent continues but must lose 4 Force plus card allowing scan. OR If opponent has 13 or more cards in hand, place all but 8 (random selection) in Used Pile. LOST: Use 4 Force to reveal opponent's hand. All cards opponent has two or more of in hand are lost.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        Filter locationFilter = Filters.and(Filters.location, Filters.has_Scomp_link);

        if (GameConditions.canTarget(game, self, locationFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Disable scomp links");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose location", locationFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard location) {
                            action.addAnimationGroup(location);
                            // Allow response(s)
                            action.allowResponses("Disable scomp links at " + GameUtils.getCardLink(location),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalLocation = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new DisableScompLinkModifier(self, Filters.sameLocationId(finalLocation)),
                                                            "Disables scomp links at " + GameUtils.getCardLink(finalLocation)));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.numCardsInHand(game, opponent) >= 13) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place random cards in Used Pile");
            // Allow response(s)
            action.allowResponses("Place random cards from opponent's hand in Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutRandomCardsFromHandOnUsedPileEffect(action, playerId, opponent, 8));
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.hasHand(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 4)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Reveal opponent's hand");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 4));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RevealOpponentsHandEffect(action, playerId) {
                                        @Override
                                        protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                                            action.appendEffect(
                                                    new LoseCardsFromHandEffect(action, opponent, Filters.and(Filters.duplicatesOfInHand(opponent), Filters.canBeTargetedBy(self))));
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isAboutToLookAtOpponentsHand(game, effect, opponent, Filters.not(Filters.Monnok))) {
            LookAtCardsInOpponentsHandEffect sourceEffect = (LookAtCardsInOpponentsHandEffect) effect;
            final Action sourceAction = sourceEffect.getAction();
            final PhysicalCard sourceCard = sourceEffect.getCardAllowingScan();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Make opponent lose Force and card");
            action.addAnimationGroup(sourceCard);
            // Allow response(s)
            action.allowResponses("Make opponent lose 4 Force and " + GameUtils.getCardLink(sourceCard),
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            // Add these Effects to the source action, so they occur after the other Effects of that action.
                            sourceAction.appendAfterEffect(
                                    new LoseCardsFromOffTableSimultaneouslyEffect(sourceAction, Collections.singleton(sourceCard), false));
                            sourceAction.appendAfterEffect(
                                    new LoseCardsFromTableSimultaneouslyEffect(sourceAction, Collections.singleton(sourceCard), false, false));
                            sourceAction.appendAfterEffect(
                                    new LoseForceEffect(sourceAction, opponent, 4));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}