package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Droid
 * Title: Artoo (V)
 */
public class Card219_028 extends AbstractDroid {
    public Card219_028 () {
        super(Side.LIGHT, 1, 2, 2, 4, "Artoo", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setAlternateDestiny(6);
        setVirtualSuffix(true);
        setLore("Counterpart to C-3PO. Spy. Obstinate, headstrong and always full of surprises. R2-D2 was an integral part of Luke Skywalker's rescue plans.");
        setGameText("When deployed, may play a Defensive Shield from under your Starting Effect (as if from hand). Once per game, may use 1 Force to place opponent's just-played Interrupt out of play. While with Anakin, C-3PO, or Luke, attrition against you here is -2.");
        addPersona(Persona.R2D2);
        addKeywords(Keyword.SPY);
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.VIRTUAL_SET_19, Icon.NAV_COMPUTER, Icon.JABBAS_PALACE);
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
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ARTOO__PLACE_INTERRUPT_OUT_OF_PLAY;
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

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, Filters.here(self), new WithCondition(self, Filters.or(Filters.Anakin, Filters.C3PO, Filters.Luke)), -2, self.getOwner()));
        return modifiers;
    }
}
