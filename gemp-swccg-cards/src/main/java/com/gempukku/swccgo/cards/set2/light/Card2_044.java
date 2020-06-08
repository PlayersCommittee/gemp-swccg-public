package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: Alternatives To Fighting
 */
public class Card2_044 extends AbstractLostInterrupt {
    public Card2_044() {
        super(Side.LIGHT, 3, Title.Alternatives_To_Fighting);
        setLore("The Rebellion's limited resources force it to consider the wisdom of any military encounter. In many cases, retreat or deception is a preferable recourse.");
        setGameText("Use 3 Force to cancel a battle just initiated at a system or sector. OR Cancel Besieged. OR Release (move for free) all your characters from a captured starship to your side of any docking bay site.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.system_or_sector)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel battle");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelBattleEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Besieged)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Besieged, Title.Besieged);
            actions.add(action);
        }

        Filter starshipFilter = Filters.and(Filters.captured_starship, Filters.hasAboardExceptRelatedSites(self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.character, Filters.canBeRelocatedToLocation(Filters.docking_bay, true, 0))));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, starshipFilter)
                && GameConditions.canSpotLocation(game, Filters.docking_bay)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Relocate characters to docking bay");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose captured starship", starshipFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard starship) {
                            final Collection<PhysicalCard> characters = Filters.filterActive(game, self,
                                    SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.your(self), Filters.aboardExceptRelatedSites(starship)));
                            List<PhysicalCard> validDockingBays = new LinkedList<PhysicalCard>();
                            for (PhysicalCard dockingBay : Filters.filterTopLocationsOnTable(game, Filters.docking_bay)) {
                                for (PhysicalCard character : characters) {
                                    if (Filters.canBeRelocatedToLocation(dockingBay, true, 0).accepts(game, character)) {
                                        validDockingBays.add(dockingBay);
                                        break;
                                    }
                                }
                            }
                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose docking bay", Filters.in(validDockingBays)) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard dockingBay) {
                                            final Collection<PhysicalCard> charactersToRelocate = Filters.filter(characters, game, Filters.canBeRelocatedToLocation(dockingBay, true, 0));
                                            action.addAnimationGroup(charactersToRelocate);
                                            action.addAnimationGroup(dockingBay);
                                            // Allow response(s)
                                            action.allowResponses("Relocate " + GameUtils.getAppendedNames(charactersToRelocate) + " to " + GameUtils.getCardLink(dockingBay),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new RelocateBetweenLocationsEffect(action, charactersToRelocate, dockingBay));
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
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Besieged)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}