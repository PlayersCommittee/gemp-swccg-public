package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PayExtraCostToFireWeaponEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collection;

/**
 * An action for firing a single weapon.
 */
public class FireSingleWeaponAction extends AbstractFireWeaponAction {
    private PhysicalCard _sourceCard;
    private boolean _weaponFiringStarted;
    private boolean _extraCostAdded;
    private boolean _recordedWeaponUsed;
    private boolean _weaponFired;
    private boolean _recordedWeaponFired;
    private boolean _emitFiredWeaponResult;
    private boolean _weaponFiringEnded;
    private boolean _checkedToFireRepeatedly;
    private Filter _targetedAsCharacter;
    private Float _defenseValueAsCharacter;
    private boolean _ignorePerAttackOrBattleLimit;
    private boolean _thrown;

    /**
     * Creates an action for firing a single weapon.
     *
     * @param sourceCard                      the card to initiate the firing
     * @param weaponOrCardWithPermanentWeapon the weapon (or card with a permanent weapon)
     * @param permanentWeapon                 the permanent weapon built-in (or null if not a permanent weapon)
     * @param repeatedFiring                  true if a repeated firing, otherwise false
     * @param targetedAsCharacter             filter for cards may be targeted as characters
     * @param defenseValueAsCharacter         defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter              the filter for where the card can be played
     * @param ignorePerAttackOrBattleLimit    true if per attack/battle firing limit is ignored, otherwise false
     * @param thrown                          true if the weapon was 'thrown'
     */
    public FireSingleWeaponAction(PhysicalCard sourceCard, PhysicalCard weaponOrCardWithPermanentWeapon, SwccgBuiltInCardBlueprint permanentWeapon, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit, boolean thrown) {
        super(weaponOrCardWithPermanentWeapon, permanentWeapon, repeatedFiring, fireAtTargetFilter);
        _sourceCard = sourceCard;
        if (permanentWeapon != null) {
            setCardFiringWeapon(weaponOrCardWithPermanentWeapon);
        }
        _targetedAsCharacter = targetedAsCharacter;
        _defenseValueAsCharacter = defenseValueAsCharacter;
        _ignorePerAttackOrBattleLimit = ignorePerAttackOrBattleLimit;
        _thrown = thrown;
    }

    /**
     * Creates an action for firing a single weapon.
     * @param sourceCard the card to initiate the firing
     * @param weaponOrCardWithPermanentWeapon the weapon (or card with a permanent weapon)
     * @param permanentWeapon the permanent weapon built-in (or null if not a permanent weapon)
     * @param repeatedFiring true if a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     */
    public FireSingleWeaponAction(PhysicalCard sourceCard, PhysicalCard weaponOrCardWithPermanentWeapon, SwccgBuiltInCardBlueprint permanentWeapon, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        super(weaponOrCardWithPermanentWeapon, permanentWeapon, repeatedFiring, fireAtTargetFilter);
        _sourceCard = sourceCard;
        if (permanentWeapon != null) {
            setCardFiringWeapon(weaponOrCardWithPermanentWeapon);
        }
        _targetedAsCharacter = targetedAsCharacter;
        _defenseValueAsCharacter = defenseValueAsCharacter;
        _ignorePerAttackOrBattleLimit = ignorePerAttackOrBattleLimit;
        _thrown = false;
    }

    @Override
    public PhysicalCard getActionSource() {
        return _sourceCard;
    }

