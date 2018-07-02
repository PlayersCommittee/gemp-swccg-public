package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Interrupt
 * Subtype: Lost
 * Title: Path Of Least Resistance & Revealed
 */
public class Card10_019 extends AbstractLostInterrupt {
    public Card10_019() {
        super(Side.LIGHT, 3, "Path Of Least Resistance & Revealed", Uniqueness.UNIQUE);
        addComboCardTitles(Title.Path_Of_Least_Resistance, Title.Revealed);
        setGameText("Place one opponent's Undercover spy in opponent's Used Pile. OR If opponent just deployed a spy to a site where opponent has no presence or Force icons, return spy to hand. Any Force used to deploy spy remains used and that card may not deploy this turn. OR Relocate one of your characters at an interior mobile site to a related interior mobile site.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter spyFilter = Filters.and(Filters.opponents(self), Filters.undercover_spy);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, spyFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place undercover spy in Used Pile");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose undercover spy", SpotOverride.INCLUDE_UNDERCOVER, spyFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " in Used Pile",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canSpotLocation(game, 2, Filters.interior_mobile_site)) {
            Collection<PhysicalCard> characters = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.interior_mobile_site), Filters.canBeTargetedBy(self)));
            if (!characters.isEmpty()) {
                // Figure out which characters can be relocated to a related interior mobile site
                List<PhysicalCard> validCharacters = new LinkedList<PhysicalCard>();
                for (PhysicalCard character : characters) {
                    if (Filters.canBeRelocatedToLocation(Filters.and(Filters.interior_mobile_site, Filters.relatedSite(character)), 0).accepts(game, character)) {
                        validCharacters.add(character);
                    }
                }
                if (!validCharacters.isEmpty()) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Relocate character to related site");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose character", Filters.in(validCharacters)) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, final PhysicalCard characterToRelocate) {
                                    Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game,
                                            Filters.and(Filters.interior_mobile_site, Filters.relatedSite(characterToRelocate), Filters.locationCanBeRelocatedTo(characterToRelocate, 0)));
                                    action.appendTargeting(
                                            new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(characterToRelocate) + " to", Filters.in(otherSites)) {
                                                @Override
                                                protected void cardSelected(final PhysicalCard siteSelected) {
                                                    action.addAnimationGroup(characterToRelocate);
                                                    action.addAnimationGroup(siteSelected);
                                                    // Pay cost(s)
                                                    action.appendCost(
                                                            new PayRelocateBetweenLocationsCostEffect(action, playerId, characterToRelocate, siteSelected, 0));
                                                    // Allow response(s)
                                                    action.allowResponses("Relocate " + GameUtils.getCardLink(characterToRelocate) + " to " + GameUtils.getCardLink(siteSelected),
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                                    PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new RelocateBetweenLocationsEffect(action, finalCharacter, siteSelected));
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, opponent, Filters.and(Filters.spy, Filters.canBeTargetedBy(self)), Filters.site)) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            final PhysicalCard cardPlayed = playCardResult.getPlayedCard();
            if (playCardResult.isPlayedToSiteWithoutPresenceOrForceIcons()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Return " + GameUtils.getFullName(cardPlayed) + " to hand");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose spy", SpotOverride.INCLUDE_UNDERCOVER, cardPlayed) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Return " + GameUtils.getCardLink(cardPlayed) + " to hand",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ReturnCardToHandFromTableEffect(action, finalTarget));
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new MayNotDeployModifier(self, Filters.sameTitle(finalTarget), opponent), null));
                                            }
                                        }
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