package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: Debris Zone
 */
public class Card3_121 extends AbstractUsedInterrupt {
    public Card3_121() {
        super(Side.DARK, 5, "Debris Zone", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.R2);
        setLore("A reactor core explosion in a destroyed AT-AT causes a plasma release which shatters its armor, showering the immediate area with molten projectiles.");
        setGameText("If an AT-AT, an AT-ST, Jabba's Sail Barge or Planet Defender Ion Cannon has just been lost at a site, draw destiny. All cards with that destiny number at that site are lost.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.or(Filters.AT_AT, Filters.AT_ST, Filters.Jabbas_Sail_Barge, Filters.Planet_Defender_Ion_Cannon), Filters.site)) {
            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            PhysicalCard cardLost = lostFromTableResult.getCard();
            final PhysicalCard site = lostFromTableResult.getFromLocation();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Play due to lost " + GameUtils.getFullName(cardLost));
            // Allow response(s)
            action.allowResponses("Draw destiny due to lost " + GameUtils.getCardLink(cardLost),
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                            final GameState gameState = game.getGameState();
                                            if (totalDestiny == null) {
                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                return;
                                            }

                                            // All cards with same destiny number (refresh destiny numbers in case R2D2 is present)
                                            final Collection<PhysicalCard> cardsToCheckDestiny = Filters.filterActive(game, self, SpotOverride.INCLUDE_ALL, Filters.at(site));
                                            if (!cardsToCheckDestiny.isEmpty()) {
                                                action.appendEffect(
                                                        new RefreshPrintedDestinyValuesEffect(action, cardsToCheckDestiny) {
                                                            @Override
                                                            protected void refreshedPrintedDestinyValues() {
                                                                final Collection<PhysicalCard> cardsToLose = Filters.filter(cardsToCheckDestiny, game, Filters.destinyEqualTo(totalDestiny));
                                                                if (!cardsToLose.isEmpty()) {
                                                                    action.appendEffect(
                                                                            new LoseCardsFromTableEffect(action, cardsToLose, true));
                                                                }
                                                            }
                                                        }
                                                );
                                            }
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