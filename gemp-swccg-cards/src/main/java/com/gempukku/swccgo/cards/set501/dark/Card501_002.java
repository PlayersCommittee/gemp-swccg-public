package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractLostOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.modifiers.MayNotBeForfeitedInBattleModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;


/**
 * Set: Set 8
 * Subtype: Lost Or Starting
 * Title: You Know What I've Come For
 */

public class Card501_002 extends AbstractLostOrStartingInterrupt {
    public Card501_002() {
        super(Side.DARK, 3, "Kill Them Immediately", Uniqueness.UNIQUE);
        setLore("Darth Sidious instructed Nute Gunray to dispose of the Jedi ambassadors. Rune Haako was not so confident.");
        setGameText("LOST: If your Destroyer Droid just hit a Jedi, opponent has to lose that Jedi or 1 Force. STARTING: Deploy Droid Racks(v), Where Are Those Droidekas?(v) and one effect that is always [Immune to Alter]. May also deploy Well Guarded. Place this Interrupt in Lost Pile.");
        addIcons(Icon.EPISODE_I);
        setVirtualSuffix(true);
        setTestingText("Kill Them Immediately v");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        final String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.and(Filters.opponents(self), Filters.Jedi), Filters.and(Filters.your(self), Filters.destroyer_droid))) {
            final PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            if (GameConditions.canTarget(game, self, targetingReason, cardHit)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Make opponent choose");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target character", targetingReason, cardHit) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Make opponent choose to lose Jedi or 1 Force",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, opponent,
                                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Lose Jedi", "Lose 1 Force"}) {
                                                                    @Override
                                                                    protected void validDecisionMade(int index, String result) {
                                                                        final GameState gameState = game.getGameState();
                                                                        if (index == 0) {
                                                                            gameState.sendMessage(opponent + " chooses to lose Jedi");
                                                                            action.appendEffect(
                                                                                    new PlaceCardInLostPileFromTableEffect(action, cardHit));
                                                                        } else {
                                                                            gameState.sendMessage(opponent + " chooses to lose 1 Force");
                                                                            action.appendEffect(
                                                                                    new LoseForceEffect(action, opponent, 1, false));
                                                                        }
                                                                    }
                                                                }
                                                        )
                                                );
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

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy cards from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Deploy Droid Racks (v), Where Are Those Droidekas? (v), and one Effect from Reserve Deck",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        if (GameConditions.hasInReserveDeck(game, playerId, Filters.and(Filters.Droid_Racks, Filters.and(Filters.icon(Icon.VIRTUAL_SET_8), Filters.deployable(self, null, true, 0))))
                                && GameConditions.hasInReserveDeck(game, playerId, Filters.and(Filters.Where_Are_Those_Droidekas, Filters.and(Filters.icon(Icon.VIRTUAL_SET_8), Filters.deployable(self, null, true, 0))))
                                && GameConditions.hasInReserveDeck(game, playerId, Filters.and(Filters.Effect, Filters.always_immune_to_Alter))) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Droid_Racks, Filters.icon(Icon.VIRTUAL_SET_8)), true, false));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Where_Are_Those_Droidekas, Filters.icon(Icon.VIRTUAL_SET_8)), true, false));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.Well_Guarded, 0, 1, true, false));
                        }
                        else {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.none, true, false));
                        }
                        action.appendEffect(
                                new PutCardFromVoidInLostPileEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}