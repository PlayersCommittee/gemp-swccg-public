package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used
 * Title: Might Of The Republic
 */
public class Card12_061 extends AbstractUsedInterrupt {
    public Card12_061() {
        super(Side.LIGHT, 5, "Might Of The Republic", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("Despite its current reputation, the Republic still has able citizens that are willing to try and fight the injustices before them.");
        setGameText("Once per game, retrieve 1 Force for each of your senators at Galactic Senate. (Immune to Sense.) OR Take one senator (or Coruscant Guard, Sei Taria or Mas Amedda) into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.MIGHT_OF_THE_REPUBLIC__RETRIEVE_FORCE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            final int numSenators = Filters.countActive(game, self,
                    Filters.and(Filters.your(self), Filters.senator, Filters.at(Filters.Galactic_Senate), Filters.mayContributeToForceRetrieval));
            if (numSenators > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Retrieve " + numSenators + " Force");
                action.setImmuneTo(Title.Sense);
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new RetrieveForceEffect(action, playerId, numSenators));
                            }
                        }
                );
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.MIGHT_OF_THE_REPUBLIC__UPLOAD_SENATOR_CORUSCANT_GUARD_SEI_TARIA_OR_MAS_AMEDDA;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a senator, Coruscant Guard, Sei Taria, or Mas Amedda into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.senator, Filters.Coruscant_Guard, Filters.Sei_Taria, Filters.Mas_Amedda), true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}