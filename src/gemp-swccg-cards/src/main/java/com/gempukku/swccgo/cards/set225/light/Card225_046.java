package com.gempukku.swccgo.cards.set225.light;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.FlipSingleSidedStackedCard;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * Set: Set 25
 * Type: Interrupt
 * Subtype: Lost
 * Title: Honoring What They Fight For
 */
public class Card225_046 extends AbstractLostInterrupt {
    public Card225_046() {
        super(Side.LIGHT, 5, "Honoring What They Fight For", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Luke's experience on Dagobah gave him great skill in using the Force. Vader had to keep his focus on Luke at all times, or face the consequences.");
        setGameText("If a [Cloud City] Rebel controls a battleground, turn a card stacked on Patience! face up. OR Place a card stacked on Patience! out of play to choose: if a [Cloud City] Rebel in battle, add one battle destiny. OR Take a character weapon into hand from Lost Pile.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        final Filter jediTestFaceDown = Filters.and(Filters.Jedi_Test, Filters.face_down);
        final Filter patienceWithJediTestStackedFaceDown = Filters.and(Filters.Patience, Filters.hasStacked(jediTestFaceDown));

        // Check condition(s)
        if (GameConditions.controlsWith(game, self, playerId, Filters.battleground, Filters.and(Icon.CLOUD_CITY, Filters.Rebel))
                && GameConditions.canSpot(game, self, patienceWithJediTestStackedFaceDown)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Turn Jedi Test face up");
            action.setActionMsg("Turn a Jedi Test on Patience! face up");

            action.allowResponses("Turn a Jedi Test on Patience! face up",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                            new ChooseStackedCardEffect(action, playerId, patienceWithJediTestStackedFaceDown, jediTestFaceDown, false) {
                                @Override
                                protected void cardSelected(PhysicalCard selectedCard) {
                                    // Perform result(s)
                                    action.appendEffect(
                                        new FlipSingleSidedStackedCard(action, selectedCard));
                                }
                            }
                        );
                    }
                }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        final Filter patienceWithCardStacked = Filters.and(Filters.Patience, Filters.hasStacked(Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY)));
        // Check condition(s)
        if (GameConditions.canSpot(game, self, patienceWithCardStacked)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Icon.CLOUD_CITY, Filters.Rebel))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Add one battle destiny");
                action.setActionMsg("Place a card on Patience! out of play to add one battle destiny");
                
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, patienceWithCardStacked, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY), false) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedCard) {
                                // Pay cost(s)
                                action.appendCost(
                                        new PlaceCardOutOfPlayFromOffTableEffect(action, selectedCard));
                                // Allow response(s)
                                action.allowResponses(
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddBattleDestinyEffect(action, 1));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
        }

        gameTextActionId = GameTextActionId.HONORING_WHAT_THEY_FIGHT_FOR__UPLOAD_WEAPON_FROM_LOST_PILE;
        // Check condition(s)
        if (GameConditions.canSpot(game, self, patienceWithCardStacked)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Take weapon from Lost Pile");
                action.setActionMsg("Place a card on Patience! out of play to take a character weapon into hand from Lost Pile");
                
                action.appendTargeting(
                        
                        new ChooseStackedCardEffect(action, playerId, patienceWithCardStacked, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY), false) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedCard) {
                                // Pay cost(s)
                                action.appendCost(
                                        new PlaceCardOutOfPlayFromOffTableEffect(action, selectedCard));
                                // Allow response(s)
                                action.allowResponses(
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.character_weapon, false));
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

}
