package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.DrawsNoBattleDestinyEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddToAttritionEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SubstituteDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Lost
 * Title: Artoo, I Have A Bad Feeling About This
 */
public class Card6_060 extends AbstractLostInterrupt {
    public Card6_060() {
        super(Side.LIGHT, 3, "Artoo, I Have A Bad Feeling About This", Uniqueness.UNIQUE);
        setLore("'He says our instructions are to give it only to Jabba himself. I'm terrible sorry. I'm afraid he's ever so stubborn about these sort of things.'");
        setGameText("If you are about to draw a battle destiny, instead use the destiny number of one of your droids in that battle. OR Add 1 to attrition against opponent for each droid you have in that battle. OR If R2-D2 and C-3PO are in battle together, opponent draws no battle destiny.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final String opponent = game.getOpponent(playerId);
        final Filter yourDroidInBattle = Filters.and(Filters.your(self), Filters.droid, Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                && GameConditions.canSubstituteDestiny(game)
                && GameConditions.canSpot(game, self, yourDroidInBattle)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Substitute destiny");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose droid", yourDroidInBattle) {
                        @Override
                        protected void cardSelected(final PhysicalCard droid) {
                            action.addAnimationGroup(droid);
                            // Pay cost(s)
                            action.appendCost(
                                    new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(droid)) {
                                        @Override
                                        protected void refreshedPrintedDestinyValues() {
                                            final float destinyNumber = game.getModifiersQuerying().getDestiny(game.getGameState(), droid);
                                            // Allow response(s)
                                            action.allowResponses("Substitute " + GameUtils.getCardLink(droid) + "'s destiny value of " + GuiUtils.formatAsString(destinyNumber) + " for battle destiny",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new SubstituteDestinyEffect(action, destinyNumber));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.canModifyAttritionAgainst(game, opponent)) {
            final int numToAdd = Filters.countActive(game, self,
                    Filters.and(Filters.your(self), Filters.droid, Filters.participatingInBattle));
            if (numToAdd > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add " + numToAdd + " to attrition");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddToAttritionEffect(action, opponent, numToAdd));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.R2D2)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.C3PO)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent draw no battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawsNoBattleDestinyEffect(action, opponent));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}