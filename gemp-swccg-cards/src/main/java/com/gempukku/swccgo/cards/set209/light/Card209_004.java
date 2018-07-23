package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToDrawDestinyCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Rebel
 * Title: Galen Erso
 */
public class Card209_004 extends AbstractRebel {
    public Card209_004() {
        super(Side.LIGHT, 3, 3, 3 , 3, 4, "Galen Erso", Uniqueness.UNIQUE);
        setLore("Information broker, leader, and spy.");
        setGameText("Once per game, may place a non-[Immune to Alter] Effect in owner’s Used Pile. If both players just drew one battle destiny here, may use 2 Force (free if with an Imperial) to switch numbers. Opponent must first lose 2 Force to fire a superlaser.");
        addIcons(Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.INFORMATION_BROKER, Keyword.SPY, Keyword.LEADER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.GALEN_ERSO__PLACE_EFFECT_IN_USED_PILE;
        Filter targetFilter = Filters.and(Filters.Effect, Filters.not(Filters.immune_to_Alter));

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Effect in Used Pile");
            action.setActionMsg("Place a non-[Immune to Alter] Effect in owner’s Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Effect", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " in Used Pile",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyDrawingJustCompletedForBothPlayers(game, effectResult)
                && GameConditions.didBothPlayersDrawOneBattleDestiny(game)
                && GameConditions.canUseForce(game, playerId, GameConditions.isWith(game, self, Filters.Imperial) ? 0 : 2)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Switch battle destiny numbers");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, GameConditions.isWith(game, self, Filters.Imperial) ? 0 : 2));
            // Allow response(s)
            action.allowResponses(
                    new UnrespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new SwitchBattleDestinyNumbersEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isFiringWeapon(game, effect, opponent, Filters.superlaser_weapon)
                || (TriggerConditions.isPlayingCard(game, effect, Filters.Commence_Primary_Ignition))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Lose 2 Force to fire superlaser");
            action.setPerformingPlayer(opponent);
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 2));
            return Collections.singletonList(action);
        }
        return null;
    }




}
