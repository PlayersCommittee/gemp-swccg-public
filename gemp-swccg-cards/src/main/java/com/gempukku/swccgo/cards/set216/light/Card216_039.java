package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * Set: Set 16
 * Type: Interrupt
 * Subtype: Starting
 * Title: I Am Part Of The Living Force
 */
public class Card216_039 extends AbstractStartingInterrupt {
    public Card216_039() {
        super(Side.LIGHT, 5, "I Am Part Of The Living Force", Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLore("");
        setGameText("If your starting location had 'communing' in game text, deploy Communing and stack a Jedi with 'communing' in game text on it. Deploy up to three Effects that deploy on table and are always immune to Alter. Place Interrupt in Lost Pile.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final Filter communingInGameText = Filters.or(Filters.gameTextContains("communing"), Filters.gameTextContains("communings"));
        final PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
        if (startingLocation != null && communingInGameText.accepts(game, startingLocation)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy Communing and up to three Effects from Reserve Deck");
            action.setActionMsg("Deploy Communing, stack a Jedi with 'communing' in game text on it, and deploy up to three Effects from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Communing, true, false)
                            );

                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            PhysicalCard communing = Filters.findFirstActive(game, null, Filters.Communing);
                                            if (communing != null) {
                                                action.appendEffect(
                                                        new StackCardFromReserveDeckEffect(action, communing, Filters.and(Filters.Jedi, communingInGameText), false)
                                                );
                                                action.appendEffect(
                                                        new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter,
                                                                Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table"))), 1, 3, true, false));
                                            }

                                            action.appendEffect(
                                                    new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                        }
                                    }
                            );
                        }
                    }
            );
            return action;
        }
        return null;
    }
}