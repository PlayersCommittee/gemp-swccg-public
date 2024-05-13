package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.MayNotMoveUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardUsingLandspeedEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Rite Of Passage
 */
public class Card5_151 extends AbstractUsedInterrupt {
    public Card5_151() {
        super(Side.DARK, 4, Title.Rite_Of_Passage, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("There are many different paths to becoming a Jedi, each with its own risks and consequences. A student must choose wisely.");
        setGameText("At the beginning of opponent's move phase, target an opponent's character alone at a mobile site and select an adjacent site. Opponent must choose to move target there for free (target cannot move for remainder of turn), lose target or lose 2 Force.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.MOVE)
                && GameConditions.canSpotLocation(game, 2, Filters.mobile_site)) {
            // Figure out which characters are at a site which has an adjacent site
            Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.alone, Filters.at(Filters.and(Filters.mobile_site,
                    Filters.adjacentSiteTo(self, Filters.site))));
            if (GameConditions.canTarget(game, self, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Target opponent's character");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                                action.addAnimationGroup(character);
                                // Choose target(s)
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose site", Filters.adjacentSite(character)) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard toSite) {
                                                action.addAnimationGroup(toSite);
                                                // Allow response(s)
                                                action.allowResponses("Make opponent choose to move " + GameUtils.getCardLink(character) + " to " + GameUtils.getCardLink(toSite) + ", lose " + GameUtils.getCardLink(character) + ", or lose 2 Force",
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                if (Filters.movableAsRegularMoveUsingLandspeed(opponent, false, false, true, 0, null, Filters.sameLocationId(toSite)).accepts(game, character)) {
                                                                    action.appendEffect(
                                                                            new PlayoutDecisionEffect(action, opponent,
                                                                                    new MultipleChoiceAwaitingDecision("Choose an effect", new String[]{"Move character", "Lose character", "Lose 2 Force"}) {
                                                                                        @Override
                                                                                        protected void validDecisionMade(int index, String result) {
                                                                                            if (index == 0) {
                                                                                                game.getGameState().sendMessage(opponent + " chooses to move " + GameUtils.getCardLink(character) + " to " + GameUtils.getCardLink(toSite));
                                                                                                action.appendEffect(
                                                                                                        new MoveCardUsingLandspeedEffect(action, opponent, character, true, toSite));
                                                                                                action.appendEffect(
                                                                                                        new MayNotMoveUntilEndOfTurnEffect(action, character));
                                                                                            }
                                                                                            else if (index == 1) {
                                                                                                game.getGameState().sendMessage(opponent + " chooses to lose " + GameUtils.getCardLink(character));
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, character));
                                                                                            }
                                                                                            else {
                                                                                                game.getGameState().sendMessage(opponent + " chooses to lose 2 Force");
                                                                                                action.appendEffect(
                                                                                                        new LoseForceEffect(action, opponent, 2));
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            )
                                                                    );
                                                                }
                                                                else {
                                                                    action.appendEffect(
                                                                            new PlayoutDecisionEffect(action, opponent,
                                                                                    new MultipleChoiceAwaitingDecision("Choose an effect", new String[]{"Lose character", "Lose 2 Force"}) {
                                                                                        @Override
                                                                                        protected void validDecisionMade(int index, String result) {
                                                                                            if (index == 0) {
                                                                                                game.getGameState().sendMessage(opponent + " chooses to lose " + GameUtils.getCardLink(character));
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, character));
                                                                                            }
                                                                                            else {
                                                                                                game.getGameState().sendMessage(opponent + " chooses to lose 2 Force");
                                                                                                action.appendEffect(
                                                                                                        new LoseForceEffect(action, opponent, 2));
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            )
                                                                    );
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
        return null;
    }
}