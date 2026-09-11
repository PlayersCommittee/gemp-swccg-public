package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Lost
 * Title: Force Push
 */
public class Card13_070 extends AbstractLostInterrupt {
    public Card13_070() {
        super(Side.DARK, 5, "Force Push", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("A fully-trained Sith warrior has more weapons at his disposal than just a lightsaber.");
        setGameText("Target opponent's Jedi with at least one combat card present with your Dark Jedi. Reveal one of target's combat cards (random selection). If revealed card's destiny > 4, place it on top of opponent's Reserve Deck. Otherwise, lose 1 Force. (Immune to Sense.)");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Opponent's Jedi with >=1 combat card, present with your Dark Jedi
        Filter jediFilter = Filters.and(
                Filters.opponents(self),
                Filters.Jedi,
                Filters.hasStacked(Filters.combatCard),
                Filters.presentWith(self, Filters.and(Filters.your(self), Filters.Dark_Jedi))
        );

        if (!GameConditions.canTarget(game, self, jediFilter)) {
            return null;
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setImmuneTo(Title.Sense);
        action.setText("Reveal a combat card");
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose Jedi", jediFilter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.addAnimationGroup(targetedCard);
                        // Allow response(s)
                        action.allowResponses("Reveal one of " + GameUtils.getCardLink(targetedCard) + "'s combat cards",
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                        if (finalTarget == null) {
                                            return;
                                        }

                                        Collection<PhysicalCard> stackedCombatCards = Filters.filter(
                                                game.getGameState().getStackedCards(finalTarget), game, Filters.combatCard);
                                        if (stackedCombatCards.isEmpty()) {
                                            return;
                                        }

                                        final PhysicalCard revealedCombatCard = GameUtils.getRandomCards(stackedCombatCards, 1).iterator().next();

                                        // Reveal to both players (card-local; no shared stacked-reveal hierarchy)
                                        action.appendEffect(
                                                new ShowCardOnScreenEffect(action, revealedCombatCard));
                                        action.appendEffect(
                                                new RefreshPrintedDestinyValuesEffect(action, revealedCombatCard) {
                                                    @Override
                                                    protected void refreshedPrintedDestinyValues() {
                                                        float destiny = revealedCombatCard.getDestinyValueToUse();
                                                        game.getGameState().sendMessage("Revealed " + GameUtils.getCardLink(revealedCombatCard)
                                                                + " (destiny " + GuiUtils.formatAsString(destiny) + ")");

                                                        if (destiny > 4) {
                                                            game.getGameState().sendMessage("Result: Destiny > 4 - place combat card on Reserve Deck");
                                                            action.appendEffect(
                                                                    new PutStackedCardInReserveDeckEffect(action, playerId, revealedCombatCard, false));
                                                        } else {
                                                            game.getGameState().sendMessage("Result: Destiny <= 4 - lose 1 Force; combat card remains stacked");
                                                            action.appendEffect(
                                                                    new LoseForceEffect(action, playerId, 1));
                                                        }
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
}
