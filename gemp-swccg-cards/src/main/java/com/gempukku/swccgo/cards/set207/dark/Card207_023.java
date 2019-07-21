package com.gempukku.swccgo.cards.set207.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.MinLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.cards.evaluators.ThereEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 7
 * Type: Character
 * Subtype: Sith
 * Title: Savage Opress
 */
public class Card207_023 extends AbstractSith {
    public Card207_023() {
        super(Side.DARK, 1, 8, 8, 4, 5, "Savage Opress", Uniqueness.UNIQUE);
        setLore("Dathomirian assassin.");
        setGameText("Deploys -1 for each other Sith character here (limit -4). Once per game, may exchange a card in hand with a Sith character in Lost Pile. Immune to attrition < 3.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.SEPARATIST, Icon.EPISODE_I, Icon.VIRTUAL_SET_7);
        addKeywords(Keyword.ASSASSIN);
        setSpecies(Species.DATHOMIRIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        final Evaluator evaluator = new MinLimitEvaluator(new NegativeEvaluator(new ThereEvaluator(self, Filters.Sith)), -4);
        final Filterable locationFilter = Filters.any;

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, evaluator, locationFilter) {
            @Override
            public float getDeployCostToTargetModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, PhysicalCard target) {
                if (Filters.and(locationFilter).accepts(gameState, modifiersQuerying, target)) {
                    return evaluator.evaluateExpression(gameState, modifiersQuerying, target);
                }
                return 0;
            }
        });
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SAVAGE_OPRESS__EXCHANGE_CARD_IN_HAND_WITH_SITH_IN_LOST_PILE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasLostPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange card in hand with card in Lost Pile");
            action.setActionMsg("Exchange a card in hand with a Sith character in Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, Filters.any, Filters.Sith));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
