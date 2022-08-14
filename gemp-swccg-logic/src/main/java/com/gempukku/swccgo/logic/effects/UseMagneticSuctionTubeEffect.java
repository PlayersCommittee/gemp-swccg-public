package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.MagneticSuctionTubeAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An effect that uses the specified Magnetic Suction Tube.
 */
public class UseMagneticSuctionTubeEffect extends AbstractSubActionEffect {
    private PhysicalCard _magneticSuctionTube;

    /**
     * Creates an effect to use the specified magnetic suction tube
     * @param action the action performing this effect
     * @param magneticSuctionTube the magnetic suction tube to use
     */
    public UseMagneticSuctionTubeEffect(Action action, PhysicalCard magneticSuctionTube) {
        super(action);
        _magneticSuctionTube = magneticSuctionTube;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }


    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        final String playerId = subAction.getPerformingPlayer();
        final MagneticSuctionTubeAction magneticSuctionTubeAction = _magneticSuctionTube.getBlueprint().getMagneticSuctionTubeAction(game, _magneticSuctionTube);

        // Choose target(s)
        subAction.appendTargeting(
                new TargetCardOnTableEffect(subAction, playerId, "Choose character", magneticSuctionTubeAction.getPossibleTargets()) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                        subAction.addAnimationGroup(character);
                        // Allow response(s)
                        subAction.allowResponses("'Suck up' " + GameUtils.getCardLink(character),
                                new UnrespondableEffect(subAction) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        subAction.appendEffect(
                                                new DrawDestinyEffect(subAction, playerId) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(character);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        final GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                            return;
                                                        }

                                                        float ability = game.getModifiersQuerying().getAbility(game.getGameState(), character);
                                                        gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                        if (totalDestiny > ability) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            final Filter siteFilter = Filters.and(Filters.interior_site, Filters.siteOfStarshipOrVehicle(_magneticSuctionTube.getAttachedTo()), Filters.locationCanBeRelocatedTo(character, false, false, true, 0, false));

                                                            boolean isSandcrawlerSiteOnTable = false;
                                                            for(PhysicalCard location : gameState.getTopLocations()){
                                                                if (siteFilter.accepts(game, location)){
                                                                    isSandcrawlerSiteOnTable = true;
                                                                    break;
                                                                }
                                                            }

                                                            if (isSandcrawlerSiteOnTable) {
                                                                subAction.appendEffect(
                                                                        new PlayoutDecisionEffect(subAction, playerId,
                                                                                new MultipleChoiceAwaitingDecision("Choose where to relocate " + GameUtils.getCardLink(character), new String[]{"Sandcrawler site", "Used Pile"}) {
                                                                                    @Override
                                                                                    protected void validDecisionMade(int index, String result) {
                                                                                        if (index == 0) {
                                                                                            subAction.appendEffect(
                                                                                                    new ChooseCardOnTableEffect(subAction, playerId, "Choose Sandcrawler site", siteFilter) {
                                                                                                        @Override
                                                                                                        protected void cardSelected(PhysicalCard site) {
                                                                                                            gameState.sendMessage(playerId + " chooses to relocate " + GameUtils.getCardLink(character) + " to " + GameUtils.getCardLink(site));
                                                                                                            gameState.cardAffectsCard(playerId, _magneticSuctionTube, character);
                                                                                                            gameState.cardAffectsCard(playerId, _magneticSuctionTube, site);
                                                                                                            subAction.appendEffect(
                                                                                                                    new RelocateBetweenLocationsEffect(subAction, character, site));
                                                                                                        }
                                                                                                    }
                                                                                            );
                                                                                        }
                                                                                        else {
                                                                                            gameState.sendMessage(playerId + " chooses to place " + GameUtils.getCardLink(character) + " in Used Pile");
                                                                                            subAction.appendEffect(
                                                                                                    new PlaceCardInUsedPileFromTableEffect(subAction, character));
                                                                                        }
                                                                                    }
                                                                                }
                                                                        )
                                                                );
                                                            }
                                                            else {
                                                                subAction.appendEffect(
                                                                        new PlaceCardInUsedPileFromTableEffect(subAction, character));
                                                            }
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
