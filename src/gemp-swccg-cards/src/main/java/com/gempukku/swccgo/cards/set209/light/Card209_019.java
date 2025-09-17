package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.StackedCardResult;

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
        super(Side.LIGHT, 4, "Effective Repairs & Starship Levitation", Uniqueness.UNRESTRICTED, ExpansionSet.SET_9, Rarity.V);
        addComboCardTitles(Title.Effective_Repairs, Title.Starship_Levitation);
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
                && GameConditions.canTakeCardsIntoHandFromLostPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Retrieve Effect or starship");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses("Retrieve an Effect of any kind or a non-[Maintenance] starship into hand",
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
        GameTextActionId gameTextActionId2 = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s) - If you just drew a starship for destiny, take that starship into hand to cancel and redraw that destiny.
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.starship)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId2, CardSubtype.USED);
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
            action.setImmuneTo(Title.Sense);

            StackedCardResult stackedCardResult = (StackedCardResult)effectResult;
            final PhysicalCard stackedCard = stackedCardResult.getCard();

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
