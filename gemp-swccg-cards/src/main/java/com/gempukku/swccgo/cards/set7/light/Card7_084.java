package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddToAttritionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: Balanced Attack
 */
public class Card7_084 extends AbstractUsedInterrupt {
    public Card7_084() {
        super(Side.LIGHT, 4, "Balanced Attack", Uniqueness.UNIQUE);
        setLore("Alliance starfighter pilots cross-train with other starship types to maximize combat efficiency.");
        setGameText("If you have more than one class of 'snub fighter' (Z-95, A-wing, Bwing, X-wing or Y-wing) in a battle, for each different class, add 1 to attrition against opponent (add 2 more if Falcon present). OR Take one admiral or general into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.canModifyAttritionAgainst(game, opponent)) {
            int snubFighterClasses = 0;
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Z_95))) {
                snubFighterClasses++;
            }
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.A_wing))) {
                snubFighterClasses++;
            }
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.B_wing))) {
                snubFighterClasses++;
            }
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.X_wing))) {
                snubFighterClasses++;
            }
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Y_wing))) {
                snubFighterClasses++;
            }
            if (snubFighterClasses > 1) {
                final int amountToAdd = snubFighterClasses + (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Falcon, Filters.presentInBattle)) ? 2 : 0);

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add " + amountToAdd + " to attrition");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddToAttritionEffect(action, opponent, amountToAdd));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.BALANCED_ATTACK__UPLOAD_ADMIRAL_OR_GENERAL;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take an admiral or general into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.admiral, Filters.general), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}