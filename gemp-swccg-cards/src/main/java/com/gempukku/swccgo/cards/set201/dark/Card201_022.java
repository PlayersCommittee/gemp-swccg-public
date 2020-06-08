package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHavePowerReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Character
 * Subtype: Imperial
 * Title: Colonel Wullf Yularen (V)
 */
public class Card201_022 extends AbstractImperial {
    public Card201_022() {
        super(Side.DARK, 2, 2, 1, 2, 5, Title.Yularen, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Imperial Security Bureau (ISB) officer assigned to brief Tarkin. Also ordered to ensure absolute loyalty to the Emperor. Leader. Will stop at nothing to fulfill the Emperor's will.");
        setGameText("[Power] 1, 2: any capital starship. When deployed, may activate up to X Force (limit 4), where X = number of ISB agents on table. While aboard a starship, its power may not be reduced by opponent.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(1, 2, Filters.capital_starship)));
        modifiers.add(new MayNotHavePowerReducedModifier(self, Filters.and(Filters.starship, Filters.hasAboard(self)), opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canActivateForce(game, playerId)) {
            GameState gameState = game.getGameState();
            int maxForce = Math.min(gameState.getReserveDeckSize(playerId), Math.min(4, Filters.countActive(game, self, Filters.ISB_agent)));
            if (maxForce > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Activate up to " + maxForce + " Force");
                // Choose target(s)
                action.appendTargeting(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to activate ", 1, maxForce, maxForce) {
                                    @Override
                                    public void decisionMade(final int result) throws DecisionResultInvalidException {
                                        action.setActionMsg("Activate " + result + " Force");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ActivateForceEffect(action, playerId, result));
                                    }
                                }
                        )
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
