package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractAction;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

public class FireWeaponAsCombinedAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _weaponToFire;
    private PhysicalCard _cardFiringWeapon;
    private TargetingEffect _chooseTargetEffect;
    private boolean _targetChosen;
    private TargetingEffect _targetTargetEffect;
    private boolean _targetTargeted;
    private PhysicalCard _target;
    private int _timesToFire;
    private int _timesFired;
    private boolean _totalWeaponDestinyCalculated;
    private int _totalWeaponDestiny;
    private int _numDestiniesDrawn;
    private boolean _weaponCompleted;
    private AbstractAction _that;
    private boolean _cancelAction;

    public FireWeaponAsCombinedAction(final SwccgGame game, PhysicalCard weaponToFire, PhysicalCard cardFiringWeapon, Filter filter, int timesToFire) {
        super(weaponToFire, weaponToFire.getOwner());

        _that = this;
        _weaponToFire = weaponToFire;
        _cardFiringWeapon = cardFiringWeapon;
        setPerformingPlayer(weaponToFire.getOwner());
        _timesToFire = timesToFire;

        /*

        _chooseTargetEffect =
                new TargetActiveCardEffect(_that, weaponToFire, weaponToFire.getOwner(), "Choose target to fire at", false, TargetingReason.BEFORE_WEAPON_TARGETING, true, filter) {
                    @Override
                    protected void cardTargeted(SwccgGame game, PhysicalCard target) {
                        if (target==null) {
                            _cancelAction = true;
                            return;
                        }

                        game.getGameState().getWeaponFiringState().setTarget(target);
                        _target = target;
                        game.getGameState().sendMessage(_weaponToFire.getOwner() + " targets to fire " + GameUtils.getCardLink(_weaponToFire) + " at " + GameUtils.getCardLink(_target) + " as part of combined firing");
                        _targetTargetEffect =
                                new TargetActiveCardEffect(_that, _weaponToFire, _weaponToFire.getOwner(), "Target with weapon", _weaponToFire.getBlueprint().getTargetingReasonWhenFiring(game, _weaponToFire), true, target) {
                                    @Override
                                    protected void cardTargeted(SwccgGame game, PhysicalCard target) {
                                        if (target==null) {
                                            _cancelAction = true;
                                            return;
                                        }

                                        game.getGameState().getWeaponFiringState().setTarget(target);
                                        _target = target;
                                    }
                                };
                    }
                };
                */
    }

    @Override
    public PhysicalCard getActionSource() {
        return _weaponToFire;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public Effect nextEffect(final SwccgGame game) {
            /*
        if (!_cancelAction) {


            // Start weapon firing state info
            if (!game.getGameState().isDuringWeaponFiring()) {
                game.getGameState().beginCombinedWeaponFiring();
                game.getGameState().getWeaponFiringState().addCardFiring(_weaponToFire);
            }

            if (!_targetChosen) {
                _targetChosen = true;

                game.getModifiersQuerying().weaponUsedBy(_cardFiringWeapon, _weaponToFire);
                game.getGameState().activatedCard(_weaponToFire.getOwner(), _weaponToFire);
                return _chooseTargetEffect;
            }

            if (!isAnyCostFailed()) {
                Effect cost = getNextCost();
                if (cost != null)
                    return cost;

                if (!_targetTargeted) {
                    _targetTargeted = true;

                    // Mark weapon as been "fired"
                    if (game.getGameState().isDuringBattle())
                        game.getModifiersQuerying().firedInBattle(_weaponToFire);

                    // Actually "target" the target card. This is were opponent can respond to the weapon targeting.
                    return _targetTargetEffect;
                }

                if (_timesFired < _timesToFire) {
                    _timesFired++;
                    return new StackActionEffect(
                           new FireWeaponDuringCombinedFiringAction(game, _weaponToFire, _cardFiringWeapon, _target) {
                                @Override
                                protected void weaponDestinyDrawComplete(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Integer> destinyDrawValues, Integer totalDestiny) {
                                    if (totalDestiny!=null) {
                                        _numDestiniesDrawn++;
                                        _totalWeaponDestiny += totalDestiny;
                                    }
                                }
                            });
                }

                Effect effect = getNextEffect();
                if (effect != null)
                    return effect;

                // Get the total weapon destiny
                if (!_totalWeaponDestinyCalculated) {
                    _totalWeaponDestinyCalculated = true;
                    if (_numDestiniesDrawn>0) {
                        _totalWeaponDestiny = game.getModifiersQuerying().getTotalWeaponDestinyForCombinedFiring(game.getGameState(), _weaponToFire.getOwner(), _target, _totalWeaponDestiny);
                        game.getGameState().sendMessage(_weaponToFire.getOwner() + "'s total weapon destiny is " + _totalWeaponDestiny);
                    }
                }

                if (!_weaponCompleted) {
                    _weaponCompleted = true;
                    return new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (_numDestiniesDrawn>0)
                                _weaponToFire.getBlueprint().weaponDestinyDrawComplete(game, _that, _weaponToFire, _target, _cardFiringWeapon, null, null, _totalWeaponDestiny);
                            game.getActionsEnvironment().emitEffectResult(new FiredWeaponResult(_weaponToFire, _cardFiringWeapon, _target, false));
                        }
                    };
                }
            }
        }


        // End weapon firing state info
        ((DefaultActionsEnvironment) game.getActionsEnvironment()).removeEndOfWeaponFiringActionProxies();
        ((ModifiersLogic) game.getModifiersEnvironment()).removeEndOfWeaponFiring();
        game.getGameState().finishWeaponFiring();

        */

        return null;
    }


    @Override
    public boolean wasActionCarriedOut() {
        return _weaponCompleted;
    }
}
