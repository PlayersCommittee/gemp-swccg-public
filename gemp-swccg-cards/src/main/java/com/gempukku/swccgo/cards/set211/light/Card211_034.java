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
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

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
            final Filter validCharacter = Filters.and(yourCharacterOfAbilityLessThanFour, Filters.at(relatedExteriorSite));
            int availablePassengerCapacity = game.getGameState().getAvailablePassengerCapacity(game.getModifiersQuerying(), Libertine, self);
            int availablePilotCapacity = game.getGameState().getAvailablePilotCapacity(game.getModifiersQuerying(), Libertine, self);
            if (GameConditions.canSpot(game, self, validCharacter)
                    && (availablePassengerCapacity + availablePilotCapacity > 0)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate your character aboard");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", validCharacter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard characterTargeted) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new MultipleChoiceAwaitingDecision("Choose capacity slot", new String[]{"Pilot", "Passenger"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        final boolean asPilot = (index == 0) ? true : false;
                                                        action.addAnimationGroup(characterTargeted);
                                                        action.addAnimationGroup(Libertine);
                                                        // Pay cost(s)
                                                        action.appendCost(
                                                                new PayRelocateBetweenLocationsCostEffect(action, playerId, characterTargeted, Libertine.getAtLocation(), 0));
                                                        // Allow response(s)
                                                        action.allowResponses("Relocate " + GameUtils.getCardLink(characterTargeted) + " to " + GameUtils.getCardLink(Libertine),
                                                                new UnrespondableEffect(action) {
                                                                    @Override
                                                                    protected void performActionResults(Action targetingAction) {
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
}
