package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.cards.effects.RetargetInterruptEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Set: Set 11
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Trade Federation Tactics (V)
 */
public class Card211_014 extends AbstractUsedOrLostInterrupt {
    public Card211_014() {
        super(Side.DARK, 6, "Trade Federation Tactics", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Organizations as wealthy as the Trade Federation can afford large amounts of military hardware, all purchased under the guise of protecting their commercial interests.");
        setGameText("USED: Cancel an attempt to use a weapon to target your capital starship. LOST: Re-target On Target to an opponent’s capital starship armed with a weapon. OR [upload] Blockade Support Ship or Invisible Hand.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();


        // "USED: Cancel an attempt to use a weapon to target your capital starship."

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.capital_starship), Filters.weapon)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel weapon targeting");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                        }
                    }
            );
            actions.add(action);
        }




        // "LOST: Re-target On Target to an opponent’s capital starship armed with a weapon."

        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.On_Target, Filters.any)) {

            final Action onTargetTargettingAction = effect.getAction();
            final List<PhysicalCard> originalTargets = TargetingActionUtils.getCardsTargeted(game, onTargetTargettingAction, Filters.any);
            if (!originalTargets.isEmpty()) {

                Set<TargetingReason> targetingReasons = TargetingActionUtils.getTargetingReasons(game, onTargetTargettingAction, Filters.any);

                Filter opponentsCapitalWithWeapon = Filters.and(
                        Filters.opponents(self),
                        Filters.capital_starship,
                        Filters.armedWith(Filters.weapon),
                        Filters.canBeTargetedBy(onTargetTargettingAction.getActionSource(), targetingReasons));

                if (GameConditions.canTarget(game, self, opponentsCapitalWithWeapon)) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                    action.setText("Re-target 'On Target'");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose starship to retarget " + GameUtils.getCardLink(onTargetTargettingAction.getActionSource()) + " to", targetingReasons, opponentsCapitalWithWeapon) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                    action.addAnimationGroup(targetedCard);
                                    // Allow response(s)
                                    action.allowResponses("Re-target " + GameUtils.getCardLink(onTargetTargettingAction.getActionSource()) + " to " + GameUtils.getCardLink(targetedCard),
                                            new RespondablePlayCardEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                    Collection<PhysicalCard> finalTargets = action.getPrimaryTargetCards(targetGroupId);

                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new RetargetInterruptEffect(action, onTargetTargettingAction, originalTargets, finalTargets));
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }



        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.TRADE_FEDERATION_TACTICS_UPLOAD_SHIP;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Blockade Support Ship or Invisible Hand into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Invisible_Hand, Filters.Blockade_Support_Ship), true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}