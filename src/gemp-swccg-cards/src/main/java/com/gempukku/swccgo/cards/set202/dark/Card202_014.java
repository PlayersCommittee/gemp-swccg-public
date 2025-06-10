package com.gempukku.swccgo.cards.set202.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.CalculateCardVariableEvaluator;
import com.gempukku.swccgo.cards.evaluators.MinLimitEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 2
 * Type: Starship
 * Subtype: Starfighter
 * Title: Black 6
 */
public class Card202_014 extends AbstractStarfighter {
    public Card202_014() {
        super(Side.DARK, 6, 1, 1, null, 4, null, 3, "Black 6", Uniqueness.UNIQUE, ExpansionSet.SET_2, Rarity.V);
        setGameText("May add 1 pilot. While Tanbris piloting, power +1. Once per game, may retrieve bottom card of Lost Pile. Your total battle destiny here is +X, where X = this TIE's power - 5.");
        addIcons(Icon.VIRTUAL_SET_2);
        addKeywords(Keyword.BLACK_SQUADRON);
        addModelType(ModelType.TIE_LN);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Tanbris);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new HasPilotingCondition(self, Filters.Tanbris), 1));
        final int permCardId = self.getPermanentCardId();
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), new MinLimitEvaluator(new CalculateCardVariableEvaluator(self, Variable.X) {
            @Override
            protected float baseValueCalculation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                return modifiersQuerying.getPower(gameState, self) - 5;
            }
        }, 0), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BLACK_6__RETRIEVE_BOTTOM_CARD_OF_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasLostPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve bottom card of Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.bottomOfLostPile(playerId)));
            return Collections.singletonList(action);
        }
        return null;
    }
}
