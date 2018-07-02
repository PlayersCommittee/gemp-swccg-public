package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.CalculateCardVariableEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceTopCardOfForcePileOnTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.TotalTrainingDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Broken Concentration
 */
public class Card4_118 extends AbstractNormalEffect {
    public Card4_118() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Broken Concentration", Uniqueness.UNIQUE);
        setLore("To effectively use the Force, a Jedi is required to learn concentration. A Jedi who gives in to distraction places both himself and his friends in peril.");
        setGameText("Deploy on your side of table. Apprentices subtract X from training destiny, where X = ability - Jedi Test number (minimum zero). Also, once per turn, you may use 2 Force to move the top card of opponent's Force Pile to the top of opponent's Reserve Deck.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalTrainingDestinyModifier(self, Filters.any,
                new NegativeEvaluator(
                        new CalculateCardVariableEvaluator(self, Variable.X) {
                                @Override
                                protected float baseValueCalculation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard jediTest) {
                                    if (jediTest.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE) == null)
                                        return 0;

                                    return Math.max(0, modifiersQuerying.getAbility(gameState, jediTest.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE)) - modifiersQuerying.getJediTestNumber(gameState, jediTest));
                                }
                            })));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.hasForcePile(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Move top card of Force Pile");
            action.setActionMsg("Move top card of opponent's Force Pile to top of opponent's Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new PlaceTopCardOfForcePileOnTopOfReserveDeckEffect(action, opponent));
            return Collections.singletonList(action);
        }
        return null;
    }
}