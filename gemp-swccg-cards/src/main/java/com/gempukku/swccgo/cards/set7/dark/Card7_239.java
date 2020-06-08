package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Return To Base
 */
public class Card7_239 extends AbstractNormalEffect {
    public Card7_239() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Return To Base", Uniqueness.UNIQUE);
        setLore("Imperial codes demand that damaged starships return to base for repair. Some captains with only minor damage to their ships resent this order.");
        setGameText("Use 4 Force to deploy on your side of table. A starship you just lost may be placed here. Holds one starship at a time. During your deploy phase, may use X Force to bring starship to hand, where X = deploy cost of that starship.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.starship))
                && !GameConditions.hasStackedCards(game, self)) {
            PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Stack " + GameUtils.getFullName(cardLost) + " here");
            action.setActionMsg("Stack " + GameUtils.getFullName(cardLost) + " on " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new StackOneCardFromLostPileEffect(action, cardLost, self, false, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.hasStackedCards(game, self)) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            PhysicalCard starship = gameState.getStackedCards(self).iterator().next();
            float deployCost = modifiersQuerying.getDeployCost(game.getGameState(), starship);
            final float valueForX = modifiersQuerying.getVariableValue(gameState, self, Variable.X, deployCost);
            if (GameConditions.canUseForce(game, playerId, valueForX)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take starship into hand");
                action.setActionMsg("Take " + GameUtils.getCardLink(starship) + " into hand from " + GameUtils.getCardLink(self));
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, valueForX));
                // Perform result(s)
                action.appendEffect(
                        new TakeStackedCardIntoHandEffect(action, playerId, self, starship));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}