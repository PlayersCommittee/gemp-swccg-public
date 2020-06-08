package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.LostForceResult;

import java.util.Collections;

/**
 * An effect that causes the specified unit of Force to be lost.
 */
public class LoseOneForceEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private int _amountLostSoFar;
    private boolean _isBattleDamage;
    private boolean _isFromForceDrain;
    private PhysicalCard _stackFaceDownOn;
    private boolean _asLiberationCard;

    /**
     * Creates an effect that causes the specified unit of Force to be lost.
     * @param action the action performing this effect
     * @param card the unit of Force to lose
     * @param amountLostSoFar the amount of Force lost during the current lose Force process (including this Force),
     *                        or 0 if this Force loss is from battle damage
     * @param isBattleDamage true if the Force is lost to battle damage, otherwise, false
     * @param isFromForceDrain true if the Force is lost from Force Drain, otherwise, false
     * @param stackFaceDownOn card that lost Force is instead stacked face down on, otherwise null
     * @param asLiberationCard the card lost as Force is stacked as a liberation card
     */
    public LoseOneForceEffect(Action action, PhysicalCard card, int amountLostSoFar, boolean isBattleDamage, boolean isFromForceDrain, PhysicalCard stackFaceDownOn, boolean asLiberationCard) {
        super(action);
        _card = card;
        _amountLostSoFar = amountLostSoFar;
        _isBattleDamage = isBattleDamage;
        _isFromForceDrain = isFromForceDrain;
        _stackFaceDownOn = stackFaceDownOn;
        _asLiberationCard = asLiberationCard;
    }

    /**
     * Determines if the card is shown if lost from hand.
     * @return true or false
     */
    public boolean isShownIfLostFromHand() {
        return false;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        Zone zone = _card.getZone();
        String playerId = _card.getOwner();
        PhysicalCard sourceCard = (_isBattleDamage || _isFromForceDrain) ? null : _action.getActionSource();

        if (_stackFaceDownOn != null) {
            // Stack the unit of Force face down on specified card
            if (zone == Zone.HAND && isShownIfLostFromHand()) {
                gameState.sendMessage(playerId + " loses a Force, " + GameUtils.getCardLink(_card) + ", from " + zone.getHumanReadable() + " and stacks it face down on " + GameUtils.getCardLink(_stackFaceDownOn));
            }
            else {
                gameState.sendMessage(playerId + " loses a Force from " + zone.getHumanReadable() + " and stacks it face down on " + GameUtils.getCardLink(_stackFaceDownOn));
            }
            gameState.removeCardsFromZone(Collections.singleton(_card));
            _card.setLiberationCard(_asLiberationCard);
            gameState.stackCard(_card, _stackFaceDownOn, true, false, false);
        }
        else {
            // Places the unit of Force in the Lost Pile
            if (game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.LIFE_FORCE_HIDDEN_WHEN_LOST, playerId)
                    && zone.isLifeForce())
                gameState.sendMessage(playerId + " loses a Force from " + zone.getHumanReadable());
            else
                gameState.sendMessage(playerId + " loses a Force, " + GameUtils.getCardLink(_card) + ", from " + zone.getHumanReadable());
            gameState.removeCardsFromZone(Collections.singleton(_card));
            gameState.addCardToTopOfZone(_card, Zone.LOST_PILE, playerId);
        }

        if (_isBattleDamage) {
            int increaseBy = 1;
            if (game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.DROIDS_SATISFY_FORCE_LOSS_UP_TO_THEIR_FORFEIT_VALUE, playerId)
                    && _card.getBlueprint().hasIcon(Icon.DROID) && (zone.isLifeForce() || zone == Zone.HAND)) {
                increaseBy = _card.getBlueprint().getForfeit().intValue();
            }

            BattleState battleState = game.getGameState().getBattleState();
            battleState.increaseBattleDamageSatisfied(playerId, increaseBy);
            battleState.increaseForceLostToBattleDamage(playerId, increaseBy);
            _amountLostSoFar = battleState.getForceLostToBattleDamage(playerId);
        }

        // Emits effect result that unit of Force was just lost
        game.getActionsEnvironment().emitEffectResult(new LostForceResult(sourceCard, _card.getOwner(), _card, zone, _amountLostSoFar, _isFromForceDrain, _isBattleDamage, _stackFaceDownOn));
    }
}
