package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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
import com.gempukku.swccgo.logic.modifiers.MayNotBeForfeitedInBattleModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Lost
 * Title: Join Me!
 */
public class Card11_082 extends AbstractLostInterrupt {
    public Card11_082() {
        super(Side.DARK, 4, "Join Me!", Uniqueness.UNIQUE);
        setLore("'...And together, we can rule the galaxy as father and son!'");
        setGameText("Use 3 Force if Luke was just 'hit' by a lightsaber swung by Vader. Opponent must choose: Place Luke in owner's Used Pile OR Luke may not be forfeited this battle and crosses to the Dark Side at the end of the turn if still on table (is no longer 'hit').");
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final int gameTextSourceCardId = self.getCardId();
        final String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.Luke, Filters.lightsaber, Filters.Vader)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {
            final PhysicalCard luke = ((HitResult) effectResult).getCardHit();
            if (GameConditions.canTarget(game, self, luke)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Make opponent choose");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Luke", luke) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 3));
                                // Allow response(s)
                                action.allowResponses("Make opponent choose to either place Luke in Used Pile or cross Luke over to the Dark Side at end of turn",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, opponent,
                                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Place Luke in Used Pile", "Luke crosses to the Dark Side at end of turn"}) {
                                                                    @Override
                                                                    protected void validDecisionMade(int index, String result) {
                                                                        final GameState gameState = game.getGameState();
                                                                        if (index == 0) {
                                                                            gameState.sendMessage(opponent + " chooses to place Luke in Used Pile");
                                                                            action.appendEffect(
                                                                                    new PlaceCardInUsedPileFromTableEffect(action, luke));
                                                                        } else {
                                                                            gameState.sendMessage(opponent + " chooses to cross Luke to the Dark Side at end of turn");
                                                                            action.appendEffect(
                                                                                    new RestoreCardToNormalEffect(action, luke));
                                                                            action.appendEffect(
                                                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                                                            new MayNotBeForfeitedInBattleModifier(self, luke), null));
                                                                            final int permCardId = self.getPermanentCardId();
                                                                            action.appendEffect(
                                                                                    new AddUntilEndOfTurnActionProxyEffect(action,
                                                                                            new AbstractActionProxy() {
                                                                                                @Override
                                                                                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                                                    List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                                                                    final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                                                                                    // Check condition(s)
                                                                                                    if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
                                                                                                        PhysicalCard lukeToCrossOver = Filters.findFirstActive(game, self, Filters.and(Filters.Luke, Filters.canBeTargetedBy(self)));
                                                                                                        if (lukeToCrossOver != null) {

                                                                                                            final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                                                                            action2.setText("Cross Luke over to the Dark Side");
                                                                                                            action2.setActionMsg("Cross " + GameUtils.getCardLink(lukeToCrossOver) + " over to the Dark Side");
                                                                                                            action2.appendEffect(
                                                                                                                    new CrossOverCharacterEffect(action, lukeToCrossOver));
                                                                                                            actions.add(action2);
                                                                                                        }
                                                                                                    }
                                                                                                    return actions;
                                                                                                }
                                                                                            }
                                                                                    )
                                                                            );
                                                                        }
                                                                    }
                                                                }
                                                        )
                                                );
                                            }
                                        }
                                );
                            }
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}