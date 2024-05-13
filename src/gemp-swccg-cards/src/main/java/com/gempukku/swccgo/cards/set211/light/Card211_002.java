package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnBottomOfUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelDestinyDrawsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Interrupt
 * Subtype: Used
 * Title: See You Around, Kid
 */
public class Card211_002 extends AbstractUsedInterrupt {
    public Card211_002() {
        super(Side.LIGHT, 5, "See You Around, Kid", Uniqueness.UNIQUE, ExpansionSet.SET_11, Rarity.V);
        setLore("");
        setGameText("Cancel a just-deployed First Order character’s game text until end of turn. OR Destiny draws during battles at sites may not be canceled this turn. OR During any draw phase, place a card from hand under Used Pile to take any one card into hand from Force Pile; reshuffle.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.EPISODE_VII);
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Cancel a just-deployed First Order character’s game text until end of turn

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, Filters.First_Order_character)) {

            final PlayCardResult playCardResult = (PlayCardResult) effectResult;
            final PhysicalCard playedCard = playCardResult.getPlayedCard();
            if (playedCard != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Cancel " + GameUtils.getFullName(playedCard) + "'s gametext");
                action.addAnimationGroup(playedCard);

                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose First Order character", playedCard) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);

                                // Allow response(s)
                                action.allowResponses("Cancel " + GameUtils.getFullName(cardTargeted) + "'s gametext until end of turn",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the final targeted card(s)
                                                PhysicalCard finalCardTargeted = action.getPrimaryTargetCard(targetGroupId1);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelGameTextUntilEndOfTurnEffect(action, finalCardTargeted));
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



    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        //
        // During battles at sites, destiny draws may not be canceled this turn
        //

        final PlayInterruptAction preventDestinyCancelAction = new PlayInterruptAction(game, self);
        preventDestinyCancelAction.setText("Affect battle destiny draws");
        preventDestinyCancelAction.setActionMsg("Prevent all destiny draws during battle at sites from being canceled this turn");


        // Allow response(s)
        preventDestinyCancelAction.allowResponses(
            new RespondablePlayCardEffect(preventDestinyCancelAction) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    preventDestinyCancelAction.appendEffect(
                            new AddUntilEndOfTurnModifierEffect(preventDestinyCancelAction,
                                    new MayNotCancelDestinyDrawsModifier(self, new DuringBattleAtCondition(Filters.site), true),
                                        "During battles at sites, prevents all destiny draws from being canceled this turn.")
                    );
                }
            }
        );
        actions.add(preventDestinyCancelAction);



        //
        // During any draw phase, place a card from hand under Used Pile to take any one card into hand from Force Pile; reshuffle.
        //

        // Check condition(s)
        if (GameConditions.isEitherPlayersPhase(game, Phase.DRAW)
                && GameConditions.hasInHand(game, playerId, Filters.not(self))
                && GameConditions.hasForcePile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place card from hand under Used Pile");

            // Pay cost(s)
            action.appendCost(
                    new PutCardFromHandOnBottomOfUsedPileEffect(action, playerId, Filters.not(self), true));

            // Allow response(s)
            action.allowResponses("Take a card from Force Pile into hand",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new TakeCardIntoHandFromForcePileEffect(action,  playerId, true));
                    }
                });

            actions.add(action);
        }

        return actions;
    }
}