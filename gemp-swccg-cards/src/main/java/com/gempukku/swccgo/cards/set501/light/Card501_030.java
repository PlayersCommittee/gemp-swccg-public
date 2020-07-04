package com.gempukku.swccgo.cards.set501.light;
//030
//•Anakin Skywalker (V)
//USED OR LOST INTERRUPT
//If I Feel The Conflict on table and a battle was just initiated involving Luke: USED: Place Luke’s Lightsaber in
// Used Pile; Luke is immune to attrition and may not be targeted by weapons. LOST: Cancel the game text of a character of ability < 4 present.


import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: Anakin Skywalker (v)
 */
public class Card501_030 extends AbstractUsedOrLostInterrupt {
    public Card501_030() {
        super(Side.LIGHT, 4, Title.Anakin_Skywalker, Uniqueness.UNIQUE);
        setLore("'You were right about me. Tell your sister ... you were right.'");
        setGameText("If I Feel The Conflict on table and a battle was just initiated involving Luke: USED: Place Luke’s Lightsaber in Used Pile; Luke is immune to attrition and may not be targeted by weapons. LOST: Cancel the game text of a character of ability < 4 present.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_13);
        setTestingText("Anakin Skywalker (v)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        if (GameConditions.canSpot(game, self, Filters.I_Feel_The_Conflict)
                && TriggerConditions.battleInitiatedAt(game, effectResult, Filters.sameSiteAs(self, Filters.Luke))) {
            if (GameConditions.canSpot(game, self, Filters.and(Filters.Lukes_Lightsaber))) {
                final PlayInterruptAction usedAction = new PlayInterruptAction(game, self, CardSubtype.USED);
                usedAction.setText("Place Luke's Lightsaber in Used Pile");
                usedAction.allowResponses("Make Luke immune to attrition and unable to be targeted by weapons", new RespondablePlayCardEffect(usedAction) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        usedAction.appendEffect(
                                new PlaceCardInUsedPileFromTableEffect(usedAction, Filters.findFirstActive(game, self, Filters.Lukes_Lightsaber))
                        );
                        usedAction.appendEffect(
                                new AddUntilEndOfBattleModifierEffect(
                                        usedAction, new ImmuneToAttritionModifier(self, Filters.Luke), "Luke is immune to attrition for remainder of battle"
                                )
                        );
                        usedAction.appendEffect(
                                new AddUntilEndOfBattleModifierEffect(
                                        usedAction, new MayNotBeTargetedByWeaponsModifier(self, Filters.Luke), "Luke may not be targeted by weapons for remainder of battle"
                                )
                        );
                    }
                });

                actions.add(usedAction);
            }

            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(playerId), Filters.character, Filters.abilityLessThan(4)))) {
                final PlayInterruptAction lostAction = new PlayInterruptAction(game, self, CardSubtype.LOST);
                lostAction.setText("Cancel game text of character ability less than 4");
                lostAction.appendTargeting(
                        new TargetCardOnTableEffect(lostAction, playerId, "Choose character of ability less than 4", Filters.and(Filters.opponents(playerId), Filters.character, Filters.abilityLessThan(4))) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                lostAction.allowResponses("", new RespondablePlayCardEffect(lostAction) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        final PhysicalCard finalTarget = lostAction.getPrimaryTargetCard(targetGroupId);

                                        lostAction.appendEffect(
                                                new CancelGameTextUntilEndOfBattleEffect(lostAction, finalTarget)
                                        );
                                    }
                                });
                            }
                        }
                );

                actions.add(lostAction);
            }
        }
        return actions;
    }
}
