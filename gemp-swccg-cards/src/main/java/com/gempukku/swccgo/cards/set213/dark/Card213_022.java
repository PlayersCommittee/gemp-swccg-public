package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SubtractFromOpponentsAttritionEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Lost
 * Title: Working Much More Closely
 */
public class Card213_022 extends AbstractLostInterrupt {
    public Card213_022() {
        super(Side.DARK, 3, "Working Much More Closely", Uniqueness.UNIQUE);
        setLore("Hologram");
        setGameText("If you have two Crimson Dawn characters in battle together, draw destiny and subtract that amount from attrition against you. LOST: Deploy up to two Crimson Dawn characters from Reserve Deck; reshuffle. OR Cancel a hologram.");
        addIcons(Icon.VIRTUAL_SET_13);
        addKeyword(Keyword.HOLOGRAM);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Deploy Crimson Dawn characters from reserve:
        // Check condition(s)
        GameTextActionId gameTextActionId = GameTextActionId.WORKING_MUCH_MORE_CLOSELY__DOWNLOAD_CRIMSON_DAWN_CHARACTERS;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, false, false)) {

            final Filter crimsonDawnCharacter = Filters.and(Filters.Crimson_Dawn, Filters.character);

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Deploy characters from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy up to two Crimson Dan characters from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, crimsonDawnCharacter, 1, 2, true));
                        }
                    }
            );
            actions.add(action);
        }


        // Cancel a Hologram:
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.hologram)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.hologram, "a hologram");
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.hologram)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {

        Filter yourCrimsonDawnCharacterInBattle = Filters.and(
                Filters.your(self),
                Filters.Crimson_Dawn,
                Filters.character,
                Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && (GameConditions.canSpot(game, self, 2, yourCrimsonDawnCharacterInBattle))) {

            final BattleState battleState = game.getGameState().getBattleState();
            final float currentAttrition = battleState.getAttritionTotal(game, playerId);
            if (currentAttrition > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Reduce opponent's attrition");

                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DrawDestinyEffect(action, playerId, 1) {
                                            @Override
                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                if (totalDestiny != null && totalDestiny > 0) {
                                                    action.appendEffect(
                                                            new SubtractFromOpponentsAttritionEffect(action, totalDestiny));
                                                }
                                            }
                                        });
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}