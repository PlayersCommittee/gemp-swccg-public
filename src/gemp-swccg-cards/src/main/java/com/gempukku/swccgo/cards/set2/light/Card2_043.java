package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Advance Preparation
 */
public class Card2_043 extends AbstractUsedOrLostInterrupt {
    public Card2_043() {
        super(Side.LIGHT, 6, Title.Advance_Preparation, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLore("Early warning and careful planning allow the Rebels to achieve maximum readiness while still maintaining optimal flexibility.");
        setGameText("USED: Place out of play any one of the following from your hand: Attack Run, You're All Clear Kid, Death Star: Trench or Rebel Tech. Retrieve 1 Force. LOST: Use 3 Force to retrieve Attack Run.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final Collection<PhysicalCard> mayBePlacedOutOfPlay = Filters.filter(game.getGameState().getHand(playerId), game,
                Filters.and(Filters.or(Filters.Attack_Run, Filters.Youre_All_Clear_Kid, Filters.Death_Star_Trench, Filters.Rebel_Tech), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY)));
        if (!mayBePlacedOutOfPlay.isEmpty()) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Retrieve 1 Force");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, Filters.in(mayBePlacedOutOfPlay)) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
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
                                                    new RetrieveForceEffect(action, playerId, 1));
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Retrieve Attack Run");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, Filters.Attack_Run));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}