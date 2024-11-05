package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromOutsideTheGameEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used
 * Title: Rite Of Passage (V)
 */

public class Card223_022 extends AbstractUsedInterrupt {
    public Card223_022() {
        super(Side.DARK, 4, Title.Rite_Of_Passage, Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("There are many different paths to becoming a Jedi, each with its own risks and consequences. A student must choose wisely.");
        setGameText("Once per game, deploy a Lift Tube from outside your deck. [Immune to Sense.] OR [Upload] [Set 0] Imperial Decree. OR Cancel Path Of Least Resistance or Run Luke, Run! OR Cancel opponent's Mos Eisley or Upper Plaza Corridor site game text for remainder of turn.");
        setVirtualSuffix(true);
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_23);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        Filter filter = Filters.or(Filters.Mos_Eisley, Filters.Upper_Plaza_Corridor);

        if (GameConditions.canSpot(game, self, filter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel opponent's site game text.");
            action.setActionMsg("Cancel opponent's Mos Eisley or Upper Plaza Corridor game text for remainder of turn.");
            // Choose target(s)
            action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose site", filter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        // Allow response(s)
                        action.allowResponses("Cancel opponent's " + GameUtils.getCardLink(targetedCard) + " game text for remainder of turn.",
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                        final String opponent = game.getOpponent(playerId);

                                        // Perform result(s)
                                        action.appendEffect(
                                            new AddUntilEndOfTurnModifierEffect(action, 
                                                new CancelsGameTextOnSideOfLocationModifier(self, finalTarget, opponent), 
                                            "Game text canceled."));
                                    }
                                }
                        );
                    }
                }
            );
            actions.add(action);

        }

        GameTextActionId gameTextActionId = GameTextActionId.RITE_OF_PASSAGE__DOWNLOAD_LIFT_TUBE;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setImmuneTo(Title.Sense);
            action.setText("Deploy Lift Tube from outside your deck.");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.allowResponses("deploy a Lift Tube from outside your deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                new DeployCardFromOutsideTheGameEffect(action, Filters.Lift_Tube, 0));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId1 = GameTextActionId.RITE_OF_PASSAGE__DOWNLOAD_IMPERIAL_DECREE;

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId1)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId1);
            action.allowResponses("Take [Set 0] Imperial Decree into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.icon(Icon.VIRTUAL_SET_0), Filters.Imperial_Decree), true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.canTargetToCancel(game, self, Filters.Path_Of_Least_Resistance)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Path_Of_Least_Resistance, Title.Path_Of_Least_Resistance);
            actions.add(action);
        }
        
        if (GameConditions.canTargetToCancel(game, self, Filters.Run_Luke_Run)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Run_Luke_Run, Title.Run_Luke_Run);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Path_Of_Least_Resistance, Filters.Run_Luke_Run))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }
}
