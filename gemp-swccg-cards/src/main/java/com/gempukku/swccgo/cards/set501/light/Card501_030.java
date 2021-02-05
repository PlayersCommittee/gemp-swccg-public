package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.BreakCoverEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Quite A Mercenary (V) Errata
 */
public class Card501_030 extends AbstractUsedOrLostInterrupt {
    public Card501_030() {
        super(Side.LIGHT, 5, Title.Quite_A_Mercenary, Uniqueness.UNIQUE);
        setLore("Smugglers and other rogues frequent spaceports along trade routes. 'Your friend is quite a mercenary. I wonder if he really cares about anything, or anybody.'");
        setGameText("USED: Cancel Elis Helrot or Stunning Leader. [Immune to Sense.] OR Cancel a smuggler's game text for remainder of turn. OR [upload] [Reflections II] Chewie or Mercenary Armor. LOST: During your move phase, “break cover” of an Undercover spy.");
        addIcons(Icon.A_NEW_HOPE);
        setVirtualSuffix(true);
        setTestingText("Quite A Mercenary (V) Errata");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // USED: Cancel Elis Helrot or Stunning Leader. [Immune to Sense.]

        // Check condition(s)
        if ((TriggerConditions.isPlayingCard(game, effect, Filters.Elis_Helrot)
                || TriggerConditions.isPlayingCard(game, effect, Filters.Stunning_Leader))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Sense);

            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // USED: Cancel Elis Helrot. [Immune to Sense.]
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Elis_Helrot)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Elis_Helrot, Title.Elis_Helrot);
            actions.add(action);
        }

        if (GameConditions.canTargetToCancel(game, self, Filters.Stunning_Leader)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Stunning_Leader, Title.Stunning_Leader);
            actions.add(action);
        }


        // USED:  Cancel a smuggler's game text for remainder of turn.
        Filter smugglerFilter = Filters.smuggler;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, smugglerFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel smuggler's game text");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose smuggler", smugglerFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }


        // USED: Take [Reflections II] Chewie or Mercenary Armor into hand from Reserve Deck; reshuffle.
        GameTextActionId gameTextActionId = GameTextActionId.QUITE_A_MERCENARY__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter ref2Chewie = Filters.and(Persona.CHEWIE, Icon.REFLECTIONS_II);

            final Filter uploadTargets = Filters.or(ref2Chewie, Filters.title(Title.Mercenary_Armor));

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a [RefII] Chewie or Mercenary Armor into hand.",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, uploadTargets, true));
                        }
                    }
            );
            actions.add(action);
        }

        // LOST: During your move phase, "break cover" of an Undercover spy.

        // NOTE: We have a pending question out to D&D as to whether this is ANY Undercover spy, or just opponents
        //       Leaving this line in in case we need to swap it out
        // Filter targetFilter = Filters.and(Filters.opponents(self), Filters.undercover_spy);
        Filter targetFilter = Filters.undercover_spy;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE) &&
                GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Break a spy's cover");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose undercover spy", SpotOverride.INCLUDE_UNDERCOVER, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);

                            // Allow response(s)
                            action.allowResponses("'Break cover' of " + GameUtils.getCardLink(cardTargeted),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalSpy = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new BreakCoverEffect(action, finalSpy));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

}