package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.MayNotBattleUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used
 * Title: Ng'ok
 */
public class Card2_136 extends AbstractUsedInterrupt {
    public Card2_136() {
        super(Side.DARK, 3, "Ng'ok");
        setLore("Dejarik of a Ng'ok war beast. Foul temper gives rise to bad feelings. Has razor-sharp retractable claws. Used in many systems to frighten off potential attackers.");
        setGameText("If opponent just deployed four or more characters to same location this turn, prevent all of those characters from battling this turn. OR If opponent just 'reacted' to a battle, cancel the battle.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.DEJARIK);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, opponent, Filters.character, Filters.any)) {
            PhysicalCard location = ((PlayCardResult) effectResult).getToLocation();
            if (location != null
                    && GameConditions.hasDeployedAtLeastXCardsToLocationThisTurn(game, opponent, 4, Filters.character, location)) {
                final Collection<PhysicalCard> characters = game.getModifiersQuerying().getCardsPlayedThisTurnToLocation(playerId, location);
                if (!characters.isEmpty()) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Prevent characters from battling");
                    action.addAnimationGroup(characters);
                    // Allow response(s)
                    action.allowResponses("Prevent " + GameUtils.getAppendedNames(characters) + " from battling this turn",
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                     // Perform result(s)
                                    action.appendEffect(
                                            new MayNotBattleUntilEndOfTurnEffect(action, characters));
                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }
        // Check condition(s)
        if (TriggerConditions.reactedToBattle(game, effectResult, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel battle");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelBattleEffect(action));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}