package com.gempukku.swccgo.cards.set8.light;

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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
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
 * Title: Here We Go Again
 */
public class Card8_054 extends AbstractLostInterrupt {
    public Card8_054() {
        super(Side.LIGHT, 3, Title.Here_We_Go_Again, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("C-3PO had been through enough battles that many of the Rebels in the strike team considered him a good luck charm. That isn't what Han considers him.");
        setGameText("If a battle that opponent initiated just ended, initiate a new battle at same location. 'Reacts' are allowed only for opponent, but cards may not 'react' away or hide from battle. Loser ignores battle damage. If your droid present there, add one battle destiny (two if C-3PO).");
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
        // Skip phase/current-player and already-battled checks; still respect MayNotInitiate and presence; cost is paid.
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
                        Condition c3poPresent = yourPresentAtBattle(self, playerId, Filters.C3PO);
                        Condition droidPresent = yourPresentAtBattle(self, playerId, Filters.droid);
                        extraDestiny.add(new AddsBattleDestinyModifier(self, c3poPresent, 2, playerId, true));
                        extraDestiny.add(new AddsBattleDestinyModifier(self, new AndCondition(droidPresent, new NotCondition(c3poPresent)), 1, playerId, true));

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
