package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ModifyEachBattleDestinyDrawUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeRandomStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: There Is No Conflict
 */
public class Card11_088 extends AbstractUsedOrLostInterrupt {
    public Card11_088() {
        super(Side.DARK, 4, "There Is No Conflict", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.C);
        setLore("'That name no longer has any meaning...'");
        setGameText("USED: If your Imperial of ability > 2 just won a battle, randomly take into hand one card stacked on I Feel The Conflict. (Immune to Sense.) LOST: If Vader in battle, subtract 2 from each of opponent's battle destiny draws.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.Imperial, Filters.abilityMoreThan(2));

        // Check condition(s)
        if (TriggerConditions.wonBattle(game, effectResult, filter)) {
            final PhysicalCard insignificantRebellion = Filters.findFirstActive(game, self, Filters.and(Filters.I_Feel_The_Conflict, Filters.hasStacked(Filters.any)));
            if (insignificantRebellion != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setImmuneTo(Title.Sense);
                action.setText("Take stacked card into hand");
                // Allow response(s)
                action.allowResponses("Randomly take a card stacked on " + GameUtils.getCardLink(insignificantRebellion) + " into hand",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new TakeRandomStackedCardIntoHandEffect(action, playerId, insignificantRebellion));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Vader)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Subtract 2 from battle destiny draws");
            // Allow response(s)
            action.allowResponses("Subtract 2 from each of opponent's battle destiny draws",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyEachBattleDestinyDrawUntilEndOfBattleEffect(action, opponent, -2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}