package com.gempukku.swccgo.cards.set501.light;


import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Rebel
 * Title: Corporal Pao
 */
public class Card501_091 extends AbstractRebel {
    public Card501_091() {
        super(Side.LIGHT, 2, 2, 2, 2, 3, "Corporal Pao", Uniqueness.UNIQUE);
        setLore("Drabatan trooper.");
        setGameText("Power +1 while with Melshi or Sefla. During battle, may add X (limit 3) to a just drawn battle destiny, where X = number of your spies out of play. If just lost from a site, may draw cards from Reserve Deck until you have up to six cards in hand.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.TROOPER);
        setSpecies(Species.DRABATAN);
        setTestingText("Corporal Pao");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, new WithCondition(self, Filters.or(Filters.Melshi, Filters.Sefla)), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s) for just lost response
        if (TriggerConditions.justLostFromLocation(game, effectResult, self, Filters.site)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.numCardsInHand(game, playerId) < 6) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw cards from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId) {
                        @Override
                        protected void cardDrawnIntoHand(PhysicalCard cardDrawn) {
                            checkToDrawAnotherCard(game, action, playerId);
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s) for during battle response
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)) {

            //Find spies out of play
            Collection<PhysicalCard> outOfPlaySpies = Filters.filterCount(game.getGameState().getAllOutOfPlayCards(), game, 3, Filters.and(Filters.your(playerId), Filters.spy));
            int numSpiesOutOfPlay = outOfPlaySpies.size();

            if (numSpiesOutOfPlay > 0) {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Add " + numSpiesOutOfPlay + " to destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new ModifyDestinyEffect(action, numSpiesOutOfPlay));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    /**
     * Check if another card should be drawn from Reserve Deck.
     *
     * @param game     the game
     * @param action   the action
     * @param playerId the player
     */
    private void checkToDrawAnotherCard(final SwccgGame game, final OptionalGameTextTriggerAction action, final String playerId) {
        if (GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.numCardsInHand(game, playerId) < 6) {
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new YesNoDecision("Do you want to draw another card from Reserve Deck?") {
                                @Override
                                protected void yes() {
                                    action.appendEffect(
                                            new DrawCardIntoHandFromReserveDeckEffect(action, playerId) {
                                                @Override
                                                protected void cardDrawnIntoHand(PhysicalCard cardDrawn) {
                                                    checkToDrawAnotherCard(game, action, playerId);
                                                }
                                            }
                                    );
                                }
                            }
                    )
            );
        }
    }
}
