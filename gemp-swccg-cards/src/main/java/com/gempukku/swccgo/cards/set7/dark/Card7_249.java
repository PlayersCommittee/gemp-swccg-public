package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Lost
 * Title: Counter Surprise Assault
 */
public class Card7_249 extends AbstractLostInterrupt {
    public Card7_249() {
        super(Side.DARK, 2, "Counter Surprise Assault");
        setLore("'He certainly has courage.' 'What good will it do us if he gets himself killed?'");
        setGameText("Cancel Surprise Assault. OR Use 4 Force to cancel opponent's Force drain at a mobile site. Deploy (for free) from Reserve Deck any number of non-unique troopers to that site. (Light Side characters, vehicles and starships here may 'escape' to Used Pile).");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Surprise_Assault)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Surprise_Assault)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Surprise_Assault, Title.Surprise_Assault);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.mobile_site)
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 4)) {
            final PhysicalCard forceDrainLocation = game.getGameState().getForceDrainLocation();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Force drain");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 4));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelForceDrainEffect(action));
                            action.appendEffect(
                                    new DeployCardsToLocationFromReserveDeckEffect(action, Filters.and(Filters.non_unique, Filters.trooper), 0, Integer.MAX_VALUE, forceDrainLocation, true, true) {
                                        public String getChoiceText(int numCardsToChoose) {
                                            return "Choose non-unique troopers to deploy from Reserve Deck";
                                        }

                                    });
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(final SwccgGame game) {
                                            final Collection<PhysicalCard> cardsToEscape = Filters.filterActive(game, self,
                                                    Filters.and(Filters.owner(opponent), Filters.or(Filters.character, Filters.vehicle, Filters.starship), Filters.here(forceDrainLocation)));
                                            if (!cardsToEscape.isEmpty()) {
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, opponent,
                                                                new YesNoDecision("Do you want your characters, vehicles, and starships to 'escape'?") {
                                                                    @Override
                                                                    protected void yes() {
                                                                        game.getGameState().sendMessage(opponent + " chooses for characters, vehicles, and starships to 'escape' to Used Pile");
                                                                        action.appendEffect(
                                                                                new PlaceCardsInUsedPileFromTableEffect(action, opponent, cardsToEscape));
                                                                    }

                                                                    @Override
                                                                    protected void no() {
                                                                        game.getGameState().sendMessage(opponent + " chooses for characters, vehicles, and starships to not 'escape' to Used Pile");
                                                                    }
                                                                }));
                                            }
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
}