package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * Subtype: Imperial
 * Title: Officer Valin Hess
 */
public class Card217_017 extends AbstractImperial {
    public Card217_017() {
        super(Side.DARK, 2, 3, 3, 3, 5, "Officer Valin Hess", Uniqueness.UNIQUE);
        setLore("Leader.");
        setGameText("During battle, may place an Imperial of ability < 4 from your Lost Pile out of play to add X to attrition against opponent, where X = that character's ability. Except during battle, may lose Valin Hess to target an opponent's Undercover spy here; target is lost.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_17);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OFFICER_VALIN_HESS__PLACE_IMPERIAL_OUT_OF_PLAY;

        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place an Imperial of ability < 4 out play");
            action.appendUsage(
                    new OncePerBattleEffect(action)
            );
            action.appendCost(
                    new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, playerId, Filters.and(Filters.Imperial, Filters.abilityLessThan(4)), false) {
                        @Override
                        protected void cardPlacedOutOfPlay(PhysicalCard character) {
                            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), character);
                            action.appendEffect(
                                    new AddToAttritionEffect(action, game.getOpponent(playerId), ability));
                        }
                    });
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();

        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.undercover_spy, Filters.here(self));

        if (!GameConditions.isInBattle(game, self)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, TargetingReason.TO_BE_LOST, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Target opponent's undercover spy to be lost");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target undercover spy", SpotOverride.INCLUDE_UNDERCOVER, TargetingReason.TO_BE_LOST, targetFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.appendCost(
                                    new LoseCardFromTableEffect(action, self));
                            // Allow response(s)
                            action.allowResponses(GameUtils.getCardLink(targetedCard) + " is lost",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, targetedCard));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}

