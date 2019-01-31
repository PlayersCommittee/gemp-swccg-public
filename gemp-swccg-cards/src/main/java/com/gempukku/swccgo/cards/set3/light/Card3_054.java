package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Lost
 * Title: You Will Go To The Dagobah System
 */
public class Card3_054 extends AbstractLostInterrupt {
    public Card3_054() {
        super(Side.LIGHT, 4, "You Will Go To The Dagobah System");
        setLore("'There you will learn from Yoda, the Jedi Master who instructed me.'");
        setGameText("During your control phase, use 1 Force to move any one of your [Hoth] characters of ability > 1 to any [Dagobah] location where you have presence. If moving to a Dagobah site, this movement is free and you do not need presence.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)) {
            final Filter locationFilter1 = Filters.and(Icon.DAGOBAH, Filters.location, Filters.not(Filters.Dagobah_site), Filters.occupies(playerId));
            Filter characterFilter1 = Filters.and(Filters.your(self), Icon.HOTH, Filters.character, Filters.abilityMoreThan(1),
                    Filters.canBeRelocatedToLocation(locationFilter1, true, false, false, 1, false));
            if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                    && GameConditions.canTarget(game, self, characterFilter1)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move character to Dagobah icon location");
                // Build action using common method
                buildAction(action, playerId, game, self, characterFilter1, locationFilter1, false, 1);
                actions.add(action);
            }
            final Filter locationFilter2 = Filters.and(Icon.DAGOBAH, Filters.Dagobah_site);
            Filter characterFilter2 = Filters.and(Filters.your(self), Icon.HOTH, Filters.character, Filters.abilityMoreThan(1),
                    Filters.canBeRelocatedToLocation(locationFilter2, true, false, true, 0, false));
            if (GameConditions.canTarget(game, self, characterFilter2)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move character to Dagobah site");
                // Build action using common method
                buildAction(action, playerId, game, self, characterFilter2, locationFilter2, true, 0, false);
                actions.add(action);
            }
        }
        return actions;
    }

    private void buildAction(final PlayInterruptAction action, final String playerId, final SwccgGame game, final PhysicalCard self, final Filter characterFilter, final Filter locationFilter, final boolean forFree, final int baseCost) {
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose character", characterFilter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId1, final PhysicalCard character) {
                        action.addAnimationGroup(character);
                        Collection<PhysicalCard> locations = Filters.filterTopLocationsOnTable(game, locationFilter);
                        Collection<PhysicalCard> validLocations = new LinkedList<PhysicalCard>();
                        // Figure out which locations the character can be relocated to
                        for (PhysicalCard location : locations) {
                            if (Filters.canBeRelocatedToLocation(location, true, false, forFree, baseCost).accepts(game.getGameState(), game.getModifiersQuerying(), character)) {
                                validLocations.add(location);
                            }
                        }
                        action.appendTargeting(
                                new ChooseCardOnTableEffect(action, playerId, "Choose location", Filters.in(validLocations)) {
                                    @Override
                                    protected void cardSelected(final PhysicalCard location) {
                                        action.addAnimationGroup(location);
                                        if (!forFree) {
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new UseForceEffect(action, playerId, 1));
                                        }
                                        // Allow response(s)
                                        action.allowResponses("Relocate " + GameUtils.getCardLink(character) + " to " + GameUtils.getCardLink(location),
                                                new RespondablePlayCardEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new RelocateBetweenLocationsEffect(action, character, location));
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );
    }
}