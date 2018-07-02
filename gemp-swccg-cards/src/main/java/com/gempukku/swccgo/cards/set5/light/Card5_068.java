package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Shocking Information
 */
public class Card5_068 extends AbstractUsedInterrupt {
    public Card5_068() {
        super(Side.LIGHT, 5, Title.Shocking_Information, Uniqueness.UNIQUE);
        setLore("'R2-D2, you know better than to trust a strange computer.'");
        setGameText("Target a location. Scomp links there cannot be used for remainder of turn. OR If opponent is about to scan or otherwise look through your hand (unless using Monnok), opponent continues but must lose 4 Force plus the card allowing the scan.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter locationFilter = Filters.and(Filters.location, Filters.has_Scomp_link);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, locationFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
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
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isAboutToLookAtOpponentsHand(game, effect, opponent, Filters.not(Filters.Monnok))) {
            LookAtCardsInOpponentsHandEffect sourceEffect = (LookAtCardsInOpponentsHandEffect) effect;
            final Action sourceAction = sourceEffect.getAction();
            final PhysicalCard sourceCard = sourceEffect.getCardAllowingScan();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
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