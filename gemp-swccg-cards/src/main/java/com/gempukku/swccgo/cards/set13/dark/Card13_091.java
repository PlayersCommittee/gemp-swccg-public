package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationToLocationModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Effect
 * Title: They Must Never Again Leave This City
 */
public class Card13_091 extends AbstractNormalEffect {
    public Card13_091() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "They Must Never Again Leave This City", Uniqueness.UNIQUE);
        setLore("Vader's plans were to turn the city in the clouds into a graveyard for the Rebellion.");
        setGameText("Deploy on table. Opponent's movement from a Bespin location to a non-Bespin location requires +1 Force. Once per game, Executor deploys for free to Bespin system. Once per turn, may take into hand a TIE just forfeited from a Bespin location.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostFromLocationToLocationModifier(self, Filters.opponents(self), 1, Filters.Bespin_location, Filters.non_Bespin_location));
        final int permCardId = self.getPermanentCardId();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Executor,
                new Condition() {
                    @Override
                    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                        PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                        return modifiersQuerying.getUntilEndOfGameLimitCounter(self.getTitle(), GameTextActionId.THEY_MUST_NEVER_AGAIN_LEAVE_THIS_CITY__EXECUTOR_DEPLOYS_FOR_FREE).getUsedLimit() < 1;
                    }
                }, Filters.Bespin_system));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployedToLocation(game, effectResult, Filters.Executor, Filters.Bespin_system)) {
            game.getModifiersQuerying().getUntilEndOfGameLimitCounter(self.getTitle(), GameTextActionId.THEY_MUST_NEVER_AGAIN_LEAVE_THIS_CITY__EXECUTOR_DEPLOYS_FOR_FREE).incrementToLimit(1, 1);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justForfeitedToLostPileFromLocation(game, effectResult, Filters.tieCountNoMoreThan(1), Filters.Bespin_location)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final PhysicalCard forfeitedCard = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take " + GameUtils.getFullName(forfeitedCard) + " into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromLostPileEffect(action, playerId, forfeitedCard, false, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}