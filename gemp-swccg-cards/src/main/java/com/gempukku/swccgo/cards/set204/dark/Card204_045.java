package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Imperial
 * Title: Tarkin (V)
 */
public class Card204_045 extends AbstractImperial {
    public Card204_045() {
        super(Side.DARK, 1, 4, 4, 3, 6, "Tarkin", Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial Governor of the Seswenna Sector. Conceived and implemented the Death Star project. A leader in the effort to crush the Rebellion.");
        setGameText("[Pilot] 2. When deployed, may draw cards from Reserve Deck until you have up to six cards in hand. During battle, if present with two ISB agents, may cancel an opponent's just drawn destiny.");
        addPersona(Persona.TARKIN);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
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
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isPresentWith(game, self, 2, Filters.ISB_agent)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));
            actions.add(action);
        }

        return actions;
    }

    /**
     * Check if another card should be drawn from Reserve Deck.
     * @param game the game
     * @param action the action
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
