package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SpeciesModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Effect
 * Title: Part Of The Tribe
 */
public class Card210_021 extends AbstractNormalEffect {
    public Card210_021() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, "Part Of The Tribe", Uniqueness.UNIQUE, ExpansionSet.SET_10, Rarity.V);
        setLore("Wookiees are known to be creatures of great emotion and are very protective of family and friends. Chewbacca has come to treat Luke as a member of his own family.");
        setGameText("Deploy on your non-alien, non-Jedi character. When deployed, choose a species of an alien here. Character gains that species. Aliens of that species may deploy (for -1Force if non-unique) or move here as a 'react'. While on Endor, adds one [Light Side Force] icon.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.ENDOR, Icon.VIRTUAL_SET_10);
    }



    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {

        // Deploy on your non-alien, non-Jedi character.
        Filter nonAlien = Filters.not(Filters.alien);
        Filter nonJedi = Filters.not(Filters.Jedi);
        Filter nonAlienNonJediCharacter = Filters.and(nonAlien, nonJedi, Filters.character);
        Filter yourNonAlienNonJediCharacter = Filters.and(Filters.your(self), nonAlienNonJediCharacter);

        // Character needs to be with another alien with a species
        Filter alienWhichHasSpecies = Filters.and(Filters.alien, Filters.hasSpecies);
        Filter nonAlienNonJediCharacterWithAlien = Filters.and(yourNonAlienNonJediCharacter, Filters.with(self, alienWhichHasSpecies));

        return nonAlienNonJediCharacterWithAlien;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // When we deploy this card, we need to pick a species immediately
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);

            // Build the list of species first (both a text-array and a species array)
            List<Species> speciesHereBuilder = new LinkedList<>();
            List<String> speciesTextListBuilder = new LinkedList<>();
            for (Species specie: Species.values()) {
                Filter alienOfSpeciesHere = Filters.and(Filters.here(self), Filters.alien, Filters.species(specie));
                if (GameConditions.canSpot(game, self, 1, false, alienOfSpeciesHere)) {
                    speciesHereBuilder.add(specie);
                    speciesTextListBuilder.add(specie.toString());
                }
            }

            // Let the player pick which of the species they want to copy
            final List<Species> speciesHere = speciesHereBuilder;
            final List<String> speciesTextList = speciesTextListBuilder;

            action.appendEffect(new PassthruEffect(action) {
                @Override
                protected void doPlayEffect(final SwccgGame game) {
                    // Ask player which species we want to add to this character
                    game.getUserFeedback().sendAwaitingDecision(self.getOwner(),
                            new MultipleChoiceAwaitingDecision("Choose species to add to character", speciesTextList.toArray(new String[0]), speciesHere.size() - 1) {
                                @Override
                                public void validDecisionMade(int index, String result) {
                                    final Species speciesChosen = speciesHere.get(index);
                                    self.setWhileInPlayData(new WhileInPlayData(speciesChosen));

                                    // Tell GEMP that this card changed (has a target now)
                                    // The code below refreshes it's "whileActiveInPlayModifiers"
                                    game.getGameState().reapplyAffectingForCard(game, self);
                                }
                            });
                }
            });

            actions.add(action);
        }

        return actions;

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

        List<Modifier> modifiers = new LinkedList<Modifier>();
        String playerId = self.getOwner();

        // See what species we are affecting (This is set upon deployment, but check for null just in case.
        Species speciesSelected = getSelectedSpecies(self);
        if (speciesSelected != null) {

            Filter aliensOfSpecies = Filters.and(Filters.alien, Filters.species(speciesSelected));
            Filter nonUniqueAlienOfSpecies = Filters.and(aliensOfSpecies, Filters.non_unique);
            Filter uniqeAlienOfSpecies = Filters.and(aliensOfSpecies, Filters.unique);
            Filter sameEndorSite = Filters.and(Filters.site, Filters.here(self));

            // Apply the species to the character we are attached to.
            PhysicalCard attachedToCharacter = self.getAttachedTo();
            if (attachedToCharacter != null) {
                modifiers.add(new SpeciesModifier(self, attachedToCharacter, speciesSelected));
            }

            // Apply all other modifiers here
            modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "deploy non-unique alien for -1 as a react", playerId, nonUniqueAlienOfSpecies, Filters.here(self), -1));
            modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "deploy unique alien as a react", playerId, uniqeAlienOfSpecies, Filters.here(self), 0));
            modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "move alien as a react", playerId, aliensOfSpecies, Filters.here(self)));
            modifiers.add(new IconModifier(self, sameEndorSite, new OnCondition(self, Title.Endor), Icon.LIGHT_FORCE, 1));
        }

        return modifiers;
    }

}