package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.AttackEffect;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerBySideEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.SnapshotData;

import java.util.Collection;

/**
 * The action to initiate an attack on a creature.
 */
public class InitiateAttackCreatureAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _location;
    private boolean _ownerChosen;
    private String _owner;
    private boolean _creatureChosen;
    private PhysicalCard _creature;
    private boolean _attackInitiated;

    /**
     * Needed to generate snapshot.
     */
    public InitiateAttackCreatureAction() {
    }

    @Override
    public void generateSnapshot(Action selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        InitiateAttackCreatureAction snapshot = (InitiateAttackCreatureAction) selfSnapshot;

        snapshot._location = snapshotData.getDataForSnapshot(_location);
        snapshot._ownerChosen = _ownerChosen;
        snapshot._owner = _owner;
        snapshot._creatureChosen = _creatureChosen;
        snapshot._creature = snapshotData.getDataForSnapshot(_creature);
        snapshot._attackInitiated = _attackInitiated;
    }

    /**
     * Creates an action to initiate an attack on a creature at the specified location.
     * @param playerId the player initiating the attack on creature
     * @param location the location
     */
    public InitiateAttackCreatureAction(String playerId, final PhysicalCard location) {
        super(location, playerId);
        _location = location;
    }

    @Override
    public String getText() {
        return "Initiate attack on creature";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        final String performingPlayer = getPerformingPlayer();

        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_ownerChosen) {
                _ownerChosen = true;

                Collection<PhysicalCard> possibleCreaturesToAttack = Filters.filterActive(game, null, Filters.creatureAtLocationCanBeAttackedByPlayer(performingPlayer, _location));
                if (!Filters.canSpot(possibleCreaturesToAttack, game, Filters.your(performingPlayer))) {
                    _owner = game.getOpponent(performingPlayer);
                }
                else if (!Filters.canSpot(possibleCreaturesToAttack, game, Filters.opponents(performingPlayer))) {
                    _owner = performingPlayer;
                }
                else {
                    appendCost(new ChoosePlayerBySideEffect(this, performingPlayer) {
                        @Override
                        protected String getChoiceText() {
                            return "Choose whose creature to attack";
                        }
                        @Override
                        protected void playerChosen(SwccgGame game, final String playerChosen) {
                            _owner = playerChosen;
                       }
                    });
                    return getNextCost();
                }
            }

            if (!_creatureChosen) {
                _creatureChosen = true;

                Collection<PhysicalCard> possibleCreaturesToAttack = Filters.filterActive(game, null, Filters.creatureAtLocationCanBeAttackedByPlayer(performingPlayer, _location));
                _creature = GameUtils.getRandomCards(Filters.filter(possibleCreaturesToAttack, game, Filters.owner(_owner)), 1).get(0);
            }

            if (!_attackInitiated) {
                _attackInitiated = true;

                return new AttackEffect(this, _creature);
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }


    @Override
    public boolean wasActionCarriedOut() {
        return _attackInitiated;
    }
}
