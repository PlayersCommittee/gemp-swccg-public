package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Character
 * Subtype: Alien
 * Title: Gardulla the Hutt (V)
 */
public class Card601_151 extends AbstractAlien {
    public Card601_151() {
        super(Side.DARK, 2, 3, 3, 3, 5, "Gardulla The Hutt", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Well-known as a gambler and a gangster. Gardulla was the former owner of Shmi and Anakin Skywalker before she lost them in a bet to the Toydarian junk dealer, Watto.");
        setGameText("During battle, if [Block 4] No Bargain on table, may lose 2 force to cancel a non-[Immune To Sense] interrupt. Once per game during your move phase, may relocate any of your characters here to any site you occupy as a regular move (or vice versa). Immune to attrition < 3.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.LEGACY_BLOCK_7);
        setSpecies(Species.HUTT);
        addKeywords(Keyword.GANGSTER, Keyword.GAMBLER, Keyword.FEMALE);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Icon.LEGACY_BLOCK_4, Filters.No_Bargain))
                && GameConditions.isOncePerBattle(game, self, gameTextSourceCardId, gameTextActionId)
                && TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.Interrupt, Filters.not(Filters.immune_to_Sense)))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendCost(
                    new LoseForceEffect(action, playerId, 2));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__GARDULLA_THE_HUTT_V__RELOCATE_CHARACTERS;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)) {

            // relocate characters from here to a site you occupy
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Relocate characters from here");
            action.setActionMsg("Relocate any of your characters here to any site you occupy as a regular move");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));

            Collection<PhysicalCard> charactersHere = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.character, Filters.here(self), Filters.hasNotPerformedRegularMove, Filters.canBeRelocatedToLocation(Filters.site, true, 0)));
            Collection<PhysicalCard> potentialDestinations = new LinkedList<>();
            for(PhysicalCard location:Filters.filterTopLocationsOnTable(game, Filters.and(Filters.site, Filters.occupies(playerId)))) {
                if (!Filters.filter(charactersHere, game, Filters.canBeRelocatedToLocation(location, true, 0)).isEmpty())
                    potentialDestinations.add(location);
            }

            // Perform result(s)
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target destination location", Filters.in(potentialDestinations)) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.appendTargeting(new TargetCardsOnTableEffect(action, playerId, "Choose characters to relocate to "+ GameUtils.getCardLink(targetedCard), 1, Integer.MAX_VALUE, Filters.and(Filters.your(self), Filters.character, Filters.here(self), Filters.hasNotPerformedRegularMove, Filters.canBeRelocatedToLocation(targetedCard, true, 0))) {
                        @Override
                        protected void cardsTargeted(final int targetGroupId1, Collection<PhysicalCard> targetedCards) {
                            action.allowResponses(new RespondableEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    PhysicalCard finalLocation = action.getPrimaryTargetCard(targetGroupId);
                                    Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId1);

                                    action.appendEffect(
                                            new RelocateBetweenLocationsEffect(action, finalCharacters, finalLocation, true));
                                }
                            });
                        }
                    });
                }
            });

            actions.add(action);


            // relocate characters from a site you occupy to here

            final PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);

            final TopLevelGameTextAction action1 = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action1.setText("Relocate characters to here");
            action1.setActionMsg("Relocate any of your characters to here from any site you occupy as a regular move");

            // Update usage limit(s)
            action1.appendUsage(
                    new OncePerGameEffect(action1));

            Collection<PhysicalCard> charactersThere = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.occupies(playerId)), Filters.not(Filters.here(self)), Filters.hasNotPerformedRegularMove, Filters.canBeRelocatedToLocation(location, true, 0)));
            Collection<PhysicalCard> potentialSites = new LinkedList<>();
            for(PhysicalCard character:charactersThere) {
                potentialSites.add(game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), character));
            }

            // Perform result(s)
            action1.appendTargeting(new TargetCardOnTableEffect(action1, playerId, "Target location to move from", Filters.in(potentialSites)) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action1.appendTargeting(new TargetCardsOnTableEffect(action1, playerId, "Choose characters to relocate to "+ GameUtils.getCardLink(location), 1, Integer.MAX_VALUE, Filters.and(Filters.your(self), Filters.character, Filters.at(targetedCard), Filters.hasNotPerformedRegularMove, Filters.canBeRelocatedToLocation(location, true, 0))) {
                        @Override
                        protected void cardsTargeted(final int targetGroupId1, Collection<PhysicalCard> targetedCards) {
                            action1.allowResponses(new RespondableEffect(action1) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    Collection<PhysicalCard> finalCharacters = action1.getPrimaryTargetCards(targetGroupId1);

                                    action1.appendEffect(
                                            new RelocateBetweenLocationsEffect(action1, finalCharacters, location, true));
                                }
                            });
                        }
                    });
                }
            });
            actions.add(action1);
        }

        return actions;
    }
}