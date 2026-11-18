package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.InitiateUnusualBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UnusualBattleInitOptions;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleEndedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Lost
 * Title: Counterattack
 */
public class Card8_138 extends AbstractLostInterrupt {
    public Card8_138() {
        super(Side.DARK, 3, Title.Counterattack, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("The Imperial forces on Endor quickly responded to the Rebel intrusion. Commander Igar had a plan in place to deal with such a commando force of Rebels.");
        setGameText("If a battle that opponent initiated just ended, initiate a new battle at same location. 'Reacts' are allowed only for opponent, but cards may not 'react' away or hide from battle. Loser ignores battle damage. If your trooper present there, add one battle destiny.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        if (!TriggerConditions.battleEnded(game, effectResult)) {
            return null;
        }

        BattleEndedResult ended = (BattleEndedResult) effectResult;
        BattleState endedBattle = ended.getBattleState();
        PhysicalCard location = ended.getLocation();
        if (endedBattle == null || endedBattle.isCanceled() || location == null) {
            return null;
        }
        if (playerId.equals(endedBattle.getPlayerInitiatedBattle())) {
            return null;
        }
        if (!GameConditions.canInitiateBattleAtLocation(playerId, game, location, false, true, false, true)) {
            return null;
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Initiate a new battle");
        action.allowResponses("Initiate a new battle at " + GameUtils.getCardLink(location),
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        List<Modifier> extraDestiny = new LinkedList<Modifier>();
                        extraDestiny.add(new AddsBattleDestinyModifier(self, yourPresentAtBattle(self, playerId, Filters.trooper), 1, playerId, true));

                        action.appendEffect(
                                new InitiateUnusualBattleEffect(action,
                                        UnusualBattleInitOptions.reinitiateBattle(playerId, location, self, opponent, extraDestiny)));
                    }
                }
        );
        return Collections.singletonList(action);
    }

    private static Condition yourPresentAtBattle(final PhysicalCard self, final String playerId, final com.gempukku.swccgo.common.Filterable filter) {
        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                if (gameState.getBattleState() == null) {
                    return false;
                }
                return Filters.canSpot(gameState.getGame(), self,
                        Filters.and(Filters.your(playerId), Filters.and(filter), Filters.presentAt(Filters.battleLocation)));
            }
        };
    }
}
