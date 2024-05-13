package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelDuelEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedBySpecificWeaponsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Lightsaber Parry
 */
public class Card11_083 extends AbstractUsedOrLostInterrupt {
    public Card11_083() {
        super(Side.DARK, 4, "Lightsaber Parry", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.C);
        setLore("Vader had to show Luke that giving in to the dark side of the Force was his only destiny.");
        setGameText("USED: If a battle was just initiated where you have a character armed with a lightsaber, characters may not be targeted by lightsabers for remainder of battle. LOST: If opponent just initiated a duel, opponent must choose to lose 4 Force or cancel the duel.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.character_with_a_lightsaber))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Prevent lightsabers from targeting characters");
            // Allow response(s)
            action.allowResponses("Prevent characters from being targeted by lightsabers",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new MayNotBeTargetedBySpecificWeaponsModifier(self, Filters.character, Filters.lightsaber),
                                            "Prevents characters from being targeted by lightsabers"));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.duelInitiatedBy(game, effectResult, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make opponent lose 4 Force or cancel duel");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, opponent,
                                            new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Lose 4 Force", "Cancel duel"}) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    if (index==0) {
                                                        game.getGameState().sendMessage(opponent + " chooses to lose 4 Force");
                                                        action.appendEffect(
                                                                new LoseForceEffect(action, opponent, 4));
                                                    }
                                                    else {
                                                        game.getGameState().sendMessage(opponent + " chooses to cancel duel");
                                                        action.appendEffect(
                                                                new CancelDuelEffect(action));
                                                    }
                                                }
                                            }
                                    )
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}