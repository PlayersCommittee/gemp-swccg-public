package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfAttackEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: Skull
 */
public class Card6_074 extends AbstractUsedInterrupt {
    public Card6_074() {
        super(Side.LIGHT, 4, "Skull", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("Bidlo Kwerve was involved in both the procurement and (posthumously) the loss of the rancor.");
        setGameText("During a battle or attack, place out of play one non-droid character, creature or creature vehicle from your Lost Pile. Add its destiny number to your total power. OR Search your Reserve Deck, take Fallen Portal or Blast the Door, Kid! into hand and reshuffle.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.SKULL__PLACE_CARD_OUT_OF_PLAY_FROM_LOST_PILE;

        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {
            // Check condition(s)
            if (GameConditions.isDuringBattle(game)) {
                actions.add(getInterruptAction(self, game, gameTextActionId, playerId, false));
            } else if (GameConditions.isDuringAttack(game)) {
                AttackState attackState = game.getGameState().getAttackState();
                if ((attackState.isNonCreatureAttackingCreature() && attackState.getAttackerOwner().equals(playerId))
                        || (attackState.isCreatureAttackingNonCreature() && attackState.getDefenderOwner().equals(playerId))) {
                    actions.add(getInterruptAction(self, game, gameTextActionId, playerId, true));
                }
            }
        }

        gameTextActionId = GameTextActionId.SKULL__UPLOAD_FALLEN_PORTAL_OR_BLAST_THE_DOOR_KID;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Fallen Portal or Blast the Door, Kid! into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Fallen_Portal, Filters.Blast_The_Door_Kid), true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    private PlayInterruptAction getInterruptAction(PhysicalCard self, final SwccgGame game, GameTextActionId gameTextActionId, final String playerId, final boolean isDuringAnAttack) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
        action.setText("Place card from Lost Pile out of play");
        // Pay cost(s)
        action.appendCost(
                new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, playerId, Filters.or(Filters.non_droid_character, Filters.creature, Filters.creature_vehicle), false) {
                    @Override
                    protected void cardPlacedOutOfPlay(final PhysicalCard card) {
                        action.appendCost(
                                new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(card)) {
                                    @Override
                                    protected void refreshedPrintedDestinyValues() {
                                        final float destiny = game.getModifiersQuerying().getDestiny(game.getGameState(), card);
                                        // Allow response(s)
                                        action.allowResponses("Add " + GuiUtils.formatAsString(destiny) + " to total power",
                                                new RespondablePlayCardEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        if (isDuringAnAttack) {
                                                            action.appendEffect(
                                                                    new ModifyTotalPowerUntilEndOfAttackEffect(action, destiny, playerId,
                                                                            "Adds " + GuiUtils.formatAsString(destiny) + " to total power"));
                                                        } else {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new ModifyTotalPowerUntilEndOfBattleEffect(action, destiny, playerId,
                                                                            "Adds " + GuiUtils.formatAsString(destiny) + " to total power"));
                                                        }
                                                    }
                                                }
                                        );

                                    }
                                });
                    }
                }
        );
        return action;
    }
}