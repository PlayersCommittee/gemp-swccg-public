package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.InitiateBattleCostModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * SubType: Alien
 * Title: Margo
 */
public class Card501_047 extends AbstractAlien {
    public Card501_047() {
        super(Side.DARK, 2, 2, 2, 2, 3, "Margo", Uniqueness.UNIQUE);
        setLore("Female Imroosian. Crimson Dawn.");
        setGameText("During opponents deploy phase, may use 1 force to; 'break cover' of opponent's undercover spy here OR draw destiny; if destiny = the number of opponent’s characters here, they must use 1 Force to initiate a battle here this turn (3 if at opponent’s site).");
        setSpecies(Species.IMROOSIAN);
        addKeywords(Keyword.CRIMSON_DAWN, Keyword.FEMALE);
        addIcon(Icon.VIRTUAL_SET_13);
        setTestingText("Margo");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.undercover_spy, Filters.atSameSite(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canUseForce(game, playerId, 1)) {

            if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, targetFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("'Break cover' of opponent's spy");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose undercover spy", SpotOverride.INCLUDE_UNDERCOVER, targetFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 1));
                                // Allow response(s)
                                action.allowResponses("'Break cover' of " + GameUtils.getCardLink(targetedCard),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new BreakCoverEffect(action, targetedCard));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw Destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (totalDestiny == Filters.countActive(game, self, Filters.and(Filters.opponents(playerId), Filters.character, Filters.here(self)))) {
                                int numForce = GameConditions.isAtLocation(game, self, Filters.and(Filters.opponents(playerId), Filters.site)) ? 3 : 1;
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new InitiateBattleCostModifier(self, Filters.here(self), numForce, game.getOpponent(playerId)),
                                                game.getOpponent(playerId) + " must use an additional " + numForce + " to initiate battle at " + GameUtils.getCardLink(self.getAtLocation()))
                                );
                            }
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
