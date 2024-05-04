package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Lost
 * Title: Hutt Smooch
 */
public class Card6_155 extends AbstractLostInterrupt {
    public Card6_155() {
        super(Side.DARK, 2, Title.Hutt_Smooch, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("'We have powerful friends. You're gonna regret this.' 'Rota go ma namatota.'");
        setGameText("Capture one opponents undercover spy ('cover is broken'). OR If opponent just deployed a spy to a site where opponent has no presence or Force icons, return spy to hand. Any Force used to deploy the spy remains used, and spy may not deploy this turn.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.opponents(self), Filters.undercover_spy);
        TargetingReason targetingReason = TargetingReason.TO_BE_CAPTURED;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, targetingReason, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Capture undercover spy");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose undercover spy", SpotOverride.INCLUDE_UNDERCOVER, targetingReason, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Capture " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CaptureCharacterOnTableEffect(action, finalTarget));
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
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, opponent, Filters.and(Filters.spy, Filters.canBeTargetedBy(self)), Filters.site)) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            final PhysicalCard cardPlayed = playCardResult.getPlayedCard();
            if (playCardResult.isPlayedToSiteWithoutPresenceOrForceIcons()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Return " + GameUtils.getFullName(cardPlayed) + " to hand");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose spy", SpotOverride.INCLUDE_UNDERCOVER, cardPlayed) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Return " + GameUtils.getCardLink(cardPlayed) + " to hand",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ReturnCardToHandFromTableEffect(action, finalTarget));
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new MayNotDeployModifier(self, Filters.sameTitle(finalTarget), opponent), null));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}