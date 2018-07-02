package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Treva Horme
 */
public class Card5_009 extends AbstractAlien {
    public Card5_009() {
        super(Side.LIGHT, 2, 2, 1, 1, 2, "Treva Horme", Uniqueness.UNIQUE);
        setLore("Lutrillian. Primary saleswoman and executive planner for Planet Dreams, Inc. Monitors production schedules and accounting. Really enjoys making a sales pitch.");
        setGameText("During your control phase, may 'sell' one Interrupt card from hand. Opponent must use X Force, where X = destiny of that card or entire Force Pile (opponent's choice). Place sold card on opponent's Used Pile and activate X Force.");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.LUTRILLIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasHand(game, playerId)) {
            final Collection<PhysicalCard> interrupts = Filters.filter(game.getGameState().getHand(playerId), game, Filters.Interrupt);
            if (!interrupts.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("'Sell' an Interrupt");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendCost(
                        new ChooseCardFromHandEffect(action, playerId, Filters.in(interrupts)) {
                            @Override
                            protected void cardSelected(final SwccgGame game, final PhysicalCard interruptToSell) {
                                // Allow response(s)
                                action.allowResponses("'Sell' " + GameUtils.getCardLink(interruptToSell) + " to opponent",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                final float destinyValue = game.getModifiersQuerying().getDestiny(game.getGameState(), interruptToSell);
                                                final float opponentsForcePileSize = GameConditions.forceAvailableToUse(game, opponent);
                                                if (opponentsForcePileSize > 0 && opponentsForcePileSize > destinyValue) {
                                                    String[] choiceArray = {GuiUtils.formatAsString(destinyValue), GuiUtils.formatAsString(opponentsForcePileSize)};
                                                    action.appendEffect(
                                                            new PlayoutDecisionEffect(action, opponent,
                                                                    new MultipleChoiceAwaitingDecision("Choose value for X", choiceArray) {
                                                                        @Override
                                                                        protected void validDecisionMade(int index, String result) {
                                                                            final float valueForX = (index == 0 ? destinyValue : opponentsForcePileSize);
                                                                            game.getGameState().sendMessage(opponent + " chooses " + GuiUtils.formatAsString(valueForX) + " for X");

                                                                            action.appendEffect(
                                                                                    new UseForceEffect(action, opponent, valueForX));
                                                                            action.appendEffect(
                                                                                    new PutCardFromHandOnUsedPileEffect(action, playerId, opponent, interruptToSell));
                                                                            action.appendEffect(
                                                                                    new ActivateForceEffect(action, playerId, ((int) Math.floor(valueForX))));
                                                                        }
                                                                    }));
                                                }
                                                else {
                                                    game.getGameState().sendMessage(opponent + "'s only option is " + GuiUtils.formatAsString(opponentsForcePileSize) + " for X");
                                                    action.appendEffect(
                                                            new UseForceEffect(action, opponent, opponentsForcePileSize));
                                                    action.appendEffect(
                                                            new PutCardFromHandOnUsedPileEffect(action, playerId, opponent, interruptToSell));
                                                    action.appendEffect(
                                                            new ActivateForceEffect(action, playerId, ((int) Math.floor(opponentsForcePileSize))));
                                                }
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
