package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfCardPlayedModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.modifiers.BattleDamageMultiplierModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: You Overestimate Their Chances
 */
public class Card1_279 extends AbstractLostInterrupt {
    public Card1_279() {
        super(Side.DARK, 4, Title.You_Overestimate_Their_Chances, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1);
        setLore("'Evacuate? In our moment of triumph?'");
        setGameText("If an opponent has just initiated a battle, triple the resulting battle damage for the eventual loser. OR Triple the result of Don't Underestimate Our Chances.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Triple battle damage");
            // Allow response(s)
            action.allowResponses("Triple battle damage for the eventual loser",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            int multiplier = 3;
                            String text = "Triples battle damage for the eventual loser";
                            if (game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.YOU_OVERESTIMATE_THEIR_CHANCES__TRIPLE_RESULT)) {
                                multiplier = 9;
                                text = "Multiplies battle damage for the eventual loser by 9";
                            }
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new BattleDamageMultiplierModifier(self, multiplier), text)
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, final SwccgGame game, Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Dont_Underestimate_Our_Chances)) {
            final PhysicalCard interruptCard = ((RespondablePlayingCardEffect) effect).getCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Triple " + GameUtils.getFullName(interruptCard));
            // Allow response(s)
            action.allowResponses("Triple the result of " + GameUtils.getCardLink(interruptCard),
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfCardPlayedModifierEffect(action, interruptCard,
                                            new ModifyGameTextModifier(self, Filters.samePermanentCardId(interruptCard), ModifyGameTextType.DONT_UNDERESTIMATE_OUR_CHANCES__TRIPLE_RESULT),
                                            "Triples the result of " + GameUtils.getCardLink(interruptCard)));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}