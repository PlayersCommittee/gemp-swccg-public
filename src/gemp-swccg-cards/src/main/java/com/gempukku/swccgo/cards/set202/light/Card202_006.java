package com.gempukku.swccgo.cards.set202.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotFireWeaponsModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Force Levitation
 */
public class Card202_006 extends AbstractUsedInterrupt {
    public Card202_006() {
        super(Side.LIGHT, 4, "Force Levitation", Uniqueness.UNIQUE, ExpansionSet.SET_2, Rarity.V);
        setLore("Telekinesis is one of the powers awakened during a Jedi's apprenticeship. Using this ability, a student of the Force can learn to levitate objects.");
        setGameText("[Upload] a device (except Landing Claw). OR Target one opponent's droid present with your Jedi in battle. For remainder of turn, target is power -1 and may not fire weapons.");
        addIcons(Icon.VIRTUAL_SET_2);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.FORCE_LEVITATION__UPLOAD_DEVICE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take device into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a device (except Landing Claw) into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.device, Filters.except(Filters.Landing_Claw)), true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        Filter filter = Filters.and(Filters.opponents(self), Filters.droid, Filters.participatingInBattle, Filters.presentWith(self, Filters.and(Filters.your(self), Filters.Jedi)));

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target a droid");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose droid", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard droid) {
                            action.addAnimationGroup(droid);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(droid) + " power -1 and may not fire weapons",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ModifyPowerUntilEndOfTurnEffect(action, finalTarget, -1));
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new MayNotFireWeaponsModifier(self, finalTarget),
                                                            "Makes " + GameUtils.getCardLink(droid) + " not fire weapons"));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}