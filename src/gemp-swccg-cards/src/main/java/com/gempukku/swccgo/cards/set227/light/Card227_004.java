package com.gempukku.swccgo.cards.set227.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Interrupt
 * SubType: Used
 * Title: A New Hope
 */
public class Card227_004 extends AbstractUsedInterrupt {
    public Card227_004() {
        super(Side.LIGHT, 4, Title.A_New_Hope, Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setLore("The heroes of the Rebellion know that where there is life, there is hope.");
        setGameText("For remainder of turn, opponent may not cancel battle destiny draws. OR If Luke or Leia in battle alone, draw one battle destiny if unable to otherwise. OR If an Alderaan or Tatooine location on table, ▲ Luke (or a Lars) or Leia (or Bail) with printed deploy < 4.");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.A_NEW_HOPE__UPLOAD_LUKE_OR_LEIA;

        if (GameConditions.canSpot(game, self, Filters.or(Filters.Alderaan_location, Filters.Tatooine_location))
                    && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);

            action.setText("Take Luke (or a Lars) or Leia (or Bail) into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Luke (or a Lars) or Leia (or Bail) into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.or(Filters.Luke, Filters.or(Filters.Beru, Filters.Owen, Filters.title("Cliegg Lars")), Filters.Leia, Filters.Bail), Filters.printedDeployCostLessThanOrEqualTo(3)), true));
                        }
                    }
            );
            actions.add(action);
        }

        final String opponent = game.getOpponent(playerId);
        final PlayInterruptAction protectBattleDestinyDrawsAction = new PlayInterruptAction(game, self);
        protectBattleDestinyDrawsAction.setText("Prevent canceling battle destiny draws");

        // Allow response(s)
        protectBattleDestinyDrawsAction.allowResponses("Prevent opponent from canceling battle destiny draws for remainder of turn",
                new RespondablePlayCardEffect(protectBattleDestinyDrawsAction) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        protectBattleDestinyDrawsAction.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(protectBattleDestinyDrawsAction,
                                        new MayNotCancelBattleDestinyModifier(self, null, opponent),
                                        "Prevents "+opponent+" from canceling battle destiny draws")
                        );
                    }
                }
        );
        actions.add(protectBattleDestinyDrawsAction);

        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.or(Filters.Luke, Filters.Leia), Filters.alone))) {
            final PhysicalCard battleLocation = game.getGameState().getBattleLocation();
            if (battleLocation != null) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Draw battle destiny if unable to otherwise");
                action.addAnimationGroup(battleLocation);
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, Filters.and(Filters.your(self), Filters.at(battleLocation), Filters.participatingInBattle), 1), "Draws battle destiny if unable to otherwise")
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
