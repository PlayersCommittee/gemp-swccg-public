package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.PersonaReplacedCharacterResult;

import java.util.Collection;


/**
 * The effect to persona replace a character with another character of the same persona.
 */
public class PersonaReplaceCharacterEffect extends AbstractSubActionEffect {
    private PhysicalCard _oldCharacter;
    private PhysicalCard _newCharacter;

    /**
     * Create an effect to persona replace a character with another character of the same persona.
     * @param action the action performing this effect
     * @param oldCharacter the character to be replaced
     * @param newCharacter the character to replace the old character
     */
    public PersonaReplaceCharacterEffect(Action action, PhysicalCard oldCharacter, PhysicalCard newCharacter) {
        super(action);
        _oldCharacter = oldCharacter;
        _newCharacter = newCharacter;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final String performingPlayerId = _action.getPerformingPlayer();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        boolean wasUndercover = _oldCharacter.isUndercover();

                        Collection<PhysicalCard> cardsToPlaceInLostPile = gameState.replaceCharacterOnTable(_oldCharacter, _newCharacter);
                        gameState.sendMessage(performingPlayerId + " persona replaces " + GameUtils.getCardLink(_oldCharacter) + " with " + GameUtils.getCardLink(_newCharacter) + (!wasUndercover && _newCharacter.isUndercover() ? " as 'undercover'" : ""));
                        if (!cardsToPlaceInLostPile.isEmpty()) {
                            subAction.appendEffect(
                                    new PutCardsInCardPileEffect(subAction, game, cardsToPlaceInLostPile, Zone.LOST_PILE));
                        }
                        subAction.appendEffect(
                                new TriggeringResultEffect(subAction, new PersonaReplacedCharacterResult(performingPlayerId, _oldCharacter, _newCharacter)));
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
