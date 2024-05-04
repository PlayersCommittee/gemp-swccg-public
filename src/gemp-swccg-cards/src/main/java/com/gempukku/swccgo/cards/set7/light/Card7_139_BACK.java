package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateForceDrainCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Objective
 * Title: Rescue The Princess / Sometimes I Amaze Even Myself
 */
public class Card7_139_BACK extends AbstractObjective {
    public Card7_139_BACK() {
        super(Side.LIGHT, 7, Title.Sometimes_I_Amaze_Even_Myself, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setGameText("While this side up, for opponent to initiate a Force drain, opponent must use +1 Force. During battle, opponent's unique (•) characters, vehicles and starships lost from table are placed out of play; Leia adds one battle destiny; attrition against opponent is +2; and Imperials, Dark Jedi and Imperial starships lose all immunity to attrition. Flip this card if Leia is captured.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition duringBattle = new DuringBattleCondition();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new InitiateForceDrainCostModifier(self, 1, opponent));
        modifiers.add(new AddsBattleDestinyModifier(self, new DuringBattleWithParticipantCondition(Filters.Leia), 1));
        modifiers.add(new AttritionModifier(self, duringBattle, 2, opponent));
        modifiers.add(new CancelImmunityToAttritionModifier(self, Filters.or(Filters.Imperial, Filters.Dark_Jedi, Filters.Imperial_starship), duringBattle));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String subjugatedPlanet = game.getGameState().getSubjugatedPlanet();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleAt(game, Filters.on(subjugatedPlanet))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.matchingOperativeToSubjugatedPlanet))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.opponents(self), Filters.unique,
                Filters.or(Filters.character, Filters.vehicle, Filters.starship)))
                && GameConditions.isDuringBattle(game)) {
            PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(cardLost) + " out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(cardLost) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromOffTableEffect(action, cardLost));
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.Leia)
                && !GameConditions.hasGameTextModification(game, self, ModifyGameTextType.RESCUE_THE_PRINCESS__CANNOT_BE_PLACED_OUT_OF_PLAY)
                && GameConditions.canBePlacedOutOfPlay(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.captured(game, effectResult, Filters.Leia)
                && GameConditions.canBeFlipped(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}