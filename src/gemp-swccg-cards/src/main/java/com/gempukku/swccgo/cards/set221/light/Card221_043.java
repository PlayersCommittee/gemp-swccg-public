package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtBottomCardOfCardPileEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromForcePileOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: A Jedi's Fury
 */
public class Card221_043 extends AbstractUsedOrLostInterrupt {
    public Card221_043() {
        super(Side.LIGHT, 5, "A Jedi's Fury", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLore("It had been decades since Vader had felt the sting of an enemy's blade.");
        setGameText("If His Destiny on table, choose: USED: If a character of ability > 4 was just 'hit' during battle, opponent loses 1 Force. " +
                "OR Peek at the bottom card of your Force Pile; may move it to the top of that pile. " +
                "LOST: Cancel game text of a Dark Jedi with Luke for remainder of turn.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();

        if (GameConditions.isDuringBattle(game)
                && TriggerConditions.justHit(game, effectResult, Filters.and(Filters.character, Filters.abilityMoreThan(4)))
                && GameConditions.canTarget(game, self, Filters.title(Title.His_Destiny))) {

            final String opponent = game.getOpponent(playerId);

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Opponent loses 1 Force");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(
                            new LoseForceEffect(action, opponent, 1));
                }
            });

            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        if (GameConditions.canTarget(game, self, Filters.title(Title.His_Destiny))) {
            // Check condition(s)
            if (GameConditions.hasForcePile(game, playerId)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Peek at bottom card of Force Pile");
                // Allow response(s)
                action.allowResponses(new RespondablePlayCardEffect(action) {
                                          @Override
                                          protected void performActionResults(Action targetingAction) {
                                              // Perform result(s)
                                              action.appendEffect(
                                                      new PeekAtBottomCardOfCardPileEffect(action, playerId, playerId, Zone.FORCE_PILE) {
                                                          @Override
                                                          protected void cardsPeekedAt(List<PhysicalCard> peekedAtCards) {
                                                              final PhysicalCard card = peekedAtCards.iterator().next();
                                                              if (card != null) {
                                                                  action.appendEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Move card to top of Force Pile?") {
                                                                      @Override
                                                                      protected void yes() {
                                                                          action.appendEffect(new PutCardFromForcePileOnTopOfCardPileEffect(action, playerId, card, Zone.FORCE_PILE, true));
                                                                      }

                                                                      @Override
                                                                      protected void no() {
                                                                          action.appendEffect(new SendMessageEffect(action, playerId + " chooses not to move card to top of Force Pile"));
                                                                      }
                                                                  }));
                                                              }
                                                          }
                                                      });
                                          }
                                      }
                );
                actions.add(action);
            }


            Filter filter = Filters.and(Filters.opponents(self), Filters.Dark_Jedi, Filters.with(self, Filters.Luke));

            if (GameConditions.canTarget(game, self, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Cancel game text of a Dark Jedi");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Dark Jedi", filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }
}
