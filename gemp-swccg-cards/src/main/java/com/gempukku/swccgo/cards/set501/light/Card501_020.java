package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: Anakin Skywalker (V)
 */
public class Card501_020 extends AbstractUsedOrLostInterrupt {
    public Card501_020() {
        super(Side.LIGHT, 4, Title.Anakin_Skywalker, Uniqueness.UNIQUE);
        setLore("'You were right about me. Tell your sister ... you were right.'");
        setGameText("USED: Relocate Prophecy Of The Force to a site. LOST: If I Feel The Conflict on table and Luke armed with [Death Star II] Luke's Lightsaber during battle at a site, choose: Add one destiny to attrition. OR Cancel the game text of a character of ability < 4.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_13);
        setVirtualSuffix(true);
        setTestingText("Anakin Skywalker (V) (ERRATA)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
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

        if (GameConditions.canSpot(game, self, Filters.I_Feel_The_Conflict)) {
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Luke, Filters.at(Filters.site)))
                    && GameConditions.isArmedWith(game, Filters.findFirstActive(game, self, Filters.Luke), Filters.and(Filters.icon(Icon.DEATH_STAR_II), Filters.Lukes_Lightsaber))) {

                if (GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                    action.setText("Add destiny to attrition");
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new AddDestinyToAttritionEffect(action, 1)
                            );
                        }
                    });

                    actions.add(action);
                }

                Filter characterAbilityLessThanFour = Filters.and(Filters.character, Filters.abilityLessThan(4));

                if (GameConditions.isDuringBattleWithParticipant(game, characterAbilityLessThanFour)) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                    action.setText("Cancel game text of character ability less than 4");
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose character of ability less than 4", characterAbilityLessThanFour) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                    action.allowResponses(new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
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
        }
        return actions;
    }
}