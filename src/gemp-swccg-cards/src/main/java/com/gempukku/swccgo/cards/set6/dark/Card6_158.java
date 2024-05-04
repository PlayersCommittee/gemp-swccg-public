package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Torture
 */
public class Card6_158 extends AbstractUsedOrLostInterrupt {
    public Card6_158() {
        super(Side.DARK, 6, Title.Torture, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("'We have been without an interpreter since our master got angry with our last protocol droid and disintegrated him.'");
        setGameText("USED: Cancel Never Tell Me The Odds if it was just inserted or revealed. (Immune to Sense). LOST: Target a droid at Droid Workshop that you have captured or stolen. Droid is lost. Retrieve Force equal to droid's forfeit (doubled if stolen).");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        Filter filter = Filters.Never_Tell_Me_The_Odds;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Sense);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.justRevealedInsertCard(game, effectResult, Filters.Never_Tell_Me_The_Odds)
                && GameConditions.canCancelRevealedInsertCard(game, self, effectResult)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Sense);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelRevealedInsertCardAction(action, effectResult);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.droid, Filters.at(Filters.Droid_Workshop),
                Filters.or(Filters.and(Filters.opponents(self), Filters.captive), Filters.and(Filters.your(self), Filters.stolen)));
        Set<TargetingReason> targetingReasonSet = new HashSet<TargetingReason>();
        targetingReasonSet.add(TargetingReason.TO_BE_LOST);
        targetingReasonSet.add(TargetingReason.TO_BE_TORTURED);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, targetingReasonSet, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Torture droid");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose droid", SpotOverride.INCLUDE_CAPTIVE, targetingReasonSet, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Torture " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            float amountToRetrieve = game.getModifiersQuerying().getForfeit(game.getGameState(), finalTarget);
                                            if (Filters.stolen.accepts(game, finalTarget)) {
                                                amountToRetrieve *= 2;
                                            }
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, finalTarget));
                                            if (!Filters.mayContributeToForceRetrieval.accepts(game, finalTarget)) {
                                                action.appendEffect(
                                                        new SendMessageEffect(action, "Force retrieval not allowed due to including cards not allowed to contribute to Force retrieval"));
                                            }
                                            else {
                                                action.appendEffect(
                                                        new RetrieveForceEffect(action, playerId, amountToRetrieve));
                                            }
                                        }
                                    }
                            );
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}