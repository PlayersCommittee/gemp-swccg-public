package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfForcePileAndReserveDeckAndReturnCardsToOnePileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionLimitedToModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Objective
 * Title: The Galaxy May Need A Legend / We Need Luke Skywalker
 */
public class Card211_036_BACK extends AbstractObjective {
    public Card211_036_BACK() {
        super(Side.LIGHT, 7, Title.We_Need_Luke_Skywalker, ExpansionSet.SET_11, Rarity.V);
        setGameText("Immediately place Luke out of play (ignore [Death Star II] objective restrictions, if any). For remainder of battle, weapons may not be fired. " +
                "While this side up, opponent's immunity to attrition is limited to < 6. Once during your turn, may peek at the top card of your Force Pile and Reserve Deck; place both cards (in any order) on top of one of those piles. Where you have two unique (•) Resistance characters, your Force drains are +1 and, once per turn during battle, may cancel an opponent's just drawn destiny to cause a redraw.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.EPISODE_VII);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        final String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            //Immune To BHBM. See Card9_151. (Line 80)
            action.setText("Place Luke out of play.");
            action.setActionMsg("Place Luke out of play.");

            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, Filters.findFirstActive(game, self, Filters.Luke)));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotBeFiredModifier(self, Filters.any), "Prevents weapons from being fired")
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId1 = GameTextActionId.THE_GALAXY_MAY_NEED_A_LEGEND_FORCE_PILE_UPLOAD;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId1)
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId1)
                && self.getWhileInPlayData() == null) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId1);
            action.setText("Take card into hand from Force Pile");
            action.setActionMsg("Take a card into hand from Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromForcePileEffect(action, playerId, true));
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setWhileInPlayData(new WhileInPlayData(true));
                        }
                    });
            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId2)
                && GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.hasForcePile(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId2);
            action.setText("Peek at top card of Force Pile and Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new PeekAtTopCardOfForcePileAndReserveDeckAndReturnCardsToOnePileEffect(action, playerId)
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();

        Filter uniqueResistanceCharacter = Filters.and(Filters.unique, Filters.Resistance_character, Filters.your(self));
        modifiers.add(new ImmunityToAttritionLimitedToModifier(self, Filters.opponents(self), 6));
        modifiers.add(new ForceDrainModifier(self, Filters.sameLocationAs(self, Filters.and(uniqueResistanceCharacter, Filters.with(self, uniqueResistanceCharacter))), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if ((TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId)))
                && GameConditions.isOncePerTurn(game, self, playerId,gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattle(game)
                && Filters.countActive(game, self, Filters.and(Filters.unique, Filters.Resistance_character, Filters.participatingInBattle)) >= 2
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel destiny and cause re-draw");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            actions.add(action);
        }

        return actions;
    }
}