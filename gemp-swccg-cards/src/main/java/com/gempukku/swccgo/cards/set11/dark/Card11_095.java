package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractPodracer;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ExchangeTopmostRaceDestiniesEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.RaceDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Podracer
 * Title: Ben Quadinaros' Podracer
 */
public class Card11_095 extends AbstractPodracer {
    public Card11_095() {
        super(Side.DARK, 2, "Ben Quadinaros' Podracer", ExpansionSet.TATOOINE, Rarity.C);
        setLore("Balta-Trabaat BT310 Podracer that utilizes four engines instead of two. Has the potential to be the fastest Podracer on the track, despite its inconsistent performance.");
        setGameText("Deploy on Podrace Arena. Adds 1 to each of your race destinies here. Once during each of your control phases may lose 2 Force to exchange the topmost race destiny here with topmost race destiny of another Podracer.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Podrace_Arena;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new RaceDestinyModifier(self, Filters.and(Filters.raceDestinyForPlayer(playerId), Filters.stackedOn(self)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringPodrace(game)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasStackedCards(game, self, Filters.raceDestiny)) {
            Filter podracerFilter = Filters.and(Filters.Podracer, Filters.not(self), Filters.hasStacked(Filters.raceDestiny));
            if (GameConditions.canTarget(game, self, podracerFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Exchange topmost race destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose a Podracer", podracerFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedPodracer) {
                                action.addAnimationGroup(targetedPodracer);
                                // Pay cost(s)
                                action.appendCost(
                                        new LoseForceEffect(action, playerId, 2, true));
                                // Allow response(s)
                                action.allowResponses("Exchange topmost race destinies with " + GameUtils.getCardLink(targetedPodracer),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ExchangeTopmostRaceDestiniesEffect(action, self, targetedPodracer));
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}