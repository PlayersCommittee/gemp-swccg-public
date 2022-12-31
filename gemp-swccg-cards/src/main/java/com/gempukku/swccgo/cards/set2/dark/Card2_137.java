package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.CaptureCharactersOnTableEffect;
import com.gempukku.swccgo.logic.effects.MayNotMoveUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;
import com.gempukku.swccgo.logic.timing.results.RelocatedBetweenLocationsResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used
 * Title: Oo-ta Goo-ta, Solo?
 */
public class Card2_137 extends AbstractUsedInterrupt {
    public Card2_137() {
        super(Side.DARK, 5, Title.Oota_Goota_Solo, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.C2);
        setLore("Greedo cheskopokuta klees ruya Solo. Hoko yanee boopa gush Cantina. Cheeco wa Solo's anye nyuma Greedo vakee. Jabba kul steeka et en anpaw.");
        setGameText("If Nabrun Leids just completed a transport, use 2 Force. Nabrun is lost and all Rebels transported are captured. (Immune to Sense.) OR Prevent a just-deployed smuggler from moving this turn. OR Cancel a Kessel Run.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.transportCompletedBy(game, effectResult, Filters.Nabrun_Leids)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {
            final PhysicalCard sourceCard = ((RelocatedBetweenLocationsResult) effectResult).getActionSource();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            action.setText("Make " + GameUtils.getFullName(sourceCard) + " lost and capture Rebels");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Allow response(s)
            action.allowResponses("Make " + GameUtils.getFullName(sourceCard) + " lost and capture all Rebels transported",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardFromVoidInLostPileEffect(action, playerId, sourceCard));
                            Collection<PhysicalCard> rebelsRelocated = Filters.filter(((RelocatedBetweenLocationsResult) effectResult).getMovedCards(), game, Filters.Rebel);
                            if (!rebelsRelocated.isEmpty()) {
                                action.appendEffect(
                                        new CaptureCharactersOnTableEffect(action, rebelsRelocated));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, Filters.and(Filters.smuggler, Filters.canBeTargetedBy(self)))) {
            PhysicalCard cardDeployed = ((PlayCardResult) effectResult).getPlayedCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Prevent " + GameUtils.getFullName(cardDeployed) + " from moving");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose smuggler", cardDeployed) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard smuggler) {
                            action.addAnimationGroup(smuggler);
                            // Allow response(s)
                            action.allowResponses("Prevent " + GameUtils.getCardLink(smuggler) + " from moving",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalSmuggler = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MayNotMoveUntilEndOfTurnEffect(action, finalSmuggler));
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
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        Filter filter = Filters.Kessel_Run;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Kessel_Run)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Kessel_Run, Title.Kessel_Run);
            return Collections.singletonList(action);
        }
        return null;
    }
}