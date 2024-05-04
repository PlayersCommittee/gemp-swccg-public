package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.AttackEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerBySideEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.results.AttackTargetSelectedResult;
import com.gempukku.swccgo.logic.timing.results.ForceLossInitiatedResult;
import com.gempukku.swccgo.logic.timing.rules.CreatureAttacksNonCreatureRule;

import java.util.Collection;

/**
 * The action to initiate an attack on a non-creature.
 */
public class InitiateAttackNonCreatureAction extends RequiredRuleTriggerAction {
    private PhysicalCard _creature;
    private boolean _ownerChosen;
    private String _owner;
    private boolean _targetChosen;
    private boolean _targetChanged;
    private PhysicalCard _target;
    private boolean _attackInitiated;

    /**
     * Needed to generate snapshot.
     */
    public InitiateAttackNonCreatureAction() {
        super(new CreatureAttacksNonCreatureRule());
    }

    @Override
    public void generateSnapshot(Action selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        InitiateAttackNonCreatureAction snapshot = (InitiateAttackNonCreatureAction) selfSnapshot;

        snapshot._creature = snapshotData.getDataForSnapshot(_creature);
        snapshot._ownerChosen = _ownerChosen;
        snapshot._owner = _owner;
        snapshot._targetChosen = _targetChosen;
        snapshot._target = snapshotData.getDataForSnapshot(_target);
        snapshot._attackInitiated = _attackInitiated;
    }

    /**
     * Creates an action to initiate an attack on a non-creature.
     * @param creature the creature to attack a non-creature
     */
    public InitiateAttackNonCreatureAction(final PhysicalCard creature) {
        super(new CreatureAttacksNonCreatureRule(), creature);
        _creature = creature;
    }

    public void resetTargetSelection() {
        _targetChosen = false;
        _targetChanged = true;
    }

    public void setTarget(PhysicalCard card) {
        _target = card;
        _targetChosen = true;
        _targetChanged = true;
    }

    public boolean isTargetChosen() {
        return _targetChosen;
    }

    public boolean isTargetChanged() {
        return _targetChanged;
    }

    @Override
    public String getText() {
        return "Initiate attack";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        final String performingPlayer = _creature.getOwner();

        // Send message that action is being initiated
        if (!_sentInitiationMessage) {
            _sentInitiationMessage = true;

            gameState.activatedCard(getPerformingPlayer(), _physicalCard);
            if (_initiationMessage != null) {
                gameState.sendMessage(_initiationMessage);
            }
        }

        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_ownerChosen) {
                _ownerChosen = true;

                Collection<PhysicalCard> possibleNonCreaturesToAttack = Filters.filterActive(game, _creature, SpotOverride.INCLUDE_ALL, Filters.nonCreatureCanBeAttackedByCreature(_creature, false));
                if (!Filters.canSpot(possibleNonCreaturesToAttack, game, Filters.your(performingPlayer))) {
                    _owner = game.getOpponent(performingPlayer);
                }
                else if (!Filters.canSpot(possibleNonCreaturesToAttack, game, Filters.opponents(performingPlayer))) {
                    _owner = performingPlayer;
                }
                else {
                    appendCost(new ChoosePlayerBySideEffect(this, performingPlayer) {
                        @Override
                        protected String getChoiceText() {
                            return "Choose whose non-creature to attack";
                        }
                        @Override
                        protected void playerChosen(SwccgGame game, final String playerChosen) {
                            _owner = playerChosen;
                       }
                    });
                    return getNextCost();
                }
            }

            if (!_targetChosen) {
                _targetChosen = true;
                _targetChanged = false;

                Collection<PhysicalCard> possibleNonCreaturesToAttack = Filters.filterActive(game, _creature, SpotOverride.INCLUDE_ALL, Filters.nonCreatureCanBeAttackedByCreature(_creature, false));
                _target = GameUtils.getRandomCards(Filters.filter(possibleNonCreaturesToAttack, game, Filters.owner(_owner)), 1).get(0);
                gameState.sendMessage(GameUtils.getCardLink(_target) + " randomly chosen to be attacked");
                gameState.cardAffectsCard(_creature.getOwner(), _creature, _target);
                return new TriggeringResultEffect(this, new AttackTargetSelectedResult(this, _creature, _target));
            }

            if (!_attackInitiated) {
                _attackInitiated = true;

                return new AttackEffect(this, _target, _physicalCard);
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }
}
