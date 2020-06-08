package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyPoliticsUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.ResetPoliticsUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used
 * Title: On The Payroll Of The Trade Federation
 */
public class Card12_156 extends AbstractLostInterrupt {
    public Card12_156() {
        super(Side.DARK, 5, "On The Payroll Of The Trade Federation", Uniqueness.UNIQUE);
        setLore("A political agenda is no use without supporters. And in the world of politics, left and right often meet in the middle.");
        setGameText("Target an opponent's character at Galactic Senate if you have a senator there with a matching agenda. For remainder of turn: your senator's politics is increased by target's current politics, and target's politics is then reduced to zero.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        List<PhysicalCard> validOpponentsCharacters = new LinkedList<PhysicalCard>();
        Collection<PhysicalCard> opponentsCharacters = Filters.filterActive(game, self,
                Filters.and(Filters.opponents(self), Filters.character, Filters.at(Filters.Galactic_Senate), Filters.canBeTargetedBy(self)));
        for (PhysicalCard opponentsCharacter : opponentsCharacters) {
            if (GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.senator, Filters.with(opponentsCharacter), Filters.hasMatchingAgenda(opponentsCharacter), Filters.canBeTargetedBy(self)))) {
                validOpponentsCharacters.add(opponentsCharacter);
            }
        }
        if (!validOpponentsCharacters.isEmpty()) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target opponent's character");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's character", Filters.in(validOpponentsCharacters)) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard opponentsCharacter) {
                            // Choose target(s)
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose your character", Filters.and(Filters.your(self),
                                            Filters.senator, Filters.with(opponentsCharacter), Filters.hasMatchingAgenda(opponentsCharacter))) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard yourCharacter) {
                                            action.addAnimationGroup(opponentsCharacter, yourCharacter);
                                            float currentPolitics = game.getModifiersQuerying().getPolitics(game.getGameState(), opponentsCharacter);

                                            // Allow response(s)
                                            action.allowResponses("Increase " + GameUtils.getCardLink(yourCharacter) + "'s politics by " + GuiUtils.formatAsString(currentPolitics) + " and reset " + GameUtils.getCardLink(opponentsCharacter) + "'s politics to 0",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalOpponentsCharacter = action.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard finalYourCharacter = action.getPrimaryTargetCard(targetGroupId2);
                                                            float finalPolitics = game.getModifiersQuerying().getPolitics(game.getGameState(), finalOpponentsCharacter);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new ModifyPoliticsUntilEndOfTurnEffect(action, finalYourCharacter, finalPolitics,
                                                                            "Increases " + GameUtils.getCardLink(finalYourCharacter) + "'s politics by " + GuiUtils.formatAsString(finalPolitics)));
                                                            action.appendEffect(
                                                                    new ResetPoliticsUntilEndOfTurnEffect(action, finalOpponentsCharacter, 0));
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