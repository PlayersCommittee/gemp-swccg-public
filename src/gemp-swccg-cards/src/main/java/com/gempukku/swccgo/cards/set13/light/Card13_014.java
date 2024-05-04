package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ResetPowerAndForfeitUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.ResetPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Lost
 * Title: Diversionary Tactics
 */
public class Card13_014 extends AbstractLostInterrupt {
    public Card13_014() {
        super(Side.LIGHT, 4, "Diversionary Tactics", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Rebel pilots understand that what they lack in numbers, they must make up for in strategy. They often use diversions to remove dangerous opponents from a conflict.");
        setGameText("If your starfighter with a pilot character aboard is in a battle, target an opponent's starfighter present that has no characters aboard. For remainder of battle, both starfighters are power = 0, and target starfighter is forfeit = 0. (Immune to Sense.)");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final Filter opponentsStarfighterFilter = Filters.and(Filters.opponents(self), Filters.starfighter, Filters.presentInBattle, Filters.not(Filters.hasAboardExceptRelatedSites(self, Filters.character)));
        final Filter yourStarfighterFilter = Filters.and(Filters.your(self), Filters.starfighter, Filters.participatingInBattle, Filters.hasAboardExceptRelatedSites(self, Filters.and(Filters.pilot, Filters.character)));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, yourStarfighterFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target two starfighters");
            action.setImmuneTo(Title.Sense);
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose your starfighter", yourStarfighterFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard yourStarfighter) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's starfighter", opponentsStarfighterFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, final PhysicalCard opponentsStarfighter) {
                                            action.addAnimationGroup(yourStarfighter, opponentsStarfighter);
                                            // Allow response(s)
                                            action.allowResponses("Target " + GameUtils.getCardLink(yourStarfighter) + " and " + GameUtils.getCardLink(opponentsStarfighter),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard yourFinalTarget = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard opponentsFinalTarget = targetingAction.getPrimaryTargetCard(targetGroupId2);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new ResetPowerUntilEndOfBattleEffect(action, yourFinalTarget, 0));
                                                            action.appendEffect(
                                                                    new ResetPowerAndForfeitUntilEndOfBattleEffect(action, opponentsFinalTarget, 0));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}