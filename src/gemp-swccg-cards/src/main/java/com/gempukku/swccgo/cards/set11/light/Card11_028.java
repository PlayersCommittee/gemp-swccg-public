package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: A Jedi's Patience
 */
public class Card11_028 extends AbstractUsedOrLostInterrupt {
    public Card11_028() {
        super(Side.LIGHT, 5, "A Jedi's Patience", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.C);
        setLore("Early on in his duel with Vader, Luke took his time in trying to sense his opponent's weaknesses.");
        setGameText("USED: If your character armed with a lightsaber is defending a battle, adds 2 to your total power in that battle. LOST: During a duel, cancel one of your duel destiny draws to cause a re-draw.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.character_with_a_lightsaber, Filters.defendingBattle))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add 2 to total power");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyTotalPowerUntilEndOfBattleEffect(action, 2, playerId,
                                            "Adds 2 to total power"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isDuelDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY, CardSubtype.LOST);

            action.setText("Cancel and re-draw duel destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDestinyAndCauseRedrawEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}