package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SuspendCardUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 25
 * Type: Interrupt
 * Subtype: Used Or Lost Interrupt
 * Title: Critical Error Revealed (V)
 */
public class Card225_007 extends AbstractUsedOrLostInterrupt {
    public Card225_007() {
        super(Side.LIGHT, 4, Title.Critical_Error_Revealed, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Hologram technology allows efficient communication of complex intelligence during war room briefings.");
        setGameText("USED: Suspend Ominous Rumors or There Are Many Hunting You Now for remainder of turn. OR [Upload] Blount, Orrimaarko, Tala Durith, or [Endor] Chewie. LOST: Lose 1 Force to exclude opponent's passenger from battle (then place this Interrupt out of play).");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_25);
        addKeyword(Keyword.HOLOGRAM);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        
        Filter suspendFilter = Filters.or(Filters.Ominous_Rumors, Filters.There_Are_Many_Hunting_You_Now);
        TargetingReason suspendedTargetingReason = TargetingReason.TO_BE_SUSPENDED;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, suspendedTargetingReason, suspendFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            
            action.setText("Suspend card for remainder of turn");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose card to suspend", suspendFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Suspend " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new SuspendCardUntilEndOfTurnEffect(action, targetedCard));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        Filter pullFilter = Filters.or(Filters.Blount, Filters.title("Orrimaarko"), Filters.persona(Persona.TALA_DURITH), Filters.and(Icon.ENDOR, Filters.Chewie));
        GameTextActionId gameTextActionId = GameTextActionId.CRITICAL_ERROR_REVEALED__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);

            action.setText("Take card into hand from Reserve Deck");

            action.allowResponses("Take Blount, Orrimaarko, Tala Durith, or [Endor] Chewie into hand from Reserve Deck",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new TakeCardIntoHandFromReserveDeckEffect(action, playerId, pullFilter, true));
                    }
                }
            );
            actions.add(action);
        }

        Filter passengerFilter = Filters.and(Filters.opponents(self), Filters.aboardAsPassenger(Filters.any), Filters.participatingInBattle);
        TargetingReason excludedTargetingReason = TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE;

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canTarget(game, self, excludedTargetingReason, passengerFilter)) {
           
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Exclude passenger from battle");
            action.setActionMsg("Exclude opponent's passenger from battle");
            // Choose target(s)
            action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose opponent's passenger", excludedTargetingReason, passengerFilter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.addAnimationGroup(targetedCard);
                        // Pay cost(s)
                        action.appendCost(new LoseForceEffect(action, playerId, 1, true));
                        // Allow response(s)
                        action.allowResponses("Exclude " + GameUtils.getCardLink(targetedCard) + " from battle",
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new ExcludeFromBattleEffect(action, finalTarget));
                                        action.appendEffect(new PlaceCardOutOfPlayFromOffTableEffect(action, self));
                                    }
                                }
                        );
                    }
                });
            actions.add(action);
        }
        return actions;
    }
}