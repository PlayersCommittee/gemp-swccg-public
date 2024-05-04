package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: Walker Barrage
 */
public class Card3_140 extends AbstractUsedInterrupt {
    public Card3_140() {
        super(Side.DARK, 5, Title.Walker_Barrage, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.U1);
        setLore("Before an AT-AT's troops can disembark to engage the enemy, the walker must first destroy the Rebel traitors' defensive emplacements.");
        setGameText("If you have a piloted AT-AT present at a site, target opponent's artillery weapon at same or adjacent exterior site. Draw destiny. Target lost if destiny +1 > forfeit. Also, one opponent's character at same site as target (random selection) lost if destiny +1 > 6.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter atatFilter = Filters.and(Filters.your(self), Filters.piloted, Filters.AT_AT, Filters.presentAt(Filters.and(Filters.exterior_site,
                Filters.sameOrAdjacentSiteAs(self, Filters.and(Filters.opponents(self), Filters.artillery_weapon, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST))))));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, atatFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target artillery weapon");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose AT-AT", atatFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard atat) {
                            Filter weaponFilter = Filters.and(Filters.opponents(self), Filters.artillery_weapon, Filters.at(Filters.and(Filters.exterior_site, Filters.sameOrAdjacentSite(atat))));
                            // Choose target(s)
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose artillery weapon", targetingReason, weaponFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedCard) {
                                            action.addAnimationGroup(atat);
                                            action.addAnimationGroup(targetedCard);
                                            // Allow response(s)
                                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost while targeting " + GameUtils.getCardLink(atat),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId2);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            GameState gameState = game.getGameState();
                                                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                                                                            float forfeit = modifiersQuerying.getForfeit(gameState, finalTarget);
                                                                            gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : 0));
                                                                            float totalToCheck = modifiersQuerying.getCalculationTotal(gameState, self, (totalDestiny != null ? totalDestiny : 0) + 1);
                                                                            gameState.sendMessage("Total: " + GuiUtils.formatAsString(totalToCheck));
                                                                            gameState.sendMessage("Forfeit: " + GuiUtils.formatAsString(forfeit));

                                                                            if (totalToCheck > forfeit) {
                                                                                if (totalToCheck > 6) {
                                                                                    Collection<PhysicalCard> characters = Filters.filterActive(game, self, Filters.and(Filters.opponents(self), Filters.character, Filters.atSameSite(finalTarget)));
                                                                                    if (!characters.isEmpty()) {
                                                                                        PhysicalCard randomCharacter = GameUtils.getRandomCards(characters, 1).get(0);
                                                                                        gameState.sendMessage("Result: Succeeded (and random character " + GameUtils.getCardLink(randomCharacter) + " lost)");
                                                                                        gameState.cardAffectsCard(playerId, self, randomCharacter);
                                                                                        action.appendEffect(
                                                                                                new LoseCardsFromTableEffect(action, Arrays.asList(finalTarget, randomCharacter)));
                                                                                        return;
                                                                                    }
                                                                                }
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new LoseCardFromTableEffect(action, finalTarget));
                                                                            } else {
                                                                                gameState.sendMessage("Result: Failed");
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
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}