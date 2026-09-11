package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Go For Help!
 */
public class Card8_144 extends AbstractUsedInterrupt {
    public Card8_144() {
        super(Side.DARK, 5, Title.Go_For_Help, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.C);
        setLore("When confronted with enemy troops, biker scouts are instructed to immediately call for reinforcements.");
        setGameText("If opponent just initiated a battle at an exterior site with double your total power, reveal top 3 cards of your Reserve Deck. If any of those cards are scouts or speeder bikes, deploy them for free to that battle (replace others on top of Reserve Deck in same order).");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.exterior_site)
                && GameConditions.hasReserveDeck(game, playerId)) {
            float playersPower = GameConditions.getBattlePower(game, playerId);
            float opponentsPower = GameConditions.getBattlePower(game, opponent);
            // "with double your total power" => opponent has at least twice your power
            if (opponentsPower >= (2 * playersPower)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Reveal top 3 cards of Reserve Deck");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new RevealTopCardsOfReserveDeckEffect(action, playerId, 3) {
                                            @Override
                                            protected void cardsRevealed(final List<PhysicalCard> cards) {
                                                action.appendEffect(
                                                        getDeployNextScoutOrSpeederBikeEffect(action, self, playerId, cards));
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

    /**
     * Recursively deploys any revealed scouts or speeder bikes that can deploy for free to the battle location.
     * Non-deployed revealed cards remain on top of Reserve Deck in their original relative order.
     */
    private StandardEffect getDeployNextScoutOrSpeederBikeEffect(final PlayInterruptAction action, final PhysicalCard self,
                                                                 final String playerId, final List<PhysicalCard> revealedCards) {
        return new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(final SwccgGame game) {
                Collection<PhysicalCard> deployable = Filters.filter(revealedCards, game,
                        Filters.and(Filters.or(Filters.scout, Filters.speeder_bike),
                                Filters.deployableToLocation(self, Filters.battleLocation, true, 0)));
                if (deployable.isEmpty()) {
                    return;
                }

                if (deployable.size() == 1) {
                    PhysicalCard onlyCard = deployable.iterator().next();
                    action.appendEffect(
                            new DeployCardToLocationFromReserveDeckEffect(action, onlyCard, Filters.battleLocation, true, false, false));
                    // After deploying, check remaining revealed cards again
                    action.appendEffect(getDeployNextScoutOrSpeederBikeEffect(action, self, playerId, revealedCards));
                    return;
                }

                action.appendEffect(
                        new ChooseCardEffect(action, playerId, "Choose scout or speeder bike to deploy to battle", deployable) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedCard) {
                                action.appendEffect(
                                        new DeployCardToLocationFromReserveDeckEffect(action, selectedCard, Filters.battleLocation, true, false, false));
                                action.appendEffect(getDeployNextScoutOrSpeederBikeEffect(action, self, playerId, revealedCards));
                            }
                        }
                );
            }
        };
    }
}
