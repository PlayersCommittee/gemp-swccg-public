package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawRaceDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RepairPodracerEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Droid
 * Title: Pit Droid
 */
public class Card11_060 extends AbstractDroid {
    public Card11_060() {
        super(Side.DARK, 3, 1, 1, 2, "Pit Droid", Uniqueness.UNRESTRICTED, ExpansionSet.TATOOINE, Rarity.C);
        setLore("Manufactured by Serv-O-Droid on Cyrillia. Collapses into a compact form when hit on the nose.");
        setGameText("While at Podracer Bay, once during each of your control phases may lose 1 Force to target your Podracer. Place target's top race destiny in Lost Pile, and draw one race destiny. During any control phase may use 1 Force to 'repair' your Podracer.");
        addIcons(Icon.TATOOINE,Icon.EPISODE_I);
        addModelTypes(ModelType.REPAIR);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Card action 1 (send top race destiny to lost pile)
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        final Filter yourPodracerWithRaceDestinyFilter = Filters.and(Filters.your(self), Filters.Podracer, Filters.canBeTargetedBy(self), Filters.hasStacked(Filters.topRaceDestiny));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isAtLocation(game, self, Filters.Podracer_Bay)
                && GameConditions.isDuringPodrace(game)
                && GameConditions.canTarget(game, self, yourPodracerWithRaceDestinyFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place top race destiny in Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Podracer", yourPodracerWithRaceDestinyFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new LoseForceEffect(action, playerId, 1, true));

                            // Allow response(s)
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " top race destiny on lost pile",
                                    new UnrespondableEffect(action) {
                                        final Collection<PhysicalCard> topRaceDestinies = Filters.filterStacked(game, Filters.and(Filters.stackedOn(targetedCard), Filters.topRaceDestiny));
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PutStackedCardsInLostPileEffect(action, playerId, topRaceDestinies,false));
                                            action.appendEffect(
                                                    new DrawRaceDestinyEffect(action));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        // Card action 2 (repair)
        Filter yourDamagedPodracerFilter = Filters.and(Filters.your(self), Filters.damaged, Filters.Podracer);
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        // Check condition(s)
        final float action2ForceCost = 1;
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.CONTROL)
                && GameConditions.isDuringPodrace(game)
                && GameConditions.canUseForce(game, playerId, action2ForceCost)
                && GameConditions.canTarget(game, self, yourDamagedPodracerFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("'Repair' a Podracer");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Podracer", yourDamagedPodracerFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedPodracer) {
                            action.addAnimationGroup(targetedPodracer);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, action2ForceCost));
                            // Allow response(s)
                            action.allowResponses("'Repair' " + GameUtils.getCardLink(targetedPodracer),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            PhysicalCard finalPodracer = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RepairPodracerEffect(action, finalPodracer));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}
