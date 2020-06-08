package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: Ghhhk
 */
public class Card2_132 extends AbstractLostInterrupt {
    public Card2_132() {
        super(Side.DARK, 1, Title.Ghhhk);
        setLore("Dejarik of creature from Clak'dor VII. Ghhhk rise with the dawn, screeching their mating calls across the jungle. Locals use their skin oils as a healing salve.");
        setGameText("During the damage segment of a battle you lost, if you have no cards left that can be forfeited, cancel all remaining battle damage. (Immune to Sense.) OR Cancel Nightfall.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.DEJARIK);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isDuringBattleLostBy(game, playerId)
                && GameConditions.isBattleDamageRemaining(game, playerId)
                && !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.participatingInBattle, Filters.mayBeForfeited))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel all remaining battle damage");
            action.setImmuneTo(Title.Sense);
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new SatisfyAllBattleDamageEffect(action, playerId));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Nightfall)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Nightfall)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Nightfall, Title.Nightfall);
            return Collections.singletonList(action);
        }
        return null;
    }
}