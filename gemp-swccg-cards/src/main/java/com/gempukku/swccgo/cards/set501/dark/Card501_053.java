package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtBottomCardOfCardPileEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: Imperial Code Cylinder (V)
 */
public class Card501_053 extends AbstractUsedInterrupt {
    public Card501_053() {
        super(Side.DARK, 4, "Imperial Code Cylinder");
        setLore("");
        setGameText("Take a non-spy Captain (or Krennic) into hand from Reserve Deck; reshuffle. OR 'glance' at the bottom card of any pile. OR if your Captain in battle with your other leader, cancel a destiny draw during the power segment of battle (except a battle destiny)");
        addIcons(Icon.VIRTUAL_SET_13);
        setVirtualSuffix(true);
        setTestingText("Imperial Code Cylinder");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_CODE_CYLINDER__UPLOAD_CHARACTER;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take a non-spy Captain (or Krennic) into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.and(Filters.not(Filters.spy), Filters.captain), Filters.Krennic), true));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId2);
            action.setText("Peek at bottom card of any card pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ChooseExistingCardPileEffect(action, playerId, "Choose card pile to peek at bottom card of", Filters.any) {
                                        @Override
                                        public void pileChosen(SwccgGame game, String cardPileOwner, Zone cardPile) {
                                            action.appendEffect(
                                                    new PeekAtBottomCardOfCardPileEffect(action, playerId, cardPileOwner, cardPile));
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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && !TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.captain))) {

            PhysicalCard captain = Filters.findFirstActive(game, self, Filters.and(Filters.your(playerId), Filters.captain, Filters.presentInBattle));

            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.other(captain), Filters.leader))) {
                BattleState battleState = game.getGameState().getBattleState();
                if (battleState != null &&
                        battleState.isReachedPowerSegment()) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Cancel a non-battle destiny draw");
                    // Allow response(s)
                    action.allowResponses("Cancel a non-battle destiny draw",
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new CancelDestinyEffect(action));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
