package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CaptureOption;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.actions.TractorBeamAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.*;
import com.gempukku.swccgo.logic.timing.results.CaptureStarshipResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An effect that fires the specified weapon (or permanent weapon).
 */
public class UseTractorBeamEffect extends AbstractSubActionEffect {
    private PhysicalCard _tractorBeam;
    private boolean _forFree;
    private Filter _filterForModifierDuringEffect;
    private Modifier _modifierToApplyDuringEffect;
    private List<Modifier> _modifiersToApplyIfUnsuccessful;
    private PhysicalCard _preSelectedTarget;

    /**
     * Creates an effect to use the specified tractor beam
     * @param action the action performing this effect
     * @param tractorBeam the tractor beam to use
     */
    public UseTractorBeamEffect(Action action, PhysicalCard tractorBeam) {
        this(action, tractorBeam, false);
    }

    /**
     * Creates an effect to use the specified tractor beam
     * @param action the action performing this effect
     * @param tractorBeam the tractor beam to use
     * @param forFree true if free, otherwise false
     */
    public UseTractorBeamEffect(Action action, PhysicalCard tractorBeam, boolean forFree) {
        this(action, tractorBeam, forFree, Filters.none, null, null, null);
    }

    public UseTractorBeamEffect(Action action, PhysicalCard tractorBeam, boolean forFree, Filter filterForModifierDuringEffect, Modifier modifierToApplyDuringEffect, List<Modifier> modifiersToApplyIfUnsuccessful, PhysicalCard preSelectedTarget) {
        super(action);
        _tractorBeam = tractorBeam;
        _forFree = forFree;
        _filterForModifierDuringEffect = filterForModifierDuringEffect;
        _modifierToApplyDuringEffect = modifierToApplyDuringEffect;
        _modifiersToApplyIfUnsuccessful = modifiersToApplyIfUnsuccessful;
        _preSelectedTarget = preSelectedTarget;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }


    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        final String playerId = subAction.getPerformingPlayer();
        final TractorBeamAction tractorBeamAction = _tractorBeam.getBlueprint().getTractorBeamAction(game, _tractorBeam);

        Filter targetFilter = tractorBeamAction.getPossibleTargets();
        if (_preSelectedTarget != null && targetFilter.accepts(game, _preSelectedTarget)) {
            targetFilter = Filters.and(targetFilter, _preSelectedTarget);
        }

        TargetingReason targetingReason = TargetingReason.TO_BE_CAPTURED;

       subAction.appendTargeting(new TargetCardOnTableEffect(subAction, playerId, "Target with tractor beam", targetingReason, targetFilter) {
            @Override
            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                int forceCost = tractorBeamAction.getForceCost();
                if (!_forFree && !tractorBeamAction.getForFree())
                    subAction.appendCost(new UseForceEffect(subAction, playerId, forceCost));

                subAction.addAnimationGroup(targetedCard);



                subAction.allowResponses("Targeting "+GameUtils.getCardLink(targetedCard)+" with "+GameUtils.getCardLink(_tractorBeam),
                        new RespondableEffect(subAction) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        int numDestinies = tractorBeamAction.getNumDestinies();

                        final PhysicalCard target = subAction.getPrimaryTargetCard(targetGroupId);

                        subAction.appendEffect(new PassthruEffect(subAction) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {

                                game.getGameState().beginUsingTractorBeam(_tractorBeam);
                                game.getGameState().getUsingTractorBeamState().setTarget(target);

                                if (_filterForModifierDuringEffect != null && _modifierToApplyDuringEffect != null
                                    &&_filterForModifierDuringEffect.accepts(game.getGameState(), game.getModifiersQuerying(), target)) {
                                        game.getModifiersEnvironment().addUntilEndOfTractorBeamModifier(_modifierToApplyDuringEffect);
                                }
                            }
                        });
                        subAction.appendEffect(new DrawDestinyEffect(subAction, playerId, numDestinies, DestinyType.TRACTOR_BEAM_DESTINY) {
                            @Override
                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                return Collections.singletonList(target);
                            }
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                final PhysicalCard target = subAction.getPrimaryTargetCard(targetGroupId);

                                //TODO have this check which Statistic it should compare to (right now always Statistic.DEFENSE_VALUE)
                                float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), target);

                                game.getGameState().sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                game.getGameState().sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));

                                if (totalDestiny != null && totalDestiny > defenseValue) {
                                    //success
                                    game.getGameState().sendMessage("Result: Succeeded");
                                    // Perform result(s)
                                    subAction.appendEffect(
                                            new CaptureStarshipEffect(subAction, target, _tractorBeam)
                                    );
                                } else {
                                    //failed
                                    game.getGameState().sendMessage("Result: Failed");

                                    if (_modifiersToApplyIfUnsuccessful!=null) {
                                        for (Modifier m : _modifiersToApplyIfUnsuccessful) {
                                            m.setAffectFilter(Filters.and(target));
                                            game.getModifiersEnvironment().addUntilEndOfBattleModifier(m);
                                        }
                                    }
                                }
                            }
                        });
                        subAction.appendEffect(new PassthruEffect(subAction) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                game.getGameState().finishUsingTractorBeam();
                            }
                        });

                    }
                });


            }
        });



        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