    @Override
    public Effect nextEffect(final SwccgGame game) {
        // Send message that action is being initiated
        if ((isChoosingTargetsComplete() || _respondableEffect != null)  && !_sentInitiationMessage) {
            _sentInitiationMessage = true;
            if (_weaponToFire != null && _weaponToFire.getZone().isInPlay()) {
                game.getGameState().activatedCard(_weaponToFire.getOwner(), _weaponToFire);
            }
            if (_initiationMessage != null && _weaponToFire != null && !_weaponToFire.getZone().isFaceDown()) {
                game.getGameState().sendMessage(_initiationMessage);
            }
        }

        // Start weapon firing state info
        if (!_weaponFiringStarted) {
            _weaponFiringStarted = true;
            game.getGameState().beginWeaponFiring(_weaponToFire, _permanentWeapon);
            if (getCardFiringWeapon() == null &&
                    !_weaponToFire.getBlueprint().isFiredByCharacterPresentOrHere() && !Filters.artillery_weapon.accepts(game, _weaponToFire)) {
                setCardFiringWeapon(_weaponToFire.getAttachedTo());
            }
        }

        // Add any extra cost to fire the weapon
        if (!_extraCostAdded) {
            _extraCostAdded = true;
            appendBeforeCost(new PayExtraCostToFireWeaponEffect(this, _weaponToFire, _permanentWeapon));
        }

        // Verify no costs have failed
        if (!isAnyCostFailed()) {
            // Initiation
            // 1a) Check limits
            // 1b) Choose targets
            // 1c) Pay costs
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            // Record the weapon as used
            if (!_recordedWeaponUsed) {
                _recordedWeaponUsed = true;
                game.getModifiersQuerying().weaponUsedBy(getCardFiringWeapon(), _weaponToFire);
                game.getGameState().getWeaponFiringState().setCardFiringWeapon(getCardFiringWeapon());
                if (game.getGameState().isDuringAttack()) {
                    if (_permanentWeapon != null)
                        game.getModifiersQuerying().firedInAttack(_permanentWeapon, getCardFiringWeapon(), false);
                    else
                        game.getModifiersQuerying().firedInAttack(_weaponToFire, getCardFiringWeapon(), false);
                }
                if (game.getGameState().isDuringBattle()) {
                    if (_permanentWeapon != null)
                        game.getModifiersQuerying().firedInBattle(_permanentWeapon, getCardFiringWeapon(), false);
                    else
                        game.getModifiersQuerying().firedInBattle(_weaponToFire, getCardFiringWeapon(), false);
                }
                if (game.getGameState().isDuringAttackRun()) {
                    if (_permanentWeapon != null)
                        game.getModifiersQuerying().firedInAttackRun(_permanentWeapon, getCardFiringWeapon(), false);
                    else
                        game.getModifiersQuerying().firedInAttackRun(_weaponToFire, getCardFiringWeapon(), false);
                }

            }

            // Responses
            // 2) Perform a RespondableEffect to allow other responses to the action
            if (!_weaponFired) {
                _weaponFired = true;

                // Send message and show animation of targets now that action initiation is complete
                if (_actionMessage != null) {
                    game.getGameState().sendMessage(_actionMessage);
                }

                // Assert that RespondableEffect is set (which is required)
                if (_respondableEffect == null) {
                    if (_weaponToFire != null)
                        throw new UnsupportedOperationException(GameUtils.getFullName(_physicalCard) + " does not have RespondableEffect set");
                    else
                        throw new UnsupportedOperationException("No RespondableEffect set");
                }

                // Animate the card groups in order
                if (_weaponToFire != null) {
                    for (Collection<PhysicalCard> cardsToAnimate : _animationGroupList) {
                        game.getGameState().cardAffectsCards(getPerformingPlayer(), _weaponToFire, cardsToAnimate);
                    }
                }

                // Perform the RespondableEffect if one was provided
                if (_respondableEffect != null) {
                    game.getGameState().getWeaponFiringState().setWeaponFiringEffect(_respondableEffect);
                    return _respondableEffect;
                }
            }

            if (_respondableEffect == null || !_respondableEffect.isCanceled()) {

                // Record the weapon as being fired during attack or battle (unless this is a repeated firing)
                if (!_repeatedFiring && !_recordedWeaponFired) {
                    _recordedWeaponFired = true;
                    if (game.getGameState().isDuringAttack()) {
                        if (_permanentWeapon != null)
                            game.getModifiersQuerying().firedInAttack(_permanentWeapon, getCardFiringWeapon(), true);
                        else
                            game.getModifiersQuerying().firedInAttack(_weaponToFire, getCardFiringWeapon(), true);
                    }
                    if (game.getGameState().isDuringBattle()) {
                        if (_permanentWeapon != null)
                            game.getModifiersQuerying().firedInBattle(_permanentWeapon, getCardFiringWeapon(), true);
                        else
                            game.getModifiersQuerying().firedInBattle(_weaponToFire, getCardFiringWeapon(), true);
                    }
                    if (game.getGameState().isDuringAttackRun()) {
                        if (_permanentWeapon != null)
                            game.getModifiersQuerying().firedInAttackRun(_permanentWeapon, getCardFiringWeapon(), true);
                        else
                            game.getModifiersQuerying().firedInAttackRun(_weaponToFire, getCardFiringWeapon(), true);
                    }
                }

                // Result
                // 3) Carry out the results of the action
                Effect effect = getNextEffect();
                if (effect != null)
                    return effect;

                // Emit effect result that weapon was fired
                if (!_emitFiredWeaponResult) {
                    _emitFiredWeaponResult = true;
                    game.getActionsEnvironment().emitEffectResult(new FiredWeaponResult(game, _permanentWeapon != null ? null : _weaponToFire, _permanentWeapon, getCardFiringWeapon(), _thrown));
                }
            }
        }

        // Finish the weapon firing state
        if (_weaponFiringStarted && !_weaponFiringEnded) {
            _weaponFiringEnded = true;
            appendAfterEffect(
                    new PassthruEffect(this) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            game.getGameState().finishWeaponFiring();
                        }
                    }
            );
        }

        // If weapon firing completed, check if weapon can repeatedly fire.
        if (!_checkedToFireRepeatedly && wasCarriedOut()) {
            _checkedToFireRepeatedly = true;

            final String playerId = getPerformingPlayer();
            if (game.getModifiersQuerying().mayFireWeaponRepeatedly(game.getGameState(), _weaponToFire)) {
                final FireWeaponAction fireWeaponAction = _weaponToFire.getBlueprint().getFireWeaponAction(playerId, game, _weaponToFire, false, 0, _sourceCard, true, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
                if (fireWeaponAction != null) {
                    appendAfterEffect(
                            new PlayoutDecisionEffect(this, playerId,
                                    new YesNoDecision("Do you want to repeatedly fire " + GameUtils.getCardLink(_weaponToFire) + " again?") {
                                        @Override
                                        protected void yes() {
                                            game.getActionsEnvironment().addActionToStack(fireWeaponAction);
                                        }
                                        @Override
                                        protected void no() {
                                            game.getGameState().sendMessage(playerId + " chooses to not repeatedly fire " + GameUtils.getCardLink(_weaponToFire) + " again");
                                        }
                                    }));
                }
            }
        }

        return null;
    }
}
