package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SubtractFromOpponentsAttritionEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardFromVoidOutOfPlayEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
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
        setGameText("Once per game, may use 1 Force to shuffle opponent’s Reserve Deck or place opponent’s just-played Interrupt out of play. " +
                    "If in battle with Anakin or Luke, may draw one destiny and subtract that amount from opponent’s attrition.");
        addPersona(Persona.R2D2);
        addKeywords(Keyword.SPY);
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.VIRTUAL_SET_19, Icon.NAV_COMPUTER, Icon.JABBAS_PALACE);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ARTOO__SHUFFLE_OR_PLACE_INTERRUPT_OUT_OF_PLAY;
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
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.ARTOO__SHUFFLE_OR_PLACE_INTERRUPT_OUT_OF_PLAY;
        String opponent = game.getOpponent(playerId);

        if (GameConditions.hasReserveDeck(game, opponent)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);

            action.setText("Shuffle opponent's Reserve Deck");
            action.setActionMsg("Shuffle opponent's Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect (
                    new ShufflePileEffect(action, opponent, Zone.RESERVE_DECK));

            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isInBattleWith(game, self, Filters.or(Filters.Luke,Filters.Anakin))
                && GameConditions.canDrawDestiny(game, playerId)) {
            final BattleState battleState = game.getGameState().getBattleState();
            if (battleState.hasAttritionTotal(game.getOpponent(playerId))) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Reduce opponent's attrition");
                // Perform result(s)
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId, 1, DestinyType.DESTINY_TO_REDUCE_ATTRITION) {
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                if (totalDestiny != null && totalDestiny > 0) {
                                    action.appendEffect(
                                            new SubtractFromOpponentsAttritionEffect(action, totalDestiny));
                                }
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
