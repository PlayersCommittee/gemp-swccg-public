package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 11
 * Type: Objective
 * Title: The Galaxy May Need A Legend / We Need Luke Skywalker
 */
public class Card211_026_BACK extends AbstractObjective {
    public Card211_026_BACK() {
        super(Side.LIGHT, 7, Title.We_Need_Luke_Skywalker);
        setGameText("Immediately place Luke out of play (ignore [Death Star II] objective restrictions, if any). For remainder of battle, opponent may not fire weapons. \n" +
                "While this side up, opponent's immunity to attrition is limited to < 5. Your Force drains are +1 where you have two unique (•) Resistance characters. Once during your turn, may peek at the top card of your Force Pile and Reserve Deck; place both cards (in any order) on top of one of those piles. Once per turn during battle, may cancel an opponent's just drawn destiny to cause a re-draw.");
        addIcons(Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        final String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Luke out of play.");
            action.setActionMsg("Place Luke out of play.");
            // Perform result(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Place Luke Out of Play", Filters.Luke) {
                        @Override
                        protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                            action.appendEffect(
                                    new PlaceCardOutOfPlayFromTableEffect(action, targetedCard)
                            );
                        }
                    });
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotBeFiredModifier(self, Filters.any), "Prevents all weapons from being fired")
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId1 = GameTextActionId.THE_GALAXY_MAY_NEED_A_LEGEND_FORCE_PILE_UPLOAD;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId1)
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId1);
            action.setText("Take card into hand from Force Pile");
            action.setActionMsg("Take a card into hand from Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromForcePileEffect(action, playerId, true));
            actions.add(action);
        }

        //TODO: Implement Pile Peek

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Filter cardsWithMoreThan5ITA = Filters.and(Filters.opponents(self.getOwner()), Filters.immunityToAttritionLessThan(5));

        modifiers.add(new ImmuneToAttritionLessThanModifier(self, cardsWithMoreThan5ITA, 5));
        modifiers.add(new ForceDrainModifier(self, Filters.any, new ControlsWithCondition(self, self.getOwner(), 2, Filters.any, Filters.and(Filters.unique, Filters.Resistance_character)), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if ((TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId)))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel destiny and cause re-draw");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}