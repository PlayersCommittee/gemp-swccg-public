package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Lost
 * Title: T-47 Battle Formation
 */
public class Card7_107 extends AbstractLostInterrupt {
    public Card7_107() {
        super(Side.LIGHT, 4, "T-47 Battle Formation");
        setLore("Airspeeders operate in coordinated patrols, supporting Rebel ground troops and denying infiltration of key planets.");
        setGameText("If your T-47s occupy three battleground sites on same planet, your Force drains at same and related battlegrounds this turn are +1 (or+2 if at Hoth). OR Cancel a Force drain at a site if your T-47 occupies an adjacent site. OR Cancel Walker Garrison.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Collection<PhysicalCard> occupiedBattlegroundSites = Filters.filterTopLocationsOnTable(game,
                Filters.and(Filters.battleground_site, Filters.occupiesWith(playerId, self, Filters.T_47)));
        List<PhysicalCard> validBattlegroundSites = new LinkedList<PhysicalCard>();
        for (PhysicalCard occupiedBattlegroundSite : occupiedBattlegroundSites) {
            String planetName = occupiedBattlegroundSite.getPartOfSystem();
            if (planetName != null) {
                if (Filters.filterCount(occupiedBattlegroundSites, game, 3, Filters.partOfSystem(planetName)).size() >= 3) {
                    validBattlegroundSites.add(occupiedBattlegroundSite);
                }
            }
        }
        if (!validBattlegroundSites.isEmpty()) {
            final Filter affectedLocations = Filters.and(Filters.battleground, Filters.sameOrRelatedLocationAs(self, Filters.sameLocationIds(validBattlegroundSites)));

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add to Force drains");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new ForceDrainModifier(self, affectedLocations, new CardMatchesEvaluator(1, 2, Filters.Hoth_location), playerId),
                                            "Adds to Force drains"));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Walker_Garrison)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Walker_Garrison, Title.Walker_Garrison);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.adjacentSiteTo(self, Filters.occupiesWith(playerId, self, Filters.and(Filters.piloted, Filters.T_47))))
                && GameConditions.canCancelForceDrain(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Force drain");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelForceDrainEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Walker_Garrison)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}