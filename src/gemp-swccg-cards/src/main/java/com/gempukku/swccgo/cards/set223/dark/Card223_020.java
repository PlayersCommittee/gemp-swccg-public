package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Physical Choke (V)
 */
public class Card223_020 extends AbstractUsedOrLostInterrupt {
    public Card223_020() {
        super(Side.DARK, 3, Title.Physical_Choke, Uniqueness.UNRESTRICTED, ExpansionSet.SET_23, Rarity. V);
        setLore("Darth Vader often used physical means of 'persuasion' to get information. Captain Antilles of Tantive IV chose to die rather than reveal the location of the stolen Death Star plans.");
        setGameText("USED: [Upload] (or place in owner's Used Pile) Deactivated Hyperdrive or an Admiral's Order. LOST: If Vader in battle against two characters of ability < 4 present with him, add one battle destiny and, if one is a Rebel trooper, it is 'choked' (lost).");
        setVirtualSuffix(true);
        addIcon(Icon.VIRTUAL_SET_23);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter targetFilter = Filters.or(Filters.Deactivated_Hyperdrive, Filters.Admirals_Order);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;


        if (GameConditions.canTarget(game, self, targetFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Place card in Used Pile");
            action.setActionMsg("Place Deactivated Hyperdrive or an Admiral's Order in owner's Used Pile");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose target", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " in Used Pile",
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
                                    }
                                }
                            );
                        }
                    }
            );

            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.PHYSICAL_CHOKE__UPLOAD_DEACTIVATED_HYPERDRIVE;

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId2)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId2, CardSubtype.USED);
            action.setText("Take Deactivated Hyperdrive or an Admiral's Order into hand from Reserve Deck");

            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Deactivated_Hyperdrive, Filters.Admirals_Order), true));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId3 = GameTextActionId.OTHER_CARD_ACTION_2;
        Filter characterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(4), Filters.participatingInBattle, Filters.presentWith(self, Filters.Vader));

        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Vader)
                && GameConditions.canSpot(game, self, 2, characterFilter)){

            final Filter trooperFilter = Filters.and(characterFilter, Filters.Rebel, Filters.trooper, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_CHOKED));

            if (GameConditions.canAddBattleDestinyDraws(game, self)
                    && GameConditions.canSpot(game, self, trooperFilter)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId3, CardSubtype.LOST);

                action.setText("Add a battle destiny and choke a Rebel trooper");
                action.setActionMsg("Add one battle destiny and choke a Rebel trooper");
                action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendTargeting(
                                new TargetCardOnTableEffect(action, playerId, "Choose Rebel trooper", trooperFilter) {
                                    @Override
                                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                        action.addAnimationGroup(targetedCard);
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                new LoseCardFromTableEffect(action, finalTarget)
                                            );
                                    }
                                }
                            );
                            action.appendEffect(
                                new AddBattleDestinyEffect(action, 1));
                        }
                    }
                );
                actions.add(action);
            } else if (GameConditions.canAddBattleDestinyDraws(game, self)
                    && !GameConditions.canSpot(game, self, trooperFilter)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId3, CardSubtype.LOST);

                action.setText("Add a battle destiny");
                action.setActionMsg("Add one battle destiny");
                action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                new AddBattleDestinyEffect(action, 1));
                        }
                    }
                );
                actions.add(action);
            } 
        }
        return actions;
    }
}
