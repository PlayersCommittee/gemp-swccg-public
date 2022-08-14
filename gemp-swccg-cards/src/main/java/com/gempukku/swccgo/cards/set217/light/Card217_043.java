package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToPlaceCardOutOfPlayFromOffTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToPlaceCardOutOfPlayFromTableResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Defensive Shield
 * Title: Ounee Ta (V)
 */
public class Card217_043 extends AbstractDefensiveShield {
    public Card217_043() {
        super(Side.LIGHT, Title.Ounee_Ta);
        setVirtualSuffix(true);
        setLore("Jabba's decadent behavior makes him susceptible to deception. Leia and Lando exploited this weakness, posing as Jabba's kind of scum.");
        setGameText("Plays on table. At each opponent's <> site, your Rebels are each deploy -2 and your Force generation is +1. May lose 3 Force to cancel an attempt by [Theed Palace] Sidious to place your Jedi out of play.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter opponentsGenericSite = Filters.and(Filters.opponents(self), Filters.generic_site, Filters.canBeTargetedBy(self));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.Rebel), -2, opponentsGenericSite));
        modifiers.add(new ForceGenerationModifier(self, opponentsGenericSite, 1, playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        Filter yourJedi = Filters.and(Filters.your(self), Filters.Jedi);
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, opponent, yourJedi)) {
            final AboutToPlaceCardOutOfPlayFromTableResult result = (AboutToPlaceCardOutOfPlayFromTableResult) effectResult;
            final PhysicalCard card = result.getCardToBePlacedOutOfPlay();
            final PhysicalCard source = result.getSourceCard();

            if (source != null
                    && Filters.and(Icon.THEED_PALACE, Filters.Sidious).accepts(game, source)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Cancel attempt to place Jedi out of play");
                action.setActionMsg("Cancel attempt by " + GameUtils.getCardLink(source) + " to place " + GameUtils.getCardLink(card) + " out of play");
                action.appendCost(new LoseForceEffect(action, playerId, 3));

                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Perform result(s)
                        action.addAnimationGroup(source);
                        result.getPreventableCardEffect().preventEffectOnCard(card);
                        game.getGameState().sendMessage(playerId +  " cancels attempt by " + GameUtils.getCardLink(source) + " to place " + GameUtils.getCardLink(card) + " out of play");
                    }
                });
                actions.add(action);
            }
        }

        // Check condition(s)
        if (TriggerConditions.isAboutToBePlacedOutOfPlayFromOffTable(game, effectResult, opponent, yourJedi)) {
            final AboutToPlaceCardOutOfPlayFromOffTableResult result = (AboutToPlaceCardOutOfPlayFromOffTableResult) effectResult;
            final PhysicalCard card = result.getCardToBePlacedOutOfPlay();
            final PhysicalCard source = result.getSourceCard();

            if (source != null
                    && Filters.and(Icon.THEED_PALACE, Filters.Sidious).accepts(game, source)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Cancel attempt to place Jedi out of play");
                action.setActionMsg("Cancel attempt by " + GameUtils.getCardLink(source) + " to place " + GameUtils.getCardLink(card) + " out of play");
                action.appendCost(new LoseForceEffect(action, playerId, 3));

                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Perform result(s)
                        action.addAnimationGroup(source);
                        result.getPreventableCardEffect().preventEffectOnCard(card);
                        game.getGameState().sendMessage(playerId +  " cancels attempt by " + GameUtils.getCardLink(source) + " to place " + GameUtils.getCardLink(card) + " out of play");
                    }
                });
                actions.add(action);
            }
        }

        return actions;
    }
}