package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DifferentCardTitlesParticipatingInBattleCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceDestinyCardOutOfPlayEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Objective
 * Title: Invasion / In Complete Control
 */
public class Card14_113_BACK extends AbstractObjective {
    public Card14_113_BACK() {
        super(Side.DARK, 7, Title.In_Complete_Control);
        setGameText("While this side up, you lose no more than 2 Force to any Force drain. Your battle droids deploy -1. If you have two [Presence] droids with different card titles (or a [Presence] droid and a Neimoidian) in battle at a site, draw two battle destiny if unable to otherwise. Your droid starfighters are immune to attrition < 3. If opponent just drew an Interrupt for battle destiny, once per battle you may place that card out of play. Flip this card if opponent controls Naboo system or Theed Palace Throne Room.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, 2, playerId));
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.your(self), Filters.battle_droid), -1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, Filters.your(self), new AndCondition(new DuringBattleAtCondition(Filters.site),
                new OrCondition(new DifferentCardTitlesParticipatingInBattleCondition(Filters.and(Filters.your(self), Icon.PRESENCE, Filters.droid)),
                        new AndCondition(new DuringBattleWithParticipantCondition(Filters.and(Filters.your(self), Icon.PRESENCE, Filters.droid)),
                                         new DuringBattleWithParticipantCondition(Filters.and(Filters.your(self), Filters.Neimoidian))))), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.droid_starfighter), 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.isDestinyCardMatchTo(game, Filters.Interrupt)
                && GameConditions.canPlaceDestinyCardOutOfPlay(game)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place battle destiny card out of play");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PlaceDestinyCardOutOfPlayEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controls(game, opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.or(Filters.Naboo_system, Filters.Theed_Palace_Throne_Room))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}