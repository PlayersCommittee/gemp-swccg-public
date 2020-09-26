package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: Anakin Skywalker (V)
 */
public class Card501_030 extends AbstractUsedOrLostInterrupt {
    public Card501_030() {
        super(Side.LIGHT, 4, Title.Anakin_Skywalker, Uniqueness.UNIQUE);
        setLore("'You were right about me. Tell your sister ... you were right.'");
        setGameText("USED: Relocate Prophecy Of The Force to a site. LOST: If Luke in battle and I Feel The Conflict on table, place Luke's Lightsaber in Used Pile and choose: Add one destiny to attrition. OR Cancel the game text of a character of ability < 4 with Luke.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_13);
        setTestingText("Anakin Skywalker (V)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        if (GameConditions.canSpot(game, self, Filters.Prophecy_Of_The_Force)) {
            final PhysicalCard prophecyOfTheForce = Filters.findFirstActive(game, self, Filters.Prophecy_Of_The_Force);
            if (GameConditions.canSpot(game, self, Filters.canRelocateEffectTo(playerId, prophecyOfTheForce))) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Relocate " + GameUtils.getCardLink(prophecyOfTheForce) + " to a site");
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose site", Filters.canRelocateEffectTo(playerId, prophecyOfTheForce)) {
                    @Override
                    protected void cardTargeted(int targetGroupId, PhysicalCard site) {

                        final PhysicalCard finalSite = action.getPrimaryTargetCard(targetGroupId);
                        action.addAnimationGroup(prophecyOfTheForce);
                        action.addAnimationGroup(finalSite);
                        action.allowResponses(new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new AttachCardFromTableEffect(action, prophecyOfTheForce, finalSite)
                                );
                            }
                        });
                    }
                });
                actions.add(action);
            }

        }

        if (GameConditions.canSpot(game, self, Filters.I_Feel_The_Conflict)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Luke)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Lukes_Lightsaber)) {

            final PhysicalCard lukesLightsaber = Filters.findFirstActive(game, self, Filters.Lukes_Lightsaber);

            if (GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Add destiny to attrition");
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new PlaceCardInUsedPileFromTableEffect(action, lukesLightsaber)
                        );
                        action.appendEffect(
                                new AddDestinyToAttritionEffect(action, 1)
                        );
                    }
                });

                actions.add(action);
            }

            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(playerId), Filters.character, Filters.abilityLessThan(4)))) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Cancel game text of character ability less than 4");
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character of ability less than 4", Filters.and(Filters.opponents(playerId), Filters.character, Filters.abilityLessThan(4))) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                action.allowResponses(new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                        action.appendEffect(
                                                new PlaceCardInUsedPileFromTableEffect(action, lukesLightsaber)
                                        );
                                        action.appendEffect(
                                                new CancelGameTextUntilEndOfBattleEffect(action, finalTarget)
                                        );
                                    }
                                });
                            }
                        }
                );

                actions.add(action);
            }
        }
        return actions;
    }
}
