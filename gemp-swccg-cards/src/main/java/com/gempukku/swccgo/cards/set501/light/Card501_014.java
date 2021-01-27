package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInLostPileEqualToOrMoreThanCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardsEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Character
 * Subtype: Rebel
 * Title: Daughter Of Skywalker (V)
 */
public class Card501_014 extends AbstractRebel {
    public Card501_014() {
        super(Side.LIGHT, 1, 4, 4, 5, 7, Title.Daughter_Of_Skywalker, Uniqueness.UNIQUE);
        setLore("Scout. Leader. Made friends with Wicket. Negotiated an alliance with the Ewoks. Leia found out the truth about her father from Luke in the Ewok village.");
        setGameText("[Death Star II] Vader’s game text canceled here. If opponent just initiated battle here, may activate 2 Force. May place X cards stacked on I Feel The Conflict in owner’s Lost Pile to reduce your Force loss by X. Immune to attrition < 4.");
        addPersona(Persona.LEIA);
        addIcons(Icon.ENDOR, Icon.VIRTUAL_SET_14, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT, Keyword.LEADER);
        setVirtualSuffix(true);
        setTestingText("Daughter Of Skywalker (V)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Icon.DEATH_STAR_II, Filters.Vader, Filters.here(self))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, game.getOpponent(playerId), Filters.wherePresent(self))
                && GameConditions.canActivateForce(game, playerId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 2 Force");
            action.appendEffect(new ActivateForceEffect(action, playerId, 2));
            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.I_Feel_The_Conflict, Filters.hasStacked(Filters.any)))
                && GameConditions.canReduceForceLoss(game)) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;
            if (!result.isCannotBeReduced()) {

                PhysicalCard ifeeltheconflict = Filters.findFirstActive(game, self, Filters.I_Feel_The_Conflict);

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Reduce Force loss");
                action.setActionMsg("Choose stacked cards to place in Lost Pile");
                // Pay cost(s)
                action.appendCost(
                        new ChooseStackedCardsEffect(action, playerId, ifeeltheconflict, 1,  Filters.countStacked(game, Filters.stackedOn(ifeeltheconflict))) {
                            @Override
                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                final int numCards = selectedCards.size();
                                action.appendCost(new PutStackedCardsInLostPileEffect(action, playerId, selectedCards, false));
                                action.allowResponses("Reduce force loss by "+numCards, new RespondableEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        action.appendEffect(new ReduceForceLossEffect(action, playerId, numCards));
                                    }
                                });
                            }
                        }
                );
                actions.add(action);
            }

        }

        return actions;
    }
}
