package com.gempukku.swccgo.logic.timing.actions.battle;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseOneForceEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.*;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseOrForfeitDuringDamageSegmentResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An action that carries out the damage segment of a battle.
 */
public class BattleDamageSegmentAction extends SystemQueueAction {

    /**
     * Creates an action that carries out the damage segment of a battle.
     * @param game the game
     */
    public BattleDamageSegmentAction(SwccgGame game) {
        appendEffect(
                new PassthruEffect(this) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        BattleState battleState = gameState.getBattleState();
                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                        // Make sure total attrition is set here, since it cannot change during damage segment
                        battleState.setAttritionTotal(game.getDarkPlayer(), modifiersQuerying.getTotalAttrition(gameState, game.getDarkPlayer()));
                        battleState.setAttritionTotal(game.getLightPlayer(), modifiersQuerying.getTotalAttrition(gameState, game.getLightPlayer()));

                        // Make sure each cards immunity to attrition is set here, since it cannot change during damage segment
                        for (PhysicalCard cardInBattle : battleState.getAllCardsParticipating()) {
                            cardInBattle.setImmunityToAttritionLessThan(modifiersQuerying.getImmunityToAttritionLessThan(gameState, cardInBattle));
                            cardInBattle.setImmunityToAttritionOfExactly(modifiersQuerying.getImmunityToAttritionOfExactly(gameState, cardInBattle));
                        }

                        // Set the damage segment as reached
                        battleState.reachedDamageSegment();
                        game.getModifiersEnvironment().removeReachedDamageSegmentOfBattle();
                    }
                }
        );
        appendEffect(
                new CheckIfDamageSegmentFinishedEffect(this, game.getGameState().getBattleState().getPlayerInitiatedBattle(), false));
    }

    /**
     * A private effect that checks if the damage segment is complete and schedules the next effect.
     */
    private class CheckIfDamageSegmentFinishedEffect extends AbstractSuccessfulEffect {
        private String _playerId;
        private boolean _otherPlayerFinished;

        /**
         * Creates a private effect that checks if the damage segment is complete and schedules the next effect.
         * @param action the action performing this effect
         * @param playerId the player to lose or forfeit cards
         * @param isOtherPlayerFinished true if the other player is done with damage segment actions, otherwise false
         */
        private CheckIfDamageSegmentFinishedEffect(Action action, String playerId, boolean isOtherPlayerFinished) {
            super(action);
            _playerId = playerId;
            _otherPlayerFinished = isOtherPlayerFinished;
        }

        @Override
        protected void doPlayEffect(SwccgGame game) {
            BattleState battleState = game.getGameState().getBattleState();

            // Check if battle damage remaining
            if (battleState.getBattleDamageRemaining(game, _playerId) > 0) {
                appendEffect(new ChooseCardToLoseOrForfeitEffect(_action, _playerId, _otherPlayerFinished));
                return;
            }

            // Check if any cards must be forfeited
            if (Filters.canSpot(game, null, Filters.and(Filters.owner(_playerId),
                    Filters.participatingInBattle, Filters.mustBeForfeited, Filters.mayBeForfeited))) {
                appendEffect(new ChooseCardToLoseOrForfeitEffect(_action, _playerId, _otherPlayerFinished));
                return;
            }

            // Check if attrition remaining
            if (battleState.getAttritionRemaining(game, _playerId) > 0) {
                // Check if any cards can be forfeited
                if (Filters.canSpot(game, null, Filters.and(Filters.owner(_playerId),
                        Filters.participatingInBattle, Filters.mayBeForfeited))) {
                    appendEffect(new ChooseCardToLoseOrForfeitEffect(_action, _playerId, _otherPlayerFinished));
                    return;
                }
            }

            // If we get this far, then check if other player has anything to lose or forfeit
            if (!_otherPlayerFinished) {
                appendEffect(
                        new CheckIfDamageSegmentFinishedEffect(_action, game.getOpponent(_playerId), true));
            }
        }
    }

    /**
     * A private effect that causes the specified player to choose a Force to lose or a card from battle to forfeit and
     * schedules the next effect.
     */
    public class ChooseCardToLoseOrForfeitEffect extends AbstractSubActionEffect {
        private String _text = "Choose Force to lose or a card from battle to forfeit";
        private String _playerId;
        private boolean _alreadyFulfilled;
        private boolean _prevPlayerFinished;
        private ChooseCardToLoseOrForfeitEffect _that;

        /**
         * Creates a private effect that causes the specified player to choose a Force to lose or a card from battle to
         * forfeit and schedules the next effect.
         * @param action the action performing this effect
         * @param playerId the player to lose or forfeit cards
         * @param isPrevPlayerFinished true if the other player is done with damage segment actions, otherwise false
         */
        public ChooseCardToLoseOrForfeitEffect(Action action, String playerId, boolean isPrevPlayerFinished) {
            super(action);
            _playerId = playerId;
            _prevPlayerFinished = isPrevPlayerFinished;
            _that = this;
        }

        @Override
        public String getText(SwccgGame game) {
            return _text;
        }

        /**
         * Gets the player to lose or forfeit cards
         * @return the player
         */
        public String getPlayerId() {
            return _playerId;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        /**
         * Sets the current loss of Force or forfeit card from battle action as already fulfilled by another action.
         * Example: Mantellian Savrip or The Professor can be used to forfeit cards (or reduce battle damage).
         */
        public void setFulfilledByOtherAction() {
            _alreadyFulfilled = true;
        }

        /**
         * Determines if the current loss of Force or forfeit card from battle action was already fulfilled by another action.
         * @return true if it is already fulfilled, otherwise false
         */
        public boolean isFulfilledByOtherAction() {
            return _alreadyFulfilled;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final GameState gameState = game.getGameState();
            final BattleState battleState = gameState.getBattleState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

            final SubAction subAction = new SubAction(_action, _playerId);

            // 1) Automatic and optional responses from "about to lose Force" or "resolving battle damage/attrition".
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            battleState.setCurrentLoseOrForfeitEffect(_that);
                            game.getActionsEnvironment().emitEffectResult(
                                    new AboutToLoseOrForfeitDuringDamageSegmentResult(subAction, _playerId, _that));
                        }
                    }
            );

            // 2) Check if already fulfilled
            // Other cards, such a Mantellian Savrip or The Professor, can be used to forfeit cards (or reduce battle damage)
            // during damage segment battle. If one of those actions was done, then _alreadyFulfilled will be set to true.
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(final SwccgGame game) {
                            battleState.setCurrentLoseOrForfeitEffect(null);
                            if (_alreadyFulfilled) {
                                subAction.appendEffect(
                                        new CheckIfDamageSegmentFinishedEffect(_action, _prevPlayerFinished ? _playerId : game.getOpponent(_playerId), _prevPlayerFinished));
                                return;
                            }

                            // Get the remaining battle damage and attrition.
                            float battleDamageRemaining = battleState.getBattleDamageRemaining(game, _playerId);
                            float attritionRemaining = battleState.getAttritionRemaining(game, _playerId);

                            Collection<PhysicalCard> cardsInBattle = Filters.filterActive(game,
                                    null, Filters.and(Filters.owner(_playerId), Filters.participatingInBattle));
                            // Get cards from battle that can be forfeited to satisfy battle damage and attrition
                            Collection<PhysicalCard> cardsThatMayBeForfeited = Filters.filter(cardsInBattle, game, Filters.mayBeForfeited);

                            Collection<PhysicalCard> cardsThatMayNotBeForfeited = Filters.filter(cardsInBattle, game, Filters.mayNotBeForfeited);
                            // Get any cards that must be forfeited
                            Collection<PhysicalCard> cardsThatMustBeForfeited = Filters.filter(cardsThatMayBeForfeited, game, Filters.mustBeForfeited);

                            Collection<PhysicalCard> eligibleCards = new ArrayList<>();
                            eligibleCards.addAll(cardsThatMayBeForfeited);
                            eligibleCards.addAll(cardsThatMayNotBeForfeited);

                            // Determine if all cards present that can be forfeited are immune to the total attrition.
                            boolean attritionCanBeIgnored = false;
                            if (attritionRemaining > 0) {
                                // Get total attrition
                                float totalAttrition = battleState.getAttritionTotal(game, _playerId);

                                // Immunity to attrition (less than)
                                boolean allHaveSufficentImmunityToAttritionLessThan = true;
                                // Immunity to attrition (exactly)
                                boolean allHaveSufficentImmunityToAttritionOfExactly = true;

                                for (PhysicalCard eligibleCard : eligibleCards) {
                                    // Only check cards present at the battle
                                    if (Filters.wherePresent(eligibleCard).accepts(gameState, modifiersQuerying, battleState.getBattleLocation())) {

                                        float exactImmunity = modifiersQuerying.getImmunityToAttritionOfExactly(gameState, eligibleCard);
                                        if (exactImmunity > 0) {
                                            if (exactImmunity != totalAttrition) {
                                                allHaveSufficentImmunityToAttritionOfExactly = false;
                                                break;
                                            }
                                        }
                                        else {
                                            float immunityToLessThan = modifiersQuerying.getImmunityToAttritionLessThan(gameState, eligibleCard);
                                            if (immunityToLessThan <= totalAttrition) {
                                                allHaveSufficentImmunityToAttritionLessThan = false;
                                                break;
                                            }
                                        }
                                    }
                                }

                                attritionCanBeIgnored = allHaveSufficentImmunityToAttritionOfExactly && allHaveSufficentImmunityToAttritionLessThan;
                            }

                            // If there are any characters that must be forfeited before other characters, then remove other characters.
                            if (Filters.canSpot(cardsThatMayBeForfeited, game, Filters.mustBeForfeitedBeforeOtherCharacters)) {
                                cardsThatMayBeForfeited = Filters.filter(cardsThatMayBeForfeited, game,
                                        Filters.or(Filters.mustBeForfeitedBeforeOtherCharacters, Filters.not(Filters.character)));
                            }
                            if (Filters.canSpot(cardsThatMustBeForfeited, game, Filters.mustBeForfeitedBeforeOtherCharacters)) {
                                cardsThatMustBeForfeited = Filters.filter(cardsThatMustBeForfeited, game,
                                        Filters.or(Filters.mustBeForfeitedBeforeOtherCharacters, Filters.not(Filters.character)));
                            }

                            // This list will contain the cards that can be chosen to be lost or forfeited.
                            final List<PhysicalCard> selectableCards = new ArrayList<PhysicalCard>();
                            boolean isOptionalSelection = false;

                            // Battle damage remaining
                            if (battleDamageRemaining > 0) {

                                // Add cards from Life Force that can be lost to satisfy battle damage
                                selectableCards.addAll(gameState.getHand(_playerId));
                                selectableCards.addAll(gameState.getSabaccHand(_playerId));
                                PhysicalCard topOfReserveDeck = gameState.getTopOfReserveDeck(_playerId);
                                if (topOfReserveDeck != null)
                                    selectableCards.add(topOfReserveDeck);
                                PhysicalCard topOfForcePile = gameState.getTopOfForcePile(_playerId);
                                if (topOfForcePile != null)
                                    selectableCards.add(topOfForcePile);
                                PhysicalCard topOfUsedPile = gameState.getTopOfUsedPile(_playerId);
                                if (topOfUsedPile != null)
                                    selectableCards.add(topOfUsedPile);
                                PhysicalCard topOfUnresolvedDestinyPile = gameState.getTopOfUnresolvedDestinyDraws(_playerId);
                                if (topOfUnresolvedDestinyPile != null)
                                    selectableCards.add(topOfUnresolvedDestinyPile);

                                // Add cards that may be forfeited
                                selectableCards.addAll(cardsThatMayBeForfeited);

                                // Set text and send message
                                _text = "Choose Force to lose or a card from battle to forfeit";
                                if (attritionRemaining > 0 && !attritionCanBeIgnored)
                                    gameState.sendMessage(_playerId + " has " + GuiUtils.formatAsString(attritionRemaining)
                                            + " attrition and " + GuiUtils.formatAsString(battleDamageRemaining) + " battle damage remaining to satisfy");
                                else
                                    gameState.sendMessage(_playerId + " has " + GuiUtils.formatAsString(battleDamageRemaining) + " battle damage remaining to satisfy");

                            }
                            // Attrition remaining (and cannot be ignored)
                            else if (!cardsThatMayBeForfeited.isEmpty()
                                    && attritionRemaining > 0 && !attritionCanBeIgnored) {

                                // Add cards that may be forfeited
                                selectableCards.addAll(cardsThatMayBeForfeited);

                                // Set text and send message
                                _text = "Choose a card from battle to forfeit";
                                gameState.sendMessage(_playerId + " has " + GuiUtils.formatAsString(attritionRemaining) + " attrition remaining to satisfy");

                            }
                            // Cards must be forfeited
                            else if (!cardsThatMustBeForfeited.isEmpty()) {

                                // Add cards that must be forfeited
                                selectableCards.addAll(cardsThatMustBeForfeited);

                                // Set text and send message
                                _text = "Choose a card from battle to forfeit";
                                game.getGameState().sendMessage(_playerId + " has cards remaining that must be forfeited");
                            }
                            // Attrition remaining (and may be ignored)
                            else if (!cardsThatMayBeForfeited.isEmpty()
                                    && attritionRemaining > 0) {

                                isOptionalSelection = true;
                                _text = "Choose a card from battle to forfeit (if desired)";

                                // Add cards that may be forfeited
                                selectableCards.addAll(cardsThatMayBeForfeited);

                                // Set text and send message
                                _text = "Choose a card from battle to forfeit (if desired)";
                                gameState.sendMessage(_playerId + " has " + GuiUtils.formatAsString(attritionRemaining) + " attrition remaining, but all cards are immune");
                            }

                            // If no cards to lose or forfeit, check other player next.
                            if (selectableCards.isEmpty()) {
                                if (!_prevPlayerFinished) {
                                    subAction.appendEffect(
                                            new CheckIfDamageSegmentFinishedEffect(_action, game.getOpponent(_playerId), true));
                                }
                                return;
                            }

                            // Choose card to lose or forfeit
                            chooseCardDecision(game, subAction, selectableCards, isOptionalSelection);
                        }
                    }
            );

            return subAction;
        }

        /**
         * Have the player choose a card to lose or forfeit.
         * @param game the game
         * @param subAction the subAction
         * @param selectableCards the selectable cards
         * @param isOptionalSelection true if selection is optional, otherwise selection is required
         */
        private void chooseCardDecision(final SwccgGame game, final SubAction subAction, final List<PhysicalCard> selectableCards, final boolean isOptionalSelection) {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new CardsSelectionDecision(_text, selectableCards, isOptionalSelection ? 0 : 1, 1) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            List<PhysicalCard> cards = getSelectedCardsByResponse(result);
                            if (cards.isEmpty()) {
                                if (!_prevPlayerFinished) {
                                    subAction.appendEffect(
                                            new CheckIfDamageSegmentFinishedEffect(_action, game.getOpponent(_playerId), true));
                                }
                                return;
                            }
                            final PhysicalCard card = cards.get(0);

                            // Check if selected card was from hand or Life Force to satisfy only battle damage
                            if (card.getZone() == Zone.HAND || card.getZone().isLifeForce()) {

                                // If selected card from hand is Houjix or Ghhhk, then confirm choice.
                                if (card.getZone() == Zone.HAND && Filters.or(Filters.Houjix, Filters.Ghhhk).accepts(game, card)) {

                                    game.getUserFeedback().sendAwaitingDecision(_playerId,
                                            new YesNoDecision("You are choosing to lose " + GameUtils.getCardLink(card) + " as a unit of Force, which is not typically how this card is used. Do you still want to lose it?") {
                                                @Override
                                                protected void yes() {
                                                    // Stack a subAction to lose the Force.
                                                    SubAction loseForceSubAction = new SubAction(subAction);
                                                    loseForceSubAction.appendEffect(
                                                            new LoseOneForceEffect(loseForceSubAction, card, 0, true, false, null, false));
                                                    subAction.stackSubAction(loseForceSubAction);
                                                    
                                                    // Check other player next
                                                    subAction.appendEffect(
                                                            new CheckIfDamageSegmentFinishedEffect(_action, _prevPlayerFinished ? _playerId : game.getOpponent(_playerId), _prevPlayerFinished));
                                                }
                                                @Override
                                                protected void no() {
                                                    chooseCardDecision(game, subAction, selectableCards, isOptionalSelection);
                                                }
                                            });
                                }
                                else {
                                    // Stack a subAction to lose the Force.
                                    SubAction loseForceSubAction = new SubAction(subAction);
                                    loseForceSubAction.appendEffect(
                                            new LoseOneForceEffect(loseForceSubAction, card, 0, true, false, null, false));
                                    subAction.stackSubAction(loseForceSubAction);

                                    // Check other player next
                                    subAction.appendEffect(
                                            new CheckIfDamageSegmentFinishedEffect(_action, _prevPlayerFinished ? _playerId : game.getOpponent(_playerId), _prevPlayerFinished));
                                }
                            }
                            else {
                                // If selected card to forfeit has other cards that can be forfeited aboard, then confirm choice.
                                if (!Filters.creature_vehicle.accepts(game, card)
                                        && !game.getModifiersQuerying().allowsCharactersAboardToJumpOff(game.getGameState(), card)
                                        && Filters.canSpot(selectableCards, game, Filters.attachedToWithRecursiveChecking(card))) {

                                    game.getUserFeedback().sendAwaitingDecision(_playerId,
                                            new YesNoDecision("You are choosing to forfeit " + GameUtils.getCardLink(card) + ", which has other cards aboard that could be forfeited first. Do you still want to forfeit it?") {
                                                @Override
                                                protected void yes() {
                                                    // Forfeit card from battle
                                                    SubAction forfeitCardSubAction = new SubAction(subAction);
                                                    forfeitCardSubAction.appendEffect(
                                                            new ForfeitCardFromTableEffect(forfeitCardSubAction, card));
                                                    subAction.stackSubAction(forfeitCardSubAction);

                                                    // Check other player next
                                                    subAction.appendEffect(
                                                            new CheckIfDamageSegmentFinishedEffect(_action, _prevPlayerFinished ? _playerId : game.getOpponent(_playerId), _prevPlayerFinished));
                                                }
                                                @Override
                                                protected void no() {
                                                    chooseCardDecision(game, subAction, selectableCards, isOptionalSelection);
                                                }
                                            });
                                }
                                else {
                                    // Forfeit card from battle
                                    SubAction forfeitCardSubAction = new SubAction(subAction);
                                    forfeitCardSubAction.appendEffect(
                                            new ForfeitCardFromTableEffect(forfeitCardSubAction, card));
                                    subAction.stackSubAction(forfeitCardSubAction);

                                    // Check other player next
                                    subAction.appendEffect(
                                            new CheckIfDamageSegmentFinishedEffect(_action, _prevPlayerFinished ? _playerId : game.getOpponent(_playerId), _prevPlayerFinished));
                                }
                            }
                        }
                    }
            );
        }

        @Override
        protected boolean wasActionCarriedOut() {
            return true;
        }
    }
}
