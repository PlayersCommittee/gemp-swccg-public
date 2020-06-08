package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.RetrieveForceResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 0
 * Type: Interrupt
 * Subtype: Used
 * Title: Imbalance & Kintan Strider
 */
public class Card200_122 extends AbstractUsedInterrupt {
    public Card200_122() {
        super(Side.DARK, 4, "Imbalance & Kintan Strider", Uniqueness.UNIQUE);
        addComboCardTitles("Imbalance", "Kintan Strider");
        setGameText("Cancel Blaster Proficiency. [Immune to Sense] OR If opponent just retrieved Force, opponent must lose X Force, where X = one-half the number of cards retrieved (round down) to a minimum of 1. OR If opponent's character just lost, lose 1 Force to retrieve the topmost character of ability < 6 (except a [Maintenance] or [Permanent Weapon] card) in your Lost Pile into hand.");
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_0);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Blaster_Proficiency)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.setImmuneTo(Title.Sense);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justRetrievedForce(game, effectResult, opponent)) {
            final int numForceToLose = Math.max(1, ((RetrieveForceResult) effectResult).getAmountOfForceRetrieved() / 2);

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent lose " + numForceToLose + " Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, numForceToLose));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.KINTAN_STRIDER__RETRIEVE_TOPMOST_CHARACTER;

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.opponents(self), Filters.character))
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Regenerate top-most character");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardIntoHandEffect(action, playerId, true, Filters.and(Filters.character, Filters.abilityLessThan(6), Filters.except(Filters.or(Icon.MAINTENANCE, Icon.PERMANENT_WEAPON)))));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}