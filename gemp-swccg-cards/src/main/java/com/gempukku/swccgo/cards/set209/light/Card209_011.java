package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PreventEffectOnCardEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.effects.HitCardAndResetForfeitEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Resistance
 * Title: Rose Tico
 */
public class Card209_011 extends AbstractResistance {
    public Card209_011() {
        super(Side.LIGHT, 5, 1, 1, 2, 2, "Rose Tico", Uniqueness.UNIQUE, ExpansionSet.SET_9, Rarity.V);
        setLore("Female.");
        setGameText("If Finn is about to be lost from same site, may place him in Used Pile instead. Once during battle, if your starship (or your other Resistance character) here is about to be 'hit' (and Rose is not 'hit'), may cause Rose to be 'hit' (and forfeit = 0) instead.");
        addPersona(Persona.ROSE);
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.FEMALE);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        Filter finnAtSameSite = Filters.and(Filters.Finn, Filters.atSameSite(self));
        if (TriggerConditions.isAboutToBeLost(game, effectResult, finnAtSameSite)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, finnAtSameSite)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardAboutToLeaveTable();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(cardToBeLost) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(cardToBeLost) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, result.getCardAboutToLeaveTable()));
                        }
                    });
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerBattle(game, self, gameTextSourceCardId, gameTextActionId)
                && TriggerConditions.isAboutToBeHit(game, effectResult, Filters.and(Filters.your(self), Filters.here(self), Filters.other(self), Filters.or(Filters.starship, Filters.Resistance_character)))) {

            // need to find a Rose card instead of using self because of Bane Malar
            PhysicalCard rose = Filters.findFirstActive(game, self, Filters.Rose);

            PreventableCardEffect preventableEffect = ((AboutToBeHitResult)effectResult).getPreventableCardEffect();
            PhysicalCard cardToBeHit = ((AboutToBeHitResult)effectResult).getCardToBeHit();

            if (rose != null
                    && !GameConditions.isHit(game, rose)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make Rose hit instead");

                action.appendUsage(
                        new OncePerBattleEffect(action));
                action.appendCost(
                        new HitCardAndResetForfeitEffect(action, self, 0, self, null, null));

                action.appendEffect(
                        new PreventEffectOnCardEffect(action, preventableEffect, cardToBeHit, "Makes "+GameUtils.getCardLink(rose) + " hit instead of "+GameUtils.getCardLink(cardToBeHit)));

                actions.add(action);
            }

        }
        return actions;
    }
}
