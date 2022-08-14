package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Lost
 * Title: Jedi Leap
 */
public class Card13_026 extends AbstractLostInterrupt {
    public Card13_026() {
        super(Side.LIGHT, 3, "Jedi Leap", Uniqueness.UNIQUE);
        setLore("Obi-Wan's youth combined with his Jedi training meant that he was not to be dispatched easily.");
        setGameText("During your deploy phase, lose 1 Force to relocate [Episode I] Obi- Wan to an adjacent Naboo site. OR Raise your converted interior Theed Palace site to the top.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            PhysicalCard obiWan = Filters.findFirstActive(game, self,
                    Filters.and(Icon.EPISODE_I, Filters.ObiWan, Filters.canBeTargetedBy(self)));
            if (obiWan != null
                    && Filters.canBeRelocatedToLocation(Filters.and(Filters.Naboo_site, Filters.adjacentSite(obiWan)), true, 0).accepts(game, obiWan)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate Obi-Wan to adjacent site");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Obi-Wan", Filters.sameCardId(obiWan)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard obiWan) {
                                action.addAnimationGroup(obiWan);
                                Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.and(Filters.Naboo_site, Filters.adjacentSite(obiWan), Filters.locationCanBeRelocatedTo(obiWan, false, false, true, 0, false))));
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose adjacent Naboo site", otherSites) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard location) {
                                                action.addAnimationGroup(location);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new LoseForceEffect(action, playerId, 1, true));
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(obiWan) + " to " + GameUtils.getCardLink(location),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, obiWan, location));
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

        Filter filter = Filters.and(Filters.interior_site, Filters.Theed_Palace_site, Filters.canBeConvertedByRaisingYourLocationToTop(playerId));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Raise a converted interior Theed Palace site");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose site to convert", filter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ConvertLocationByRaisingToTopEffect(action, targetedCard, true));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}