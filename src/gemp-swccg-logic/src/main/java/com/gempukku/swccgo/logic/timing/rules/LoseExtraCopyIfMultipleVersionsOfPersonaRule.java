package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Enforces the game rules if a player has more than one copy of a persona on table at a time.
 */
public class LoseExtraCopyIfMultipleVersionsOfPersonaRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates the game rules to have a player place extra copies of a persona in lost pile.
     * @param actionsEnvironment the actions environment
     */
    public LoseExtraCopyIfMultipleVersionsOfPersonaRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult) {
                        // Check condition(s)
                        if (TriggerConditions.isTableChanged(game, effectResult)) {

                            List<TriggerAction> triggerActions = new LinkedList<TriggerAction>();

                            // Check all the cards in play to see if there are overlapping personas in the dark side player's cards
                            final String darkSidePlayerId = game.getDarkPlayer();
                            Filter darkFilter = Filters.and(Filters.owner(darkSidePlayerId), Filters.hasPersona);
                            Collection<PhysicalCard> darkCards = Filters.filterForUniquenessChecking(game, darkFilter);
                            Collection<PhysicalCard> darkToBeLost = new LinkedList<>();
                            for(PhysicalCard card: darkCards) {
                                Set<Persona> personas = game.getModifiersQuerying().getPersonas(game.getGameState(), card);
                                for(Persona persona:personas) {
                                    if (Filters.filter(darkCards, game, Filters.persona(persona)).size()>1) {
                                        darkToBeLost.add(card);
                                    }
                                }
                            }
                            if (!darkToBeLost.isEmpty()) {

                                final RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.setText(darkSidePlayerId + "'s duplicated persona lost");
                                action.setMessage(darkSidePlayerId + " must choose a duplicated persona to be lost");

                                action.appendEffect(
                                        new ChooseCardEffect(action, darkSidePlayerId, "Choose a card with a duplicated persona to be placed in lost pile", darkToBeLost) {
                                            @Override
                                            protected void cardSelected(PhysicalCard selectedCard) {
                                                if (selectedCard.getZone().isInPlay())
                                                    action.appendEffect(new PlaceCardInLostPileFromTableEffect(action, selectedCard, false, Zone.LOST_PILE, true, true));
                                                else if (selectedCard.getZone()==Zone.STACKED)
                                                    action.appendEffect(new PutStackedCardInLostPileEffect(action, darkSidePlayerId, selectedCard, false));
                                            }
                                        }
                                );
                                triggerActions.add(action);
                            }

                            // Check all the cards in play to see if there are overlapping personas in the light side player's cards
                            final String lightSidePlayerId = game.getLightPlayer();
                            Filter lightFilter = Filters.and(Filters.owner(lightSidePlayerId), Filters.hasPersona);
                            Collection<PhysicalCard> lightCards = Filters.filterForUniquenessChecking(game, lightFilter);
                            Collection<PhysicalCard> lightToBeLost = new LinkedList<>();
                            for(PhysicalCard card: lightCards) {
                                Set<Persona> personas = game.getModifiersQuerying().getPersonas(game.getGameState(), card);
                                for(Persona persona:personas) {
                                    if (Filters.filter(lightCards, game, Filters.persona(persona)).size()>1) {
                                        lightToBeLost.add(card);
                                    }
                                }
                            }
                            if (!lightToBeLost.isEmpty()) {

                                final RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.setText(lightSidePlayerId + "'s duplicated persona lost");
                                action.setMessage(lightSidePlayerId + " must choose a duplicated persona to be lost");

                                action.appendEffect(
                                        new ChooseCardEffect(action, lightSidePlayerId, "Choose a card with a duplicated persona to be placed in lost pile", lightToBeLost) {
                                            @Override
                                            protected void cardSelected(PhysicalCard selectedCard) {
                                                if (selectedCard.getZone().isInPlay())
                                                    action.appendEffect(new PlaceCardInLostPileFromTableEffect(action, selectedCard, false, Zone.LOST_PILE, true, true));
                                                else if (selectedCard.getZone()==Zone.STACKED)
                                                    action.appendEffect(new PutStackedCardInLostPileEffect(action, lightSidePlayerId, selectedCard, false));
                                            }
                                        }
                                );
                                triggerActions.add(action);
                            }

                            return triggerActions;
                        }
                        return null;
                    }
                }
        );
    }
}