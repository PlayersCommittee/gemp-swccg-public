package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MustBeForfeitedInBattleModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Trooper Charge
 */
public class Card1_274 extends AbstractLostInterrupt {
    public Card1_274() {
        super(Side.DARK, 6, Title.Trooper_Charge, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("Imperial troopers are fearless and totally indoctrinated. Backed by the most powerful weapons and starships in the galaxy, they tend to consider themselves invincible.");
        setGameText("Select one of your troopers in a battle to make a 'charge.' If that trooper is a Death Star Trooper, it is power +3 this battle. If any other trooper, power +2. However, that trooper must be forfeited at end of battle.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.trooper, Filters.participatingInBattle);

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target trooper to make a 'charge'");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose trooper", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Have " + GameUtils.getCardLink(targetedCard) + " make a 'charge'",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            int amountToAdd = Filters.Death_Star_trooper.accepts(game, finalTarget) ? 3 : 2;
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                            new PowerModifier(self, finalTarget, amountToAdd),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " power +" + amountToAdd));
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                            new MustBeForfeitedInBattleModifier(self, finalTarget), null));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}