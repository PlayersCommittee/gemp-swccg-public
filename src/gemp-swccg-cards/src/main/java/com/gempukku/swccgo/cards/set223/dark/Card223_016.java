package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringWeaponsSegmentWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeLostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 23
 * Type: Character
 * Subtype: Alien
 * Title: Lobot, Lando's Broker
 */
public class Card223_016 extends AbstractAlien {
    public Card223_016() {
        super(Side.DARK, 3, 2, 2, 2, 4, "Lobot, Lando's Broker", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Son of a traveling slaver. Helps run Cloud City with Administrator Lando Calrissian. Speech capability worn away by constant cyborg neural connection.");
        setGameText("Deploys free to Cloud City. " +
                "Your characters of ability < 5 at same Cloud City site are immune to Clash Of Sabers, " +
                "may not be targeted to be lost during the weapons segment of a battle, " +
                "and during your move phase, may use 1 Force to relocate to a related site.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_23);
        addPersona(Persona.LOBOT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Cloud_City_location));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter abilityLTFiveAtSameCCSite = Filters.and(Filters.your(self), Filters.character, Filters.abilityLessThan(5), Filters.atSameLocation(self), Filters.at(Filters.Cloud_City_site));

        modifiers.add(new ImmuneToTitleModifier(self, abilityLTFiveAtSameCCSite, Title.Clash_Of_Sabers));
        modifiers.add(new MayNotTargetToBeLostModifier(self, abilityLTFiveAtSameCCSite, new DuringWeaponsSegmentWithParticipantCondition(abilityLTFiveAtSameCCSite)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // reset the play data when out of move phase
        if (!GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)) {
            self.setWhileInPlayData(null);
        }

        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                && GameConditions.isAtLocation(game, self, Filters.Cloud_City_site)
                && GameConditions.forceAvailableToUse(game, playerId) > 0) {
            // used to keep track of everyone who has relocated this phase
            List<PhysicalCard> charactersAlreadyMovedThisTurn = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCards() : null;
            if (charactersAlreadyMovedThisTurn == null) {
                charactersAlreadyMovedThisTurn = new LinkedList<PhysicalCard>();
            }

            // check all filters
            final Filter yourCharacterOfAbilityLessThanFive = Filters.and(Filters.your(self), Filters.character, Filters.abilityLessThan(5));
            final Filter relatedSite = Filters.relatedSiteTo(self, Filters.here(self));
            final Filter validCharacters = Filters.and(
                    yourCharacterOfAbilityLessThanFive
                    , Filters.canBeRelocated(false)
                    , Filters.atSameLocation(self)
                    , Filters.not(Filters.in(charactersAlreadyMovedThisTurn)));

            if (GameConditions.canSpot(game, self, validCharacters)
                    && GameConditions.canSpot(game, self, relatedSite)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Relocate your character to a related site");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character", validCharacters) {
                            @Override
                            protected void cardSelected(PhysicalCard characterToRelocate) {
                                action.addAnimationGroup(characterToRelocate);
                                action.appendTargeting(
                                new ChooseCardOnTableEffect(action, playerId, "Choose destination", relatedSite) {
                                    @Override protected void cardSelected(PhysicalCard locationToRelocateTo) {
                                        action.addAnimationGroup(locationToRelocateTo);
                                        // Pay cost(s)
                                        action.appendCost(
                                                new PayRelocateBetweenLocationsCostEffect(action, playerId, characterToRelocate, locationToRelocateTo, 1)
                                        );
                                        // Allow response(s)
                                        action.allowResponses("Relocate " + GameUtils.getCardLink(characterToRelocate) + " to " + GameUtils.getCardLink(locationToRelocateTo),
                                                new UnrespondableEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // update all play data (will be reset at end of move phase)
                                                        List<PhysicalCard> charactersAlreadyMovedThisTurn = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCards() : null;
                                                        if (charactersAlreadyMovedThisTurn == null) {
                                                            self.setWhileInPlayData(new WhileInPlayData(new LinkedList<PhysicalCard>()));
                                                            charactersAlreadyMovedThisTurn = self.getWhileInPlayData().getPhysicalCards();
                                                        }
                                                        charactersAlreadyMovedThisTurn.add(characterToRelocate);

                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new RelocateBetweenLocationsEffect(action, characterToRelocate, locationToRelocateTo)
                                                        );
                                                    }
                                                }
                                        );
                                    }
                                });
                            }
                        }
                );
                actions.add(action);
            }
            return actions;
        }
        return null;
    }
}
