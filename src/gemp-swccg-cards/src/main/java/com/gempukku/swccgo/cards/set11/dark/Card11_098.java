package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractPodracer;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.RaceDestinyModifier;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Podracer
 * Title: Teemto Pagalies' Podracer
 */
public class Card11_098 extends AbstractPodracer {
    public Card11_098() {
        super(Side.DARK, 5, "Teemto Pagalies' Podracer", ExpansionSet.TATOOINE, Rarity.C);
        setLore("IPG-X1131 Longtail Podracer. Unusual circular shape is designed around an internal metal cycling ring which stabilizes the non-aerodynamic vehicle.");
        setGameText("Deploy on Podrace Arena. Adds 2 to each of your race destinies here. Once during each of your turns, may use 1 Force to place a race destiny here (random selection) in owner's Used Pile, draw one race destiny and place it here.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Podrace_Arena;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new RaceDestinyModifier(self, Filters.and(Filters.raceDestinyForPlayer(playerId), Filters.stackedOn(self)), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringPodrace(game)
                && GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasStackedCards(game, self, Filters.raceDestinyForPlayer(playerId))
                && GameConditions.canDrawRaceDestiny(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place random race destiny in Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendCost(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            PhysicalCard raceDestiny = GameUtils.getRandomCards(Filters.filter(game.getGameState().getStackedCards(self), game, Filters.raceDestinyForPlayer(playerId)), 1).get(0);
                            action.appendCost(
                                    new PutStackedCardInUsedPileEffect(action, playerId, raceDestiny, false));
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId, 1, DestinyType.RACE_DESTINY) {
                                        @Override
                                        public PhysicalCard getStackRaceDestinyOn() {
                                            return self;
                                        }
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}