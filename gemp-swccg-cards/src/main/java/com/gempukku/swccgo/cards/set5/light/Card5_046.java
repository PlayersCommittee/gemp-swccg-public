package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RelocateFromLocationToWeatherVane;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleEndedResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Fall Of The Empire
 */
public class Card5_046 extends AbstractLostInterrupt {
    public Card5_046() {
        super(Side.LIGHT, 3, "Fall Of The Empire", Uniqueness.UNIQUE);
        setLore("'Aaaaah!'");
        setGameText("At the end of a battle that you won at an interior site where you have a character of ability > 3 present, relocate one opponent's character present to an adjacent site. (If on Cloud City, character may be relocated to Weather Vane instead.)");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.battleEndingAt(game, effectResult, Filters.interior_site)
                && GameConditions.isDuringBattleWonBy(game, playerId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(3), Filters.presentInBattle))) {
            Filter characterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle);
            final Filter locationFilter = Filters.adjacentSite(((BattleEndedResult) effectResult).getLocation());
            if (GameConditions.canSpotLocation(game, locationFilter)) {
                Collection<PhysicalCard> characters = Filters.filter(game.getGameState().getBattleState().getAllCardsParticipating(), game, Filters.and(characterFilter, Filters.canBeTargetedBy(self)));
                if (!characters.isEmpty()) {
                    // Figure out which characters can be relocated to an adjacent site
                    List<PhysicalCard> validCharacters = new LinkedList<PhysicalCard>();
                    for (PhysicalCard character : characters) {
                        if (Filters.canBeRelocatedToLocation(locationFilter, true, 0).accepts(game, character)) {
                            validCharacters.add(character);
                        }
                    }
                    if (!validCharacters.isEmpty()) {

                        final PlayInterruptAction action = new PlayInterruptAction(game, self);
                        action.setText("Relocate character to adjacent site");
                        // Choose target(s)
                        action.appendTargeting(
                                new TargetCardOnTableEffect(action, playerId, "Choose character", Filters.in(characters)) {
                                    @Override
                                    protected void cardTargeted(final int targetGroupId, final PhysicalCard characterToRelocate) {
                                        Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game, Filters.and(locationFilter, Filters.locationCanBeRelocatedTo(characterToRelocate, false, false, true, 0)));
                                        action.appendTargeting(
                                                new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(characterToRelocate) + " to", otherSites) {
                                                    @Override
                                                    protected void cardSelected(final PhysicalCard siteSelected) {
                                                        action.addAnimationGroup(characterToRelocate);
                                                        action.addAnimationGroup(siteSelected);
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
            if (GameConditions.isDuringBattleAt(game, Filters.on_Cloud_City)
                    && GameConditions.canSpot(game, self, Filters.Weather_Vane)
                    && GameConditions.canTarget(game, self, characterFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate character to Weather Vane");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", characterFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                                action.addAnimationGroup(character);
                                // Allow response(s)
                                action.allowResponses("Relocate " + GameUtils.getCardLink(character) + " to Weather Vane",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                action.appendEffect(
                                                        new RelocateFromLocationToWeatherVane(action, finalCharacter));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}