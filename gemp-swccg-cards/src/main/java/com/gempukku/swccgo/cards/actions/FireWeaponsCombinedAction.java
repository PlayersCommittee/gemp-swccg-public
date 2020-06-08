package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractAction;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

public class FireWeaponsCombinedAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _sourceCard;
    private PhysicalCard _target;
    private boolean _weaponFireOrderChosen;
    private List<PhysicalCard> _weaponsFiredInOrder = new LinkedList<PhysicalCard>();
    private boolean _totalWeaponDestinyCalculated;
    private int _totalWeaponDestiny;
    private int _numDestiniesDrawn;
    private int _numWeaponsCompleted;
    private AbstractAction _that;
    private boolean _abortAction;

    public FireWeaponsCombinedAction(final SwccgGame game, final PhysicalCard sourceCard, Filter weaponsToFireFilter, PhysicalCard target) {
        super(sourceCard, sourceCard.getOwner());

        /*

        _that = this;
        _sourceCard = sourceCard;
        setPerformingPlayer(sourceCard.getOwner());
        _target = target;

        _chooseWeaponsInOrderEffect =
                new ChooseCardsOneAtATimeEffect(this, sourceCard.getOwner(), "Choose weapons to fire combined in order", Filters.and(weaponsToFireFilter, Filters.canFireAtTarget(target, 0))) {
                    @Override
                    protected void cardChosen(SwccgGame game, final PhysicalCard weapon) {
                        if (weapon==null) {
                            _abortAction = true;
                            return;
                        }

                        game.getModifiersQuerying().weaponUsedBy(weapon.getAttachedTo(), weapon);
                        if (game.getGameState().isDuringBattle())
                            game.getModifiersQuerying().firedInBattle(weapon);

                        game.getGameState().sendMessage(sourceCard.getOwner() + " targets to fire " + GameUtils.getCardLink(weapon) + " at " + GameUtils.getCardLink(_target) + " as part of combined firing");
                        SubAction subAction = new SubAction(_that);
                        subAction.appendEffect(
                                new TargetActiveCardEffect(_that, weapon, weapon.getOwner(), "Target with weapon", weapon.getBlueprint().getTargetingReasonWhenFiring(game, weapon), false, _target) {
                                    @Override
                                    protected void cardTargeted(SwccgGame game, PhysicalCard target) {
                                        if (target==null) {
                                            _abortAction = true;
                                            return;
                                        }

                                        game.getGameState().getWeaponFiringState().setTarget(target);
                                        _target = target;
                                        game.getActionsEnvironment().addActionToStack(
                                                new FireWeaponDuringCombinedFiringAction(game, weapon, weapon.getAttachedTo(), _target) {
                                                    @Override
                                                    protected void weaponDestinyDrawComplete(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Integer> destinyDrawValues, Integer totalDestiny) {
                                                        _weaponsFiredInOrder.add(weapon);
                                                        if (totalDestiny!=null) {
                                                            _numDestiniesDrawn++;
                                                            _totalWeaponDestiny += totalDestiny;
                                                        }
                                                    }
                                                });
                                    }
                                });
                        game.getActionsEnvironment().addActionToStack(subAction);
                    }
                };
                */
    }

    @Override
    public PhysicalCard getActionSource() {
        return _sourceCard;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public Effect nextEffect(final SwccgGame game) {
/*        if (!_abortAction) {

            // Start weapon firing state info
            if (!game.getGameState().isDuringWeaponFiring()) {
                game.getGameState().beginCombinedWeaponFiring();
                game.getGameState().getWeaponFiringState().setTarget(_target);
            }

            if (!isAnyCostFailed()) {
                Effect cost = getNextCost();
                if (cost != null)
                    return cost;

                if (!_weaponFireOrderChosen) {
                    _weaponFireOrderChosen = true;
                    return _chooseWeaponsInOrderEffect;
                }

                Effect effect = getNextEffect();
                if (effect != null)
                    return effect;

                // Get the total weapon destiny
                if (!_totalWeaponDestinyCalculated) {
                    _totalWeaponDestinyCalculated = true;
                    if (_numDestiniesDrawn>0) {
                        _totalWeaponDestiny = game.getModifiersQuerying().getTotalWeaponDestinyForCombinedFiring(game.getGameState(), _sourceCard.getOwner(), _target, _totalWeaponDestiny);
                        game.getGameState().sendMessage(_sourceCard.getOwner() + "'s total weapon destiny is " + _totalWeaponDestiny);
                    }
                }

                if (_numWeaponsCompleted < _weaponsFiredInOrder.size()) {
                    _numWeaponsCompleted++;
                    return new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            PhysicalCard weapon = _weaponsFiredInOrder.get(_numWeaponsCompleted-1);
                            if (_numDestiniesDrawn>0)
                                weapon.getBlueprint().weaponDestinyDrawComplete(game, _that, weapon, _target, null, null, null, _totalWeaponDestiny);
                            game.getActionsEnvironment().emitEffectResult(new FiredWeaponResult(weapon, weapon.getAttachedTo(), _target, false));
                        }
                    };
                }
            }
        }

        // End weapon firing state info
        ((DefaultActionsEnvironment) game.getActionsEnvironment()).removeEndOfBattleActionProxies();
        ((ModifiersLogic) game.getModifiersEnvironment()).removeEndOfWeaponFiring();
        game.getGameState().finishWeaponFiring();
        */
        return null;
    }


    @Override
    public boolean wasActionCarriedOut() {
        return _totalWeaponDestinyCalculated;
    }

}
