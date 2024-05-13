package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;


/**
 * An effect that pays the cost of relocating a card between locations.
 */
public class PayRelocateBetweenLocationsCostEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Collection<PhysicalCard> _cardsToMove;
    private PhysicalCard _moveTo;
    private float _baseCost;

    /**
     * Creates an effect that pays the cost relocating a card between locations.
     * @param action the action performing this effect
     * @param playerId the player to pay the cost
     * @param cardToMove the card to move
     * @param moveTo the card to move to
     * @param baseCost the base cost (as defined by the card performing the relocation)
     */
    public PayRelocateBetweenLocationsCostEffect(Action action, String playerId, PhysicalCard cardToMove, PhysicalCard moveTo, float baseCost) {
        super(action);
        _playerId = playerId;
        _cardsToMove = Collections.singletonList(cardToMove);
        _moveTo = moveTo;
        _baseCost = baseCost;
    }

    /**
     * Creates an effect that pays the cost relocating a card between locations.
     * @param action the action performing this effect
     * @param playerId the player to pay the cost
     * @param cardsToMove the cards to move
     * @param moveTo the card to move to
     * @param baseCost the base cost (as defined by the card performing the relocation)
     */
    public PayRelocateBetweenLocationsCostEffect(Action action, String playerId, Collection<PhysicalCard> cardsToMove, PhysicalCard moveTo, float baseCost) {
        super(action);
        _playerId = playerId;
        _cardsToMove = cardsToMove;
        _moveTo = moveTo;
        _baseCost = baseCost;
    }

    @Override
    public String getText(SwccgGame game) {
        float moveCost = getMoveCost(game);
        return "Use " + GuiUtils.formatAsString(moveCost) + " Force";
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        float moveCost = getMoveCost(game);
        return moveCost <= game.getModifiersQuerying().getForceAvailableToUse(game.getGameState(), _playerId);
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        float moveCost = getMoveCost(game);
        if (moveCost > 0) {
            subAction.appendEffect(new UseForceEffect(subAction, _playerId, moveCost));
        }

        return subAction;
    }

    /**
     * Gets the cost to move.
     * @param game the game
     * @return the cost to move
     */
    private float getMoveCost(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        PhysicalCard moveFromLocation = modifiersQuerying.getLocationHere(gameState, _cardsToMove.iterator().next());
        PhysicalCard moveToLocation = modifiersQuerying.getLocationHere(gameState, _moveTo);

        return modifiersQuerying.getRelocateBetweenLocationsCost(gameState, _cardsToMove, moveFromLocation, moveToLocation, _baseCost);
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
