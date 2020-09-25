package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.InPlayDataAsFloatEvaluator;
import com.gempukku.swccgo.cards.evaluators.SubtractEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayUseOpponentsForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Alien
 * Title: R'tic H'weei
 */
public class Card200_089 extends AbstractAlien {
    public Card200_089() {
        super(Side.DARK, 3, 2, 2, 2, 3, "R'tic H'weei", Uniqueness.UNIQUE);
        setLore("Jawa.");
        setGameText("Once per game, may [upload] Jawa Blaster or a card with 'sandcrawler' in title or gametext. Once during opponent’s turn, if there is more than 1 Force in opponent's Force Pile, you may use 1 Force in opponent's Force Pile.");
        addIcons(Icon.TATOOINE, Icon.VIRTUAL_SET_0);
        setSpecies(Species.JAWA);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.RTIC_HWEEL__UPLOAD_JAWA_BLASTER_OR_CARD_WITH_SANDCRAWLER_IN_TITLE_OR_GAMETEXT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Jawa Blaster or a card with 'sandcrawler' in title or gametext into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Jawa_Blaster, Filters.titleContains("sandcrawler"), Filters.gameTextContains("sandcrawler")), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayUseOpponentsForceModifier(self, new PresentAtCondition(self, Filters.and(Filters.Tatooine_location, Filters.battleground)),
                new SubtractEvaluator(1, new InPlayDataAsFloatEvaluator(self)), playerId, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isUsingForce(game, effect, playerId)
                && GameConditions.isOnceDuringOpponentsTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isPresentAt(game, self, Filters.and(Filters.Tatooine_location, Filters.battleground)))  {
            final UseForceEffect useForceEffect = (UseForceEffect) effect;
            final int maxForceToUseViaCard = game.getModifiersQuerying().getMaxOpponentsForceToUseViaCard(game.getGameState(), playerId, self, useForceEffect.getAmountForOpponentToUse(), 0);
            if (maxForceToUseViaCard > 0) {
                final int maxForceToUse = Math.min(1, Math.min(maxForceToUseViaCard, useForceEffect.getTotalAmountOfForceToUse()));
                if (maxForceToUse > 0) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setRepeatableTrigger(true);
                    action.setText("Use 1 Force in opponent's Force Pile");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerTurnEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    useForceEffect.setAmountForOpponentToUse(useForceEffect.getAmountForOpponentToUse() + 1);
                                    self.setWhileInPlayData(new WhileInPlayData(1f));
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
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isUsingForce(game, effect, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isPresentAt(game, self, Filters.and(Filters.Tatooine_location, Filters.battleground)))  {
            final UseForceEffect useForceEffect = (UseForceEffect) effect;
            int minOpponentForceToUse = Math.max(0, useForceEffect.getTotalAmountOfForceToUse() - game.getGameState().getForcePile(playerId).size());
            if (minOpponentForceToUse > 0) {
                final int maxForceToUseViaCard = game.getModifiersQuerying().getMaxOpponentsForceToUseViaCard(game.getGameState(), playerId, self, useForceEffect.getAmountForOpponentToUse(), minOpponentForceToUse);
                if (maxForceToUseViaCard > 0) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setRepeatableTrigger(true);
                    action.setPerformingPlayer(playerId);
                    action.setText("Use 1 Force in opponent's Force Pile");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerTurnEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    useForceEffect.setAmountForOpponentToUse(useForceEffect.getAmountForOpponentToUse() + 1);
                                    self.setWhileInPlayData(new WhileInPlayData(1f));
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
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }
        return null;
    }
}
