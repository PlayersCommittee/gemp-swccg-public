package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Alien
 * Title: Daroe
 */
public class Card11_053 extends AbstractAlien {
    public Card11_053() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Daroe", Uniqueness.UNIQUE);
        setLore("Jawa who has inside connections with the Empire. Frequently speaks with his Imperial contact, whom he reports to regarding Rebel activity in the Outer Rim.");
        setGameText("Adds 2 to power of anything he pilots. If opponent initiates a battle at same system, may reveal the top card of your Reserve Deck. If it is a Star Destroyer, may deploy it here for free. Otherwise, card is lost.");
        addIcons(Icon.TATOOINE, Icon.PILOT);
        setSpecies(Species.JAWA);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, game.getOpponent(playerId), Filters.sameSystem(self))
                && GameConditions.hasReserveDeck(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reveal top card of Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new RevealTopCardOfReserveDeckEffect(action, playerId) {
                        @Override
                        protected void cardRevealed(final PhysicalCard revealedCard) {
                            final PhysicalCard location = game.getGameState().getBattleLocation();
                            // Check if Star Destroyer that can deploy for free to same location
                            boolean deployable = Filters.and(Filters.Star_Destroyer, Filters.deployableToLocation(self, Filters.sameCardId(location), true, 0)).accepts(game.getGameState(), game.getModifiersQuerying(), revealedCard);

                            if (deployable) {
                                // Ask player to deploy card
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Do you want to deploy " + GameUtils.getCardLink(revealedCard) + " for free to " + GameUtils.getCardLink(location) + "?") {
                                                    @Override
                                                    protected void yes() {
                                                        game.getGameState().sendMessage(playerId + " chooses to deploy " + GameUtils.getCardLink(revealedCard) + " to " + GameUtils.getCardLink(location));
                                                        // Deploy card for free to location
                                                        action.appendEffect(
                                                                new StackActionEffect(action, revealedCard.getBlueprint().getPlayCardAction(playerId, game, revealedCard,
                                                                        self, true, 0, null, null, null, null, null, false, 0, Filters.locationAndCardsAtLocation(Filters.sameCardId(location)), null)));
                                                    }
                                                    @Override
                                                    protected void no() {
                                                        game.getGameState().sendMessage(playerId + " chooses to not deploy " + GameUtils.getCardLink(revealedCard) + " to " + GameUtils.getCardLink(location));
                                                        action.appendEffect(
                                                                new LoseCardFromTopOfReserveDeckEffect(action, playerId, revealedCard));
                                                    }
                                                }
                                        ));
                            }
                            else {
                                action.appendEffect(
                                        new LoseCardFromTopOfReserveDeckEffect(action, playerId, revealedCard));
                            }
                        }
                    });

            return Collections.singletonList(action);
        }
        return null;
    }
}
