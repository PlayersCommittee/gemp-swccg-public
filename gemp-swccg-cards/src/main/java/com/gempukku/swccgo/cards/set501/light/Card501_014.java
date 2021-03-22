package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInLostPileEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardsEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Rebel
 * Title: Daughter Of Skywalker (V)
 */
public class Card501_014 extends AbstractRebel {
    public Card501_014() {
        super(Side.LIGHT, 1, 4, 4, 5, 7, Title.Daughter_Of_Skywalker, Uniqueness.UNIQUE);
        setLore("Scout. Leader. Made friends with Wicket. Negotiated an alliance with the Ewoks. Leia found out the truth about her father from Luke in the Ewok village.");
        setGameText("If alone (or with Luke or an Ewok) on Endor during opponent's draw phase, may retrieve 1 Force. If you are about to lose Force, may place X cards stacked on I Feel The Conflict in owner's Lost Pile to reduce your Force loss by X. Immune to attrition (< 4 if not on Endor).");
        addPersona(Persona.LEIA);
        addIcons(Icon.ENDOR, Icon.VIRTUAL_SET_0, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT, Keyword.LEADER, Keyword.FEMALE);
        setVirtualSuffix(true);
        setTestingText("Daughter Of Skywalker (V) (ERRATA)");
        excludeFromDeckBuilder();
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new NotCondition(new OnCondition(self, Title.Endor)), 4));
        modifiers.add(new ImmuneToAttritionModifier(self, new OnCondition(self, Title.Endor)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)
                && GameConditions.isOnSystem(game, self, Title.Endor)
                && (GameConditions.isAlone(game, self) || GameConditions.isWith(game, self, Filters.or(Filters.Luke, Filters.Ewok)))
                && GameConditions.hasLostPile(game, playerId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendEffect(new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();


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
                        new ChooseStackedCardsEffect(action, playerId, ifeeltheconflict, 1,  Filters.countStacked(game, Filters.stackedOn(ifeeltheconflict)), Filters.any, true) {
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
