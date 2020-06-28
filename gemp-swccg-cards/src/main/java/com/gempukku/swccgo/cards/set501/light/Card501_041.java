package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;


/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: Endor Celebration (v)
 */
public class Card501_041 extends AbstractStartingInterrupt {
    public Card501_041() {
        super(Side.LIGHT, 5, Title.Endor_Celebration, Uniqueness.UNIQUE);
        setLore("The Rebel presence on Endor meant that the Ewoks would be able to live free from the Empire's tyranny.");
        setGameText("If your starting location had exactly 2 [LS] icons, deploy [V13] Chirpa's Hut up to three Effects (except Strike Planning) that deploy for free and are always immune to Alter. If Ewok Celebration on table, may take any Ewok into hand. Place Interrupt in Lost Pile.");
        addIcons(Icon.ENDOR, Icon.VIRTUAL_SET_13);
        setVirtualSuffix(true);
        setTestingText("Endor Celebration (v)");
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);

        // Check condition(s)
        if (startingLocation != null && Filters.iconCount(Icon.LIGHT_FORCE, 2).accepts(game, startingLocation)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy [V13] Chirpa's Hut and up to three Effects");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Chief_Chirpas_Hut, Filters.icon(Icon.VIRTUAL_SET_13)), true, false));

                            action.appendEffect(
                                    new ChooseCardsFromReserveDeckEffect(action, playerId, 1, 3, Filters.and(Filters.Effect, Filters.immune_to_Alter, Filters.deploysForFree, Filters.not(Filters.title("Strike Planning")))) {
                                        @Override
                                        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                            action.appendEffect(
                                                    new DeployCardsFromReserveDeckEffect(action, Filters.in(selectedCards), selectedCards.size(), selectedCards.size(), true)
                                            );

                                            boolean ewokCelebration = false;
                                            for (PhysicalCard physicalCard : selectedCards) {
                                                if (Filters.title("Ewok Celebration").accepts(game, physicalCard)) {
                                                    ewokCelebration = true;
                                                    break;
                                                }
                                            }
                                            if (ewokCelebration) {
                                                action.appendEffect(
                                                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Ewok, true)
                                                );
                                            }
                                        }
                                    }
                            );
                            action.appendEffect(
                                    new PutCardFromVoidInLostPileEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }
        return null;
    }
}