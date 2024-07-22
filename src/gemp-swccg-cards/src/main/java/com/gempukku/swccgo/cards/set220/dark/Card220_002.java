package com.gempukku.swccgo.cards.set220.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasStackedCondition;
import com.gempukku.swccgo.cards.effects.takeandputcards.StackCardsFromHandEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutRandomCardsFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.ShuffleUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardsIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 20
 * Type: Defensive Shield
 * Title: Drop! (V)
 */
public class Card220_002 extends AbstractDefensiveShield {
    public Card220_002() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Drop, ExpansionSet.SET_20, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Plays on table. At end of a turn, if opponent has 15 or more cards in hand, may use 3 Force to shuffle all but 9 (random selection) into Used Pile. If Grimtaash or Thrown Back just targeted your hand, may reveal 3 cards from hand; they cannot be removed or checked for duplicates.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)
                && GameConditions.numCardsInHand(game, opponent) >= 15
                && GameConditions.canUseForce(game, playerId, 3)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Remove cards from opponent's hand");
            action.setActionMsg("Shuffle all but 9 random cards from opponent's hand into opponent's Used Pile");

            action.appendCost(
                    new UseForceEffect(action, playerId, 3));

            // Perform result(s)
            action.appendEffect(
                    new PutRandomCardsFromHandOnUsedPileEffect(action, playerId, opponent, 9));
            action.appendEffect(
                    new ShuffleUsedPileEffect(action, self, opponent));

            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.or(Filters.Grimtaash, Filters.title(Title.Thrown_Back)), new HasStackedCondition(self, Filters.any), ModifyGameTextType.REMOVE_THREE_MORE_CARDS));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        final String opponent = game.getOpponent(playerId);

        // protect 3 cards
        if (GameConditions.numCardsInHand(game, playerId) >= 3
                && (TriggerConditions.isAboutToPlaceRandomCardsFromOpponentsHandOnUsedPile(game, effect, opponent, Filters.or(Filters.Grimtaash, Filters.title(Title.Thrown_Back)))
                || TriggerConditions.isAboutToLookAtOpponentsHand(game, effect, opponent, Filters.or(Filters.Grimtaash, Filters.title(Title.Thrown_Back))))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Protect three cards in hand");

            action.appendEffect(
                    new StackCardsFromHandEffect(action, playerId, 3, 3, self, false));

            // add an effect to the existing action to return the cards to hand
            effect.getAction().appendAfterEffect(new TakeStackedCardsIntoHandEffect(action, self.getOwner(), 3, 3, self, Filters.any));

            actions.add(action);
        }

        return actions;
    }
}
