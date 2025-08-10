package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToCrossOverResult;
import com.gempukku.swccgo.logic.timing.results.CrossedOverResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An effect that converts a character.
 */
public class CrossOverCharacterEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private PhysicalCard _characterToCrossOver;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private CrossOverCharacterEffect _that;

    /**
     * Creates an effect that converts a character.
     * @param action the action performing this effect
     * @param characterToCrossOver the character to cross-over
     */
    public CrossOverCharacterEffect(Action action, PhysicalCard characterToCrossOver) {
        super(action);
        _characterToCrossOver = characterToCrossOver;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final String performingPlayerId = _action.getPerformingPlayer();

        final SubAction subAction = new SubAction(_action);

        // 1) Trigger "about to cross-over" for card.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent the card from crossing-over.
        subAction.appendEffect(new TriggeringResultEffect(subAction,
                new AboutToCrossOverResult(_action.getPerformingPlayer(), _characterToCrossOver, _that)));

        // 2) Figure out if card will still be crossed-over
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!isEffectOnCardPrevented(_characterToCrossOver)) {
                            String oldOwner = _characterToCrossOver.getOwner();
                            String newOwner = game.getOpponent(oldOwner);
                            String newSide = newOwner.equals(game.getDarkPlayer()) ? "Dark Side" : "Light Side";

                            // Opponent may not deploy character with persona for remainder of game
                            Persona oldPersona = modifiersQuerying.getPersonas(gameState, _characterToCrossOver).iterator().next();
                            modifiersQuerying.crossedOver(oldPersona);
                            game.getModifiersEnvironment().addUntilEndOfGameModifier(
                                    new MayNotDeployModifier(null, Filters.persona(oldPersona), oldOwner));

                            gameState.sendMessage(performingPlayerId + " crosses " + GameUtils.getCardLink(_characterToCrossOver) + " over to the " + newSide);
                            Collection<PhysicalCard> cardsToPlaceInLostPile = gameState.crossOverCharacterOnTable(_characterToCrossOver);
                            if (!cardsToPlaceInLostPile.isEmpty()) {
                                subAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, cardsToPlaceInLostPile, Zone.LOST_PILE));
                            }
                            subAction.appendEffect(
                                    new TriggeringResultEffect(subAction, new CrossedOverResult(performingPlayerId, _characterToCrossOver)));
                        }
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _preventedCards.isEmpty();
    }

    /**
     * Prevents the specified card from being affected by the effect.
     * @param card the card
     */
    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCards.add(card);
    }

    /**
     * Determines if the specified card was prevented from being affected by the effect.
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return _preventedCards.contains(card);
    }
}
