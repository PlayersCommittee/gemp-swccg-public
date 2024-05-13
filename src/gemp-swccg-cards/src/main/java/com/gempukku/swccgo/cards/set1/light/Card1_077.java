package com.gempukku.swccgo.cards.set1.light;

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
import com.gempukku.swccgo.logic.effects.TargetCardBeingPlayedEffect;
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
 * Title: Don't Underestimate Our Chances
 */
public class Card1_077 extends AbstractLostInterrupt {
    public Card1_077() {
        super(Side.LIGHT, 4, Title.Dont_Underestimate_Our_Chances, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1);
        setLore("'Stand-by alert. Death Star approaching. Estimated time to firing range, fifteen minutes.'");
        setGameText("If an opponent has just initiated a battle, triple the resulting battle damage for the eventual loser. OR Triple the result of You Overestimate Their Chances.");
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
                            if (game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.DONT_UNDERESTIMATE_OUR_CHANCES__TRIPLE_RESULT)) {
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
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.You_Overestimate_Their_Chances, Filters.canBeTargetedBy(self)))) {
            RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Triple " + GameUtils.getFullName(respondableEffect.getCard()));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardBeingPlayedEffect(action, respondableEffect) {
                        @Override
                        protected void cardTargeted(final PhysicalCard targetedCard) {
                            // Allow response(s)
                            action.allowResponses("Triple the result of " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfCardPlayedModifierEffect(action, targetedCard,
                                                            new ModifyGameTextModifier(self, Filters.samePermanentCardId(targetedCard), ModifyGameTextType.YOU_OVERESTIMATE_THEIR_CHANCES__TRIPLE_RESULT),
                                                            "Triples the result of " + GameUtils.getCardLink(targetedCard)));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}