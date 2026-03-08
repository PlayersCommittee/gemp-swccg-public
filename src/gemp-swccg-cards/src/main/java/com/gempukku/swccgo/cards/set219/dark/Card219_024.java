package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceAndStackFaceUpEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.SuspendCardUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Interrupt
 * Subtype: Used
 * Title: Understand Art, Understand A Species
 */
public class Card219_024 extends AbstractUsedInterrupt {
    public Card219_024() {
        super(Side.DARK, 4, "Understand Art, Understand A Species", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setLore("The Empire considers alien species to be inferior.");
        setGameText("If an Imperial leader just won a battle, opponent loses 1 Force (and stacks it on Thrawn's Art Collection if possible). OR Suspend Ancient Watering Hole for remainder of turn. OR If you have 4 artwork cards, you retrieve 1 Force and opponent loses 1 Force.");
        addIcons(Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (TriggerConditions.wonBattle(game, effectResult, Filters.Imperial_leader)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);

            // The rest of the action depends on whether Thrawn's Art Collection is on table
            final PhysicalCard thrawnsArtCollection = Filters.findFirstActive(game, self, Filters.Thrawns_Art_Collection);

            if (thrawnsArtCollection != null) {
                action.setText("Make opponent lose (and stack) 1 Force");
                // Allow response(s)
                action.allowResponses("Make opponent lose (and stack) 1 Force",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new LoseForceAndStackFaceUpEffect(action, opponent, 1, thrawnsArtCollection));
                            }
                        }
                );
            } else {
                action.setText("Make opponent lose 1 Force");
                // Allow response(s)
                action.allowResponses("Make opponent lose 1 Force",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new LoseForceEffect(action, opponent, 1));
                            }
                        }
                );
            }
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        Filter suspendFilter = Filters.Ancient_Watering_Hole;
        TargetingReason suspendedTargetingReason = TargetingReason.TO_BE_SUSPENDED;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, suspendedTargetingReason, suspendFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Suspend Ancient Watering Hole");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Ancient Watering Hole", suspendFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Suspend " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new SuspendCardUntilEndOfTurnEffect(action, targetedCard));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Thrawns_Art_Collection, Filters.hasStacked(4, Filters.any)))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Retrieve 1 Force; opponent loses 1 Force");
            // Allow response(s)
            action.allowResponses("Retrieve 1 Force and opponent loses 1 Force",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveForceEffect(action, playerId, 1));
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, 1));
                        }
                    }
            );
            actions.add(action);
        }        
        return actions;
    }
}
