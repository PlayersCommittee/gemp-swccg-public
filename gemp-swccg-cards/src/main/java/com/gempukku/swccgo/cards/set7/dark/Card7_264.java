package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: Tauntaun Skull
 */
public class Card7_264 extends AbstractUsedInterrupt {
    public Card7_264() {
        super(Side.DARK, 5, "Tauntaun Skull", Uniqueness.UNIQUE);
        setLore("Bones strewn around the cave lair of the wampa are proof of the beat's prowess.");
        setGameText("During a battle or attack, place out of play one non-droid character, creature or creature vehicle from your Lost Pile. Add its destiny number to your total power. OR Take one Stop Motion or Yaggle Gakkle into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.TAUNTAUN_SKULL__PLACE_CARD_OUT_OF_PLAY_FROM_LOST_PILE;

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

        gameTextActionId = GameTextActionId.TAUNTAUN_SKULL__UPLOAD_STOP_MOTION_OR_YAGGLE_GAKKLE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a Stop Motion or Yaggle Gakkle into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Stop_Motion, Filters.Yaggle_Gakkle), true));
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