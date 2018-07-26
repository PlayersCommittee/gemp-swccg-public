package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.StackedFromCardPileResult;
import com.gempukku.swccgo.logic.timing.results.StackedFromHandResult;
import com.gempukku.swccgo.logic.timing.results.StackedFromTableResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Effective Repairs & Starship Levitation
 */
public class Card209_019 extends AbstractUsedOrLostInterrupt {
    public Card209_019() {
        super(Side.LIGHT, 4, "Effective Repairs & Starship Levitation", Uniqueness.UNIQUE);
        setVirtualSuffix(false);
        setLore("");
        setGameText("USED: Cancel Broken Concentration, Lateral Damage, or Limited Resources. OR Place a card just stacked on Droid Racks or Strategic Reserves in opponent's Lost Pile. (Immune to Sense) OR If you just drew a starship for destiny, take that starship into hand to cancel and redraw that destiny. LOST: Use 3 Force to retrieve an Effect of any kind or a non-[Maintenance] starship into hand.");
        addIcons(Icon.VIRTUAL_SET_9);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();


        // Check condition(s) - Broken Concentration in play
        if (GameConditions.canTargetToCancel(game, self, Filters.Broken_Concentration)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Broken_Concentration, Title.Broken_Concentration);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }

        // Check condition(s) - Lateral Damage in play
        if (GameConditions.canTargetToCancel(game, self, Filters.Lateral_Damage)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Lateral_Damage, Title.Lateral_Damage);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }

        // Check condition(s)
        GameTextActionId gameTextActionId = GameTextActionId.EFFECTIVE_REPAIRS_AND_STARSHIP_LEVITATION__RETRIEVE_NON_MAINTENANCE_STARSHIP;
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Retrieve Effect or non-[M] starship");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardIntoHandEffect(action, playerId, Filters.or(Filters.Effect_of_any_Kind, Filters.and(Filters.starship, Filters.not(Icon.MAINTENANCE)))));
                        }
                    }
            );
            actions.add(action);
        };

        return actions;
    }




    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s) - If you just drew a starship for destiny, take that starship into hand to cancel and redraw that destiny.
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.starship)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Take destiny card into hand and cause re-draw");
            // Pay cost(s)
            action.appendEffect(
                    new TakeDestinyCardIntoHandEffect(action));
            // Allow response(s)
            action.allowResponses("Cancel destiny and cause re-draw",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDestinyAndCauseRedrawEffect(action));
                        }
                    }
            );
            actions.add(action);
        }




        // Check conditions(s) - Place a card just stacked on Droid Racks or Strategic Reserves in opponent's Lost Pile.
        GameTextActionId gameTextActionId = GameTextActionId.EFFECTIVE_REPAIRS_AND_STARSHIP_LEVITATION__PLACE_JUST_STACKED_CARD_IN_LOST_PILE;
        if (TriggerConditions.justStackedCardOn(game, effectResult, Filters.any, Filters.or(Filters.Strategic_Reserves, Filters.Droid_Racks))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place just stacked card in lost pile");

            // Figure out what the card was which was just stacked
            // Could have came from table/lost pile (Droid Racks) or from hand (Strategic Reserves)
            PhysicalCard cardStacked = null;
            if (effectResult.getType() == EffectResult.Type.STACKED_FROM_HAND) {
                StackedFromHandResult stackedFromHandResult = (StackedFromHandResult) effectResult;
                cardStacked = stackedFromHandResult.getCard();
            } else if (effectResult.getType() == EffectResult.Type.STACKED_FROM_CARD_PILE) {
                StackedFromCardPileResult stackedFromCardPileResult = (StackedFromCardPileResult) effectResult;
                cardStacked = stackedFromCardPileResult.getCard();
            } else if (effectResult.getType() == EffectResult.Type.STACKED_FROM_TABLE) {
                StackedFromTableResult stackedFromTableResult = (StackedFromTableResult) effectResult;
                cardStacked = stackedFromTableResult.getCard();
            } else {
                // Should not be possible. Stacked cards must have came from one of those places!
            }

            // Final so the callbacks can access it correctly
            final PhysicalCard stackedCard = cardStacked;

            // Allow response(s)
                action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {

                            // Perform result(s)
                            action.appendEffect(
                                    new PutStackedCardInLostPileEffect(action, playerId, stackedCard, false));

                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }



    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter filter = Filters.or(Filters.Limited_Resources, Filters.Broken_Concentration, Filters.Lateral_Damage);

        // Check condition(s) - is playing Limited Resources, Broken Concentration or Lateral Damage
        if (TriggerConditions.isPlayingCard(game, effect, filter)
            && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }


        return actions;
    }

}
