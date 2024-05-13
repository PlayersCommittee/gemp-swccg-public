package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: Site
 * Title: Assembly Area
 */
public class Card221_048 extends AbstractSite {
    public Card221_048() {
        super(Side.LIGHT, "Assembly Area", Uniqueness.DIAMOND_1, ExpansionSet.SET_21, Rarity.V);
        setLocationDarkSideGameText("Deploys only to same system as Clone Command Center. Your droids are power +1 here.");
        setLocationLightSideGameText("During your move phase, a pair of [Clone Army] characters may move between here and a site you occupy.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_21);
    }

    @Override
    public boolean mayNotBePartOfSystem(SwccgGame game, String system) {
        return Filters.filterTopLocationsOnTable(game, Filters.and(Filters.titleContains(Title.Clone_Command_Center), Filters.partOfSystem(system))).isEmpty();
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.droid, Filters.here(self)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        final Filter siteYouOccupy = Filters.and(Filters.other(self), Filters.site, Filters.occupies(playerOnLightSideOfLocation));

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && GameConditions.canSpotLocation(game, siteYouOccupy)) {

            final Filter characterFilter = Filters.and(Filters.your(playerOnLightSideOfLocation), Icon.CLONE_ARMY, Filters.character, Filters.hasNotPerformedRegularMove);

            //from here to a site you occupy
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, characterFilter, self, siteYouOccupy, false, 1)) {

                final List<PhysicalCard> validSites = new LinkedList<>();
                for(PhysicalCard site: Filters.filterTopLocationsOnTable(game, siteYouOccupy)) {
                    if (Filters.sameLocationAs(self, Filters.and(Filters.and(characterFilter, Filters.canBeRelocatedToLocation(site, 1)),
                            Filters.with(self, Filters.and(characterFilter, Filters.canBeRelocatedToLocation(site, 1))))).accepts(game, self)) {
                        validSites.add(site);
                    }
                }

                if (!validSites.isEmpty()) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
                    action.setText("Move from here to a site you occupy");
                    action.appendUsage(
                            new OncePerPhaseEffect(action));

                    action.appendTargeting(new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Choose location to relocate to", Filters.in(validSites)) {
                        @Override
                        protected void cardTargeted(final int targetGroupIdSite, final PhysicalCard targetedSite) {
                            action.appendTargeting(new TargetCardsOnTableEffect(action, playerOnLightSideOfLocation, "Choose characters to move", 2, 2, Filters.and(characterFilter, Filters.at(self), Filters.canBeRelocatedToLocation(targetedSite, 1))) {
                                @Override
                                protected void cardsTargeted(final int targetGroupIdCharacters, Collection<PhysicalCard> targetedCharacters) {
                                    action.appendCost(
                                            new PayRelocateBetweenLocationsCostEffect(action, playerOnLightSideOfLocation, targetedCharacters, targetedSite, 1));
                                    action.allowResponses(new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalSite = action.getPrimaryTargetCard(targetGroupIdSite);
                                            Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupIdCharacters);

                                            action.appendEffect(
                                                    new RelocateBetweenLocationsEffect(action, finalCharacters, finalSite, true));
                                        }
                                    });
                                }
                            });
                        }
                    });
                    actions.add(action);
                }
            }

            //from a site you occupy to here
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, characterFilter, siteYouOccupy, self, false, 1)) {

                final List<PhysicalCard> validSites = new LinkedList<>();
                for(PhysicalCard site: Filters.filterTopLocationsOnTable(game, siteYouOccupy)) {
                    if (Filters.sameLocationAs(self, Filters.and(Filters.and(characterFilter, Filters.canBeRelocatedToLocation(self, 1)),
                            Filters.with(self, Filters.and(characterFilter, Filters.canBeRelocatedToLocation(self, 1))))).accepts(game, site)) {
                        validSites.add(site);
                    }
                }

                if (!validSites.isEmpty()) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
                    action.setText("Move from a site you occupy to here");
                    action.appendUsage(
                            new OncePerPhaseEffect(action));

                    action.appendTargeting(new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Choose location to relocate from", Filters.in(validSites)) {
                        @Override
                        protected void cardTargeted(final int targetGroupIdSite, final PhysicalCard targetedSite) {
                            action.appendTargeting(new TargetCardsOnTableEffect(action, playerOnLightSideOfLocation, "Choose characters to move", 2, 2, Filters.and(characterFilter, Filters.at(targetedSite), Filters.canBeRelocatedToLocation(self, 1))) {
                                @Override
                                protected void cardsTargeted(final int targetGroupIdCharacters, Collection<PhysicalCard> targetedCharacters) {
                                    action.appendCost(
                                            new PayRelocateBetweenLocationsCostEffect(action, playerOnLightSideOfLocation, targetedCharacters, self, 1));
                                    action.allowResponses(new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupIdCharacters);

                                            action.appendEffect(
                                                    new RelocateBetweenLocationsEffect(action, finalCharacters, self, true));
                                        }
                                    });
                                }
                            });
                        }
                    });
                    actions.add(action);
                }
            }
        }


        return actions;
    }
}