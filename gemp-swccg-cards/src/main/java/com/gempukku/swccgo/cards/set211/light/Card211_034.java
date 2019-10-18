package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.RelocateFromLocationToStarshipOrVehicle;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11
 * Type: Starship
 * Subtype: Starfighter
 * Title: Libertine
 */
public class Card211_034 extends AbstractStarfighter {
    private Filter MATCHING_PILOTS = Filters.or(Filters.BB8, Filters.Rose, Filters.DJ);

    public Card211_034() {
        super(Side.LIGHT, 2, 2, 2, null, 4, 5, 5, "Libertine", Uniqueness.UNIQUE);
        setLore("");
        setGameText("May add 2 pilots and 2 passengers. During your deploy phase, may relocate your character of ability < 4 at a related exterior site aboard. While BB-8, DJ, or Rose aboard, immune to attrition < 5.");
        addPersona(Persona.LIBERTINE);
        addIcons(Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.EPISODE_VII, Icon.VIRTUAL_SET_11);
        addModelType(ModelType.DLANSEAUX_STAR_YACHT);
        setPilotCapacity(2);
        setPassengerCapacity(2);
        setMatchingPilotFilter(MATCHING_PILOTS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasAboardCondition(self, MATCHING_PILOTS), 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        final PhysicalCard Libertine = self;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)) {
            final Filter yourCharacterOfAbilityLessThanFour = Filters.and(Filters.your(self), Filters.character, Filters.abilityLessThanOrEqualTo(4));
            final Filter relatedExteriorSite = Filters.and(Filters.exterior_site, Filters.relatedLocationTo(self, Filters.here(Libertine)));
            final Filter validCharacters = Filters.and(
                    yourCharacterOfAbilityLessThanFour
                    , Filters.canBeRelocated(false)
                    , Filters.at(relatedExteriorSite)
                    , Filters.not(Filters.mayNotBoard(Libertine)));
            final int availablePassengerCapacity = game.getGameState().getAvailablePassengerCapacity(game.getModifiersQuerying(), Libertine, self);
            final int availablePilotCapacity = game.getGameState().getAvailablePilotCapacity(game.getModifiersQuerying(), Libertine, self);
            final Filter validCharactersMeetingCapacityRequirements = validCharactersMeetingCapacityRequirements(validCharacters, availablePassengerCapacity, availablePilotCapacity);
            if (GameConditions.canSpot(game, self, validCharactersMeetingCapacityRequirements)
                    && (availablePassengerCapacity + availablePilotCapacity > 0)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate your character aboard");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character", validCharactersMeetingCapacityRequirements) {
                            @Override
                            protected void cardSelected(final PhysicalCard characterTargeted) {
                                ArrayList<String> capacityOptions = new ArrayList<>();
                                if (characterTargeted.getBlueprint().hasIcon(Icon.PILOT) && availablePilotCapacity > 0) {
                                    capacityOptions.add("Pilot");
                                }
                                if (availablePassengerCapacity > 0) {
                                    capacityOptions.add("Passenger");
                                }
                                if (capacityOptions.size() == 0) {
                                    throw new UnsupportedOperationException("No capacity for character chosen to relocate to Libertine.");
                                }
                                action.appendTargeting(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new MultipleChoiceAwaitingDecision("Choose capacity slot", capacityOptions) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        final boolean asPilot = (result == "Pilot") ? true : false;
                                                        // Allow response(s)
                                                        action.allowResponses("Relocate " + GameUtils.getCardLink(characterTargeted) + " to " + GameUtils.getCardLink(Libertine),
                                                                new UnrespondableEffect(action) {
                                                                    @Override
                                                                    protected void performActionResults(Action targetingAction) {
                                                                        action.addAnimationGroup(characterTargeted);
                                                                        action.addAnimationGroup(Libertine);
                                                                        // Pay cost(s)
                                                                        action.appendCost(
                                                                                new PayRelocateBetweenLocationsCostEffect(action, playerId, characterTargeted, Libertine.getAtLocation(), 0));
                                                                        // Perform result(s)
                                                                        action.appendEffect(
                                                                                new RelocateFromLocationToStarshipOrVehicle(action, characterTargeted, Libertine, asPilot, self));
                                                                    }
                                                                });
                                                    }
                                                }
                                        )
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private Filter validCharactersMeetingCapacityRequirements(Filter validCharacters, int passengerCapacity, int pilotCapacity) {
        if (passengerCapacity > 0) {
            return validCharacters;
        } else if (pilotCapacity > 0) {
            return Filters.and(validCharacters, Filters.pilot);
        }
        return Filters.none;
    }
}