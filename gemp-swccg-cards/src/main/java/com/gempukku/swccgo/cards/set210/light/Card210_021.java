package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Effect
 * Title: Part Of The Tribe
 */
public class Card210_021 extends AbstractNormalEffect {
    public Card210_021() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, "Part Of The Tribe", Uniqueness.UNIQUE);
        setLore("Wookiees are known to be creatures of great emotion and are very protective of family and friends. Chewbacca has come to treat Luke as a member of his own family.");
        setGameText("Deploy on your non-alien, non-Jedi character. When deployed, choose a species of an alien here. Character gains that species. Aliens of that species may deploy (for -1Force if non-unique) or move here as a 'react'. While on Endor, adds one [Light Side Force] icon.");
        addIcons(Icon.ENDOR, Icon.VIRTUAL_SET_10);
    }



    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {

        // Deploy on your non-alien, non-Jedi character.
        Filter nonAlien = Filters.not(Filters.alien);
        Filter nonJedi = Filters.not(Filters.Jedi);
        Filter nonAlienNonJediCharacter = Filters.and(nonAlien, nonJedi, Filters.character);

        // Character needs to be with another alien with a species
        Filter alienWhichHasSpecies = Filters.and(Filters.alien, Filters.hasSpecies);
        Filter nonAlienNonJediCharacterWithAlien = Filters.and(nonAlienNonJediCharacter, Filters.with(self, alienWhichHasSpecies));

        return nonAlienNonJediCharacterWithAlien;
    }

    /*
    @Override
    protected List<TargetingEffect> getGameTextTargetCardsWhenDeployedEffects(final Action action, String playerId, SwccgGame game, final PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {

        final Filter alienWhichHasSpeciesHere = Filters.and(Filters.alien, Filters.hasSpecies, Filters.here(target));

        TargetingEffect targetingEffect = new TargetCardOnTableEffect(action, playerId, "Choose alien to copy species", alienWhichHasSpeciesHere) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard target) {
                action.addAnimationGroup(target);
                self.setTargetedCard(TargetId.EFFECT_TARGET_1, targetGroupId, target, alienWhichHasSpeciesHere);
            }
        };
        return Collections.singletonList(targetingEffect);
    }*/


    @Override
    protected StandardEffect getGameTextSpecialDeployCostEffect(final Action action, final String playerId, SwccgGame game, final PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {

        System.out.println("** PART OF TRIBE: getGameTextSpecialDeployCostEffect");

        // Part of the 'cost' for deploying this is picking the species
        // Build the list of species first (both a text-array and a species array)
        List<Species> speciesHereBuilder = new LinkedList<>();
        List<String> speciesTextListBuilder = new LinkedList<>();
        for (Species specie: Species.values()) {
            Filter alienOfSpeciesHere = Filters.and(Filters.here(target), Filters.alien, Filters.species(specie));
            if (GameConditions.canSpot(game, self, 1, false, alienOfSpeciesHere)) {
                speciesHereBuilder.add(specie);
                speciesTextListBuilder.add(specie.toString());
            }
        }

        // Let the player pick which of the species they want to copy
        final List<Species> speciesHere = speciesHereBuilder;
        final List<String> speciesTextList = speciesTextListBuilder;


        return new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                // Ask player which species we want to add to this character
                game.getUserFeedback().sendAwaitingDecision(playerId,
                        new MultipleChoiceAwaitingDecision("Choose species to add to character", speciesTextList.toArray(new String[0]), speciesHere.size() - 1) {
                            @Override
                            public void validDecisionMade(int index, String result) {
                                final Species speciesChosen = speciesHere.get(index);
                                self.setWhileInPlayData(new WhileInPlayData(speciesChosen));
                            }
                        });
            }
        };

    }


    /**
     * See which species the player picked when he played this card
     * @param self    This card
     * @return Species or null if none selected
     */
    Species getSelectedSpecies(PhysicalCard self) {
        WhileInPlayData whileInPlayData = self.getWhileInPlayData();
        if (whileInPlayData != null) {
            Species speciesData = whileInPlayData.getSpeciesValue();
            if (speciesData != null) {
                return speciesData;
            }
        }

        return null;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        System.out.println("***** getGameTextWhileActiveInPlayModifiers *****");

        List<Modifier> modifiers = new LinkedList<Modifier>();
        String playerId = self.getOwner();

        // See what species we are affecting
        Species speciesSelected = getSelectedSpecies(self);
        if (speciesSelected != null) {

            Filter aliensOfSpecies = Filters.and(Filters.alien, Filters.species(speciesSelected));
            Filter nonUniqueAlienOfSpecies = Filters.and(aliensOfSpecies, Filters.non_unique);
            Filter uniqeAlienOfSpecies = Filters.and(aliensOfSpecies, Filters.unique);
            Filter sameEndorSite = Filters.and(Filters.site, Filters.here(self));

            modifiers.add(new SpeciesModifier(self, self, speciesSelected));
            modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "deploy non-unique alien for -1 as a react", playerId, nonUniqueAlienOfSpecies, Filters.here(self), -1));
            modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "deploy unique alien as a react", playerId, uniqeAlienOfSpecies, Filters.here(self), 0));
            modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "move alien as a react", playerId, aliensOfSpecies, Filters.here(self)));
            modifiers.add(new IconModifier(self, sameEndorSite, new OnCondition(self, Title.Endor), Icon.LIGHT_FORCE, 1));
        } else {
            System.out.println("Crap - No species was selected when getGameTextWhileActiveInPlayModifiers was triggered.");
        }

        return modifiers;
    }

}