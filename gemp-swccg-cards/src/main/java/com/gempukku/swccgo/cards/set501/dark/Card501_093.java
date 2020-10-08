package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Far More Frightening Than Death
 */
public class Card501_093 extends AbstractUsedOrLostInterrupt {
    public Card501_093() {
        super(Side.DARK, 4, "Far More Frightening Than Death", Uniqueness.UNIQUE);
        setLore("");
        setGameText("USED: Stack top card of Lost Pile face down (as a 'Hatred' card) on opponent's leader (or character of ability > 3) at a battleground. "
                + "LOST: If your Inquisitor in battle with a Jedi, Padawan, or 'Hatred' card, add one battle destiny (add two if with all three).");
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("There Are Some Things Far More Frightening Than Death");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.inquisitor)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.or(Filters.Jedi, Filters.padawan, Filters.hasStacked(Filters.hatredCard)))) {

            int numDestinies = 1;

            if (GameConditions.isDuringBattleWithParticipant(game, Filters.Jedi)
                    && GameConditions.isDuringBattleWithParticipant(game, Filters.padawan)
                    && (GameConditions.isDuringBattleWithParticipant(game, Filters.hasStacked(Filters.hatredCard))
                    || GameConditions.isDuringBattleAt(game, Filters.hasStacked(Filters.hatredCard)))) {
                numDestinies = 2;
            }
            actions.add(getAction(self, game, numDestinies));
        }

        Filter filter = Filters.and(Filters.opponents(playerId), Filters.character, Filters.or(Filters.leader, Filters.abilityMoreThan(3)));
        if (GameConditions.hasLostPile(game, playerId)
                && GameConditions.canSpot(game, self, filter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Stack 'Hatred' card on opponent's character");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target opponent's character", filter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            final PhysicalCard topCardOfLostPile = game.getGameState().getTopOfLostPile(playerId);
                                            action.appendEffect(
                                                    new StackOneCardFromLostPileEffect(action, topCardOfLostPile, targetedCard, true, false, true)
                                            );
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            topCardOfLostPile.setHatredCard(true);
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
        return actions;
    }

    private PlayInterruptAction getAction(PhysicalCard self, SwccgGame game, final int numDestinies) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
        action.setText("Add " + numDestinies + " battle destiny");
        action.allowResponses("Add " + numDestinies + " battle destiny",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new AddBattleDestinyEffect(action, numDestinies));
                    }
                }
        );
        return action;
    }
}
