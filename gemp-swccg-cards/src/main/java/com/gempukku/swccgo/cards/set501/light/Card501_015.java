package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Interrupt
 * Subtype: Used
 * Title: Our Only Hope (V)
 */
public class Card501_015 extends AbstractUsedOrLostInterrupt {
    public Card501_015() {
        super(Side.LIGHT, 4, Title.Our_Only_Hope, Uniqueness.UNIQUE);
        setLore("'The Emperor knew, as I did, if Anakin were to have any offspring, they would be a threat to him.'");
        setGameText("USED: Take [Endor] Leia into hand from Reserve Deck; reshuffle. LOST: If I Feel The Conflict on table and [Endor] Leia or [Death Star] Luke in battle at a site, choose: Add one destiny to attrition. OR Cancel the game text of a character of ability < 4.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_14);
        setVirtualSuffix(true);
        setTestingText("Our Only Hope (V)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OUR_ONLY_HOPE__UPLOAD_LEIA;

        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take Leia into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take [Endor] Leia into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Icon.ENDOR, Filters.Leia), true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.canSpot(game, self, Filters.I_Feel_The_Conflict)
            && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.or(Filters.and(Icon.ENDOR, Filters.Leia), Filters.and(Icon.DEATH_STAR_II, Filters.Luke)), Filters.at(Filters.site)))
                    ) {

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
        return actions;
    }
}