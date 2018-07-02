package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardFromVoidOutOfPlayEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Character
 * Subtype: Imperial
 * Title: Myn Kyneugh (V)
 */
public class Card208_036 extends AbstractImperial {
    public Card208_036() {
        super(Side.DARK, 4, 3, 4, 3, 5, "Myn Kyneugh", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Royal guard leader. Remembers nothing of his past other than serving his Emperor. Early instructor of Kir Kanos and Carnor Jax.");
        setGameText("When deployed, may play a Defensive Shield from under your Starting Effect (as if from hand). While at opponent's battleground, Force drains here may not be reduced. Once per game, may use 1 Force to place opponent's just-played Interrupt out of play.");
        addIcons(Icon.DEATH_STAR_II, Icon.WARRIOR, Icon.VIRTUAL_SET_8);
        addKeywords(Keyword.ROYAL_GUARD, Keyword.LEADER);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
            if (startingEffect != null) {
                Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
                if (GameConditions.hasStackedCards(game, startingEffect, filter)) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Play a Defensive Shield");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseStackedCardEffect(action, playerId, startingEffect, filter) {
                                @Override
                                protected void cardSelected(PhysicalCard selectedCard) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, Filters.here(self), new AtCondition(self, Filters.and(Filters.opponents(self), Filters.battleground))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MYN_KYNEUGH__PLACE_INTERRUPT_OUT_OF_PLAY;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.opponents(self), Filters.Interrupt))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (GameConditions.interruptCanBePlacedOutOfPlay(game, cardBeingPlayed)
                    && GameConditions.canUseForce(game, playerId, 1)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place " + GameUtils.getFullName(cardBeingPlayed) + " out of play");
                action.setActionMsg("Place " + GameUtils.getCardLink(cardBeingPlayed) + " out of play");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                // Perform result(s)
                action.appendEffect(
                        new PlaceCardFromVoidOutOfPlayEffect(action, cardBeingPlayed));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
