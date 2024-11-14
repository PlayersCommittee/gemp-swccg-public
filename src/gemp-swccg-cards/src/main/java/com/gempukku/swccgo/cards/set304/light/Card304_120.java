package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Set: The Great Hutt Expansion
 * Type: Interrupt
 * Subtype: Used
 * Title: Training Failure & Gambling Addiction
 */
public class Card304_120 extends AbstractUsedInterrupt {
    public Card304_120() {
        super(Side.LIGHT, 4, "Training Failure & Gambling Addiction", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        addComboCardTitles("Training Failure", "Gambling Addiction");
        setGameText("Cancel Lightsaber Combat Training. [Immune to Sense] OR If opponent just retrieved Force, opponent must lose X Force, where X = one-half the number of cards retrieved (round down) to a minimum of 1. OR If opponent's character just lost, lose 1 Force to retrieve the topmost character of ability < 6 (except a [Maintenance] or [Permanent Weapon] card) in your Lost Pile into hand.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Lightsaber_Combat_Training)
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