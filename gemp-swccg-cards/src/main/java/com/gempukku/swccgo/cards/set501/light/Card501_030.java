package com.gempukku.swccgo.cards.set501.light;
//030
//•Anakin Skywalker (V)
//USED OR LOST INTERRUPT
//If I Feel The Conflict on table and a battle was just initiated involving Luke: USED: Place Luke’s Lightsaber in
// Used Pile; Luke is immune to attrition and may not be targeted by weapons. LOST: Cancel the game text of a character of ability < 4 present.


import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: Anakin Skywalker (V)
 */
public class Card501_030 extends AbstractLostInterrupt {
    public Card501_030() {
        super(Side.LIGHT, 4, Title.Anakin_Skywalker, Uniqueness.UNIQUE);
        setLore("'You were right about me. Tell your sister ... you were right.'");
        setGameText("If I Feel The Conflict on table and Luke in battle, place Luke's Lightsaber in Used Pile and choose: Luke is immune to attrition. OR Add one destiny to attrition. OR Cancel an Interrupt targeting Luke. OR Cancel the game text of a character of ability < 4 with Luke.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_13);
        setTestingText("Anakin Skywalker (V)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, final Effect effect, PhysicalCard self) {

        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Interrupt, Filters.Luke)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    // Build action using common utility
                    CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        if (GameConditions.canSpot(game, self, Filters.I_Feel_The_Conflict)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Luke)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Lukes_Lightsaber)) {

            final PhysicalCard luke = Filters.findFirstActive(game, self, Filters.Luke);
            final PhysicalCard lukesLightsaber = Filters.findFirstActive(game, self, Filters.Lukes_Lightsaber);

            final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
            action1.setText("Make " + GameUtils.getCardLink(luke) + " immune to attrition");
            action1.allowResponses(new RespondablePlayCardEffect(action1) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action1.appendEffect(
                            new PlaceCardInUsedPileFromTableEffect(action1, lukesLightsaber)
                    );
                    action1.appendEffect(
                            new AddUntilEndOfBattleModifierEffect(
                                    action1, new ImmuneToAttritionModifier(self, Filters.Luke), GameUtils.getCardLink(luke) + " is immune to attrition for remainder of battle"
                            )
                    );
                }
            });

            actions.add(action1);

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self);
            action2.setText("Add destiny to attrition");
            action2.allowResponses(new RespondablePlayCardEffect(action2) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action2.appendEffect(
                            new PlaceCardInUsedPileFromTableEffect(action2, lukesLightsaber)
                    );
                    action2.appendEffect(
                            new AddDestinyToAttritionEffect(action2, 1)
                    );
                }
            });

            actions.add(action2);


            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(playerId), Filters.character, Filters.abilityLessThan(4)))) {
                final PlayInterruptAction action3 = new PlayInterruptAction(game, self);
                action3.setText("Cancel game text of character ability less than 4");
                action3.appendTargeting(
                        new TargetCardOnTableEffect(action3, playerId, "Choose character of ability less than 4", Filters.and(Filters.opponents(playerId), Filters.character, Filters.abilityLessThan(4))) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                action3.allowResponses(new RespondablePlayCardEffect(action3) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        final PhysicalCard finalTarget = action3.getPrimaryTargetCard(targetGroupId);
                                        action3.appendEffect(
                                                new PlaceCardInUsedPileFromTableEffect(action1, lukesLightsaber)
                                        );
                                        action3.appendEffect(
                                                new CancelGameTextUntilEndOfBattleEffect(action3, finalTarget)
                                        );
                                    }
                                });
                            }
                        }
                );

                actions.add(action3);
            }
        }
        return actions;
    }
}
