package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
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
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Interrupt
 * Subtype: Used
 * Title: ComScan Detection (V)
 */
public class Card221_013 extends AbstractUsedInterrupt {
    public Card221_013() {
        super(Side.DARK, 4, "ComScan Detection", Uniqueness.UNRESTRICTED, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Imperial Navy boasts the best communications network in the galaxy. Sophisticated control technology allows the Empire to dispatch armed forces without delay.");
        setGameText("During your turn, target opponent's spy at a site you control; target is lost. (Immune to Droid Shutdown and Sense.) OR Cancel Dodge, I Think I Can Handle Myself, or a Force drain initiated by a lone Falcon. OR Target a starship weapon; it may not fire this turn.");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter filter = Filters.and(Filters.at(Filters.and(Filters.controls(playerId), Filters.site)), Filters.opponents(playerId),
                Filters.spy, Filters.canBeTargetedBy(self, targetingReason));

        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make spy lost");
            action.setImmuneTo(Title.Droid_Shutdown);
            action.setImmuneTo(Title.Sense);

            // Allow response(s)
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target spy to be lost", SpotOverride.INCLUDE_UNDERCOVER, targetingReason, filter) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(null,
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    PhysicalCard finalCard = action.getPrimaryTargetCard(targetGroupId);
                                    action.appendEffect(
                                            new LoseCardFromTableEffect(action, finalCard));
                                }
                            }
                    );
                }
            });
            actions.add(action);
        }

        if (GameConditions.canTarget(game, self, Filters.starship_weapon)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Prevent a starship weapon from firing");

            // Allow response(s)
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a starship weapon", Filters.starship_weapon) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(null,
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    PhysicalCard finalCard = action.getPrimaryTargetCard(targetGroupId);
                                    action.appendEffect(
                                            new AddUntilEndOfTurnModifierEffect(action, new MayNotBeFiredModifier(self, finalCard), "Prevents "+ GameUtils.getCardLink(finalCard) + " from firing"));
                                }
                            }
                    );
                }
            });
            actions.add(action);
        }


        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Dodge)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Dodge, Title.Dodge);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.title("I Think I Can Handle Myself"))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title("I Think I Can Handle Myself"), "I Think I Can Handle Myself");
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Dodge, Filters.title("I Think I Can Handle Myself")))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }


        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiated(game, effectResult)
                && GameConditions.canCancelForceDrain(game, self)) {

            // the Falcon needs to be owned by the force draining player but we can't assume it is owned by the opponent
            String forceDrainingPlayer = game.getGameState().getForceDrainState().getPlayerId();

            if (forceDrainingPlayer != null
                    && TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.sameLocationAs(self, Filters.and(Filters.alone, Filters.Falcon, Filters.owner(forceDrainingPlayer))))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Cancel Force drain");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new CancelForceDrainEffect(action));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}