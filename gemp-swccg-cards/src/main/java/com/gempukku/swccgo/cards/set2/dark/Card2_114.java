package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Device
 * Title: Magnetic Suction Tube
 */
public class Card2_114 extends AbstractDevice {
    public Card2_114() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Magnetic_Suction_Tube);
        setLore("'Slurp.'");
        setGameText("Deploy on your Sandcrawler. Once during each of your control phases, may target one character present. Draw destiny. If destiny > character's ability, 'suck up' character (relocate to related interior Sandcrawler site or owner's Used Pile).");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.sandcrawler);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.sandcrawler;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter targetFilter = Filters.and(Filters.character, Filters.present(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseDevice(game, self)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Suck up' character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                            action.addAnimationGroup(character);
                            // Allow response(s)
                            action.allowResponses("'Suck up' " + GameUtils.getCardLink(character),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
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
                                                                final Filter siteFilter = Filters.and(Filters.interior_site, Filters.siteOfStarshipOrVehicle(self.getAttachedTo()), Filters.locationCanBeRelocatedTo(character, false, false, true, 0, false));
                                                                if (GameConditions.canSpotLocation(game, siteFilter)) {
                                                                    action.appendEffect(
                                                                            new PlayoutDecisionEffect(action, playerId,
                                                                                    new MultipleChoiceAwaitingDecision("Choose where to relocate " + GameUtils.getCardLink(character), new String[]{"Sandcrawler site", "Used Pile"}) {
                                                                                        @Override
                                                                                        protected void validDecisionMade(int index, String result) {
                                                                                            if (index == 0) {
                                                                                                action.appendEffect(
                                                                                                        new ChooseCardOnTableEffect(action, playerId, "Choose Sandcrawler site", siteFilter) {
                                                                                                            @Override
                                                                                                            protected void cardSelected(PhysicalCard site) {
                                                                                                                gameState.sendMessage(playerId + " chooses to relocate " + GameUtils.getCardLink(character) + " to " + GameUtils.getCardLink(site));
                                                                                                                gameState.cardAffectsCard(playerId, self, character);
                                                                                                                gameState.cardAffectsCard(playerId, self, site);
                                                                                                                action.appendEffect(
                                                                                                                        new RelocateBetweenLocationsEffect(action, character, site));
                                                                                                            }
                                                                                                        }
                                                                                                );
                                                                                            }
                                                                                            else {
                                                                                                gameState.sendMessage(playerId + " chooses to place " + GameUtils.getCardLink(character) + " in Used Pile");
                                                                                                action.appendEffect(
                                                                                                        new PlaceCardInUsedPileFromTableEffect(action, character));
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            )
                                                                    );
                                                                }
                                                                else {
                                                                    action.appendEffect(
                                                                            new PlaceCardInUsedPileFromTableEffect(action, character));
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
            return Collections.singletonList(action);
        }
        return null;
    }
}