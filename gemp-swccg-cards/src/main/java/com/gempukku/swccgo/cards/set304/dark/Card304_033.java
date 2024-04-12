package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Pete From Sales
 */
public class Card304_033 extends AbstractImperial {
    public Card304_033() {
        super(Side.DARK, 4, 2, 4, 1, 2, "Pete From Sales", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Pete managed the Empire's New Dawn re-education program as a temporary assignment. In between chasing new sales leads he reminds people to be safe. Workmen's Comp is expensive.");
        setGameText("During your control phase, may 'sell' one Interrupt card from hand. Opponent must use X Force, where X = destiny of that card or entire Force Pile (opponent's choice). Place sold card on opponent's Used Pile and activate X Force.");
        addIcons(Icon.CSP);
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
