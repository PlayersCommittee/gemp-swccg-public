package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: Han's Dice (V)
 */
public class Card501_031 extends AbstractUsedInterrupt {
    public Card501_031() {
        super(Side.LIGHT, 3, Title.Hans_Dice);
        setLore("A pair of dice dangling above Millennium Falcon's cockpit, for luck. 'I've never seen anything to make me believe there's one, all-powerful Force controlling everything.'");
        setGameText("If Han or your gambler is in battle (with no Jedi), draw destiny: (0-2) subtract that amount from opponent's total battle destiny; (3-4) cancel the game text of a character participating in battle (if any); (5+) opponent loses all immunity to attrition (if any) this battle.");
        setVirtualSuffix(true);
        setTestingText("Han's Dice (V)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.or(Filters.Han, Filters.and(Filters.your(playerId), Filters.gambler)))
                && !GameConditions.isDuringBattleWithParticipant(game, Filters.Jedi)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.allowResponses("Draw Destiny",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new DrawDestinyEffect(action, playerId) {
                                @Override
                                protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                    if (totalDestiny >= 0 && totalDestiny <= 2) {
                                        //(1-2) subtract destiny draw from opponent's total battle destiny
                                        action.appendEffect(
                                                new ModifyTotalBattleDestinyEffect(action, game.getOpponent(playerId), -totalDestiny));
                                    } else if (totalDestiny >= 3 && totalDestiny <= 4) {
                                        //(3-4) cancel a character's game text
                                        action.appendEffect(
                                                new ChooseCardOnTableEffect(action, playerId, "Choose character", Filters.and(Filters.character, Filters.participatingInBattle)) {
                                                    @Override
                                                    protected void cardSelected(final PhysicalCard selectedCard) {
                                                        action.addAnimationGroup(selectedCard);
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new CancelGameTextUntilEndOfBattleEffect(action, selectedCard));
                                                    }
                                                });
                                    } else if (totalDestiny >= 5) {
                                        //(5+) cancel all opponent's immunity to attrition this battle
                                        action.appendEffect(
                                                new CancelImmunityToAttritionUntilEndOfBattleEffect(action, Filters.opponents(playerId), "cancels immunity to attrition")
                                        );
                                    } else {
                                        game.getGameState().sendMessage("Result: No effect");
                                    }
                                }
                            });
                        }
                    });
            return Collections.singletonList(action);
        }

        return null;
    }
}