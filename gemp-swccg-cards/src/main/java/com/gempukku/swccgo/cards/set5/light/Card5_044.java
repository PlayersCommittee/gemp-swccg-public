package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RelocateFromWeatherVaneToLocation;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.MayNotMoveUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Desperate Reach
 */
public class Card5_044 extends AbstractUsedInterrupt {
    public Card5_044() {
        super(Side.LIGHT, 5, Title.Desperate_Reach, Uniqueness.UNRESTRICTED, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("If only someone had given Luke a hand.");
        setGameText("During your move phase, relocate one of your characters (unless Disarmed) from Weather Vane to any Cloud City site. Character may not move for remainder of turn. OR Cancel Imperial Barrier or The Shield Doors Must Be Closed.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        Filter nonDisarmedCharacterStackedOnWeatherVane = Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.disarmed_character), Filters.stackedOn(self, Filters.Weather_Vane));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
            && GameConditions.canSpot(game, self, Filters.and(Filters.Weather_Vane, Filters.hasStacked(nonDisarmedCharacterStackedOnWeatherVane)))) {

            game.getGameState().sendMessage("Weather Vane with Character Stacked Found");
            final PhysicalCard weatherVane = Filters.findFirstActive(game, self, Filters.Weather_Vane);
            final PhysicalCard character = Filters.findFirstFromStacked(game, nonDisarmedCharacterStackedOnWeatherVane);

            if (character != null) {
                game.getGameState().sendMessage("Character stacked: " + GameUtils.getCardLink(character));
                final Filter locationFilter = Filters.and(Filters.Cloud_City_site, Filters.locationCanBeRelocatedTo(character, false, false, true, 0, false));
                if (GameConditions.canSpotLocation(game, locationFilter)) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Relocate character from Weather Vane");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseStackedCardEffect(action, playerId, weatherVane, character) {
                                @Override
                                protected void cardSelected(final PhysicalCard selectedCard) {
                                    action.addAnimationGroup(selectedCard);
                                    action.appendTargeting(
                                            new TargetCardOnTableEffect(action, playerId, "Choose site", locationFilter) {
                                                @Override
                                                protected void cardTargeted(int targetGroupId, final PhysicalCard selectedSite) {
                                                    action.addAnimationGroup(selectedSite);
                                                    // Allow response(s)
                                                    action.allowResponses("Relocate " + GameUtils.getCardLink(selectedCard) + " from Weather Vane to " + GameUtils.getCardLink(selectedSite),
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new RelocateFromWeatherVaneToLocation(action, selectedCard, selectedSite));
                                                                    action.appendEffect(
                                                                            new MayNotMoveUntilEndOfTurnEffect(action, selectedCard));
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
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Imperial_Barrier)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Imperial_Barrier, Title.Imperial_Barrier);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.The_Shield_Doors_Must_Be_Closed)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.The_Shield_Doors_Must_Be_Closed, Title.The_Shield_Doors_Must_Be_Closed);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Imperial_Barrier, Filters.The_Shield_Doors_Must_Be_Closed))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}