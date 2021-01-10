package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
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
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An effect that fires the specified weapon (or permanent weapon).
 */
public class UseTractorBeamEffect extends AbstractSubActionEffect {
    private PhysicalCard _tractorBeam;
    private boolean _forFree;


    /**
     * Creates an effect to use the specified tractor beam
     * @param action the action performing this effect
     * @param tractorBeam the tractor beam to use
     * @param forFree true if free, otherwise false
     */
    public UseTractorBeamEffect(Action action, PhysicalCard tractorBeam, boolean forFree) {
        super(action);
        _tractorBeam = tractorBeam;
        _forFree = forFree;
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

        subAction.appendTargeting(new TargetCardOnTableEffect(subAction, playerId, "Target with tractor beam", targetFilter) {
            @Override
            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                int forceCost = tractorBeamAction.getForceCost();
                if (!_forFree && !tractorBeamAction.getForFree())
                    subAction.appendCost(new UseForceEffect(subAction, playerId, forceCost));

                subAction.addAnimationGroup(targetedCard);

                subAction.allowResponses("Targeting "+GameUtils.getCardLink(targetedCard)+" with "+GameUtils.getCardLink(_tractorBeam),
                        new RespondableEffect(subAction) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        int numDestinies = tractorBeamAction.getNumDestinies();

                        subAction.appendEffect(new PassthruEffect(subAction) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                game.getGameState().beginUsingTractorBeam(_tractorBeam);
                                game.getGameState().getUsingTractorBeamState().setTarget(targetedCard);
                            }
                        });
                        subAction.appendEffect(new DrawDestinyEffect(subAction, playerId, numDestinies, DestinyType.TRACTOR_BEAM_DESTINY) {
                            @Override
                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                return Collections.singletonList(targetedCard);
                            }
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                //TODO have this use Statistic.DEFENSE_VALUE
                                float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), targetedCard);
                                if (totalDestiny != null && totalDestiny > defenseValue) {
                                    //success
                                    game.getGameState().sendMessage("Success");
                                    // Perform result(s)
                                    subAction.appendEffect(
                                            new CaptureStarshipEffect(subAction, targetedCard, _tractorBeam)
                                    );
                                } else {
                                    //failed
                                    game.getGameState().sendMessage("Fail");
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
