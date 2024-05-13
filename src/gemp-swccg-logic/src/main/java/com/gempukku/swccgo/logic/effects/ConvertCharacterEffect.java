package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ConvertCharacterResult;

import java.util.List;

/**
 * An effect that converts a character.
 */
public class ConvertCharacterEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _characterToConvert;

    /**
     * Creates an effect that converts a character.
     * @param action the action performing this effect
     * @param characterToConvert the character to convert
     */
    public ConvertCharacterEffect(Action action, PhysicalCard characterToConvert) {
        super(action);
        _characterToConvert = characterToConvert;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        String newOwner = game.getOpponent(_characterToConvert.getOwner());
        String newZoneOwner = game.getOpponent(_characterToConvert.getZoneOwner());
        PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, _characterToConvert);

        List<PhysicalCard> allCardsToChangeOwner = gameState.getAttachedCards(_characterToConvert);
        allCardsToChangeOwner.add(0, _characterToConvert);
        
        String newSide = newOwner.equals(game.getDarkPlayer()) ? "Dark Side" : "Light Side";
        gameState.sendMessage(_action.getPerformingPlayer() + " converts " + GameUtils.getCardLink(_characterToConvert) + " to " + newSide);

        for (PhysicalCard card : allCardsToChangeOwner) {
            card.setOwner(newOwner);
            card.setZoneOwner(newZoneOwner);
        }
        gameState.moveCardToLocation(_characterToConvert, location);
        for (PhysicalCard card : allCardsToChangeOwner) {
            gameState.reapplyAffectingForCard(game, card);
        }
        game.getActionsEnvironment().emitEffectResult(new ConvertCharacterResult(_action.getPerformingPlayer(), _characterToConvert, null));
    }
}
