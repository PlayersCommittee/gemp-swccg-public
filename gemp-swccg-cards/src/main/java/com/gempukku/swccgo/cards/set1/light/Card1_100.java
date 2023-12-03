package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceAtLocationFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Old Ben
 */
public class Card1_100 extends AbstractLostInterrupt {
    public Card1_100() {
        super(Side.LIGHT, 3, Title.Old_Ben, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("'That old man's just a crazy wizard.' Obi-Wan lived in Tatooine's deserts for years...ready for the right moment to act.");
        setGameText("Use 1 Force if any of your characters (except Obi-Wan) was just forfeited from a Tatooine site. Mysterious 'crazy wizard' steps in and revives (returns from Lost Pile) that character back to same site.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.justForfeitedToLostPileFromLocation(game, effectResult, Filters.and(Filters.your(self),
                Filters.character, Filters.except(Filters.ObiWan), Filters.canBeTargetedBy(self)), Filters.Tatooine_site)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canReviveCharacters(game)) {
            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            final PhysicalCard cardLost = lostFromTableResult.getCard();
            final PhysicalCard location = lostFromTableResult.getFromLocation();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Revive " + GameUtils.getFullName(cardLost));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Revive " + GameUtils.getCardLink(cardLost),
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlaceAtLocationFromLostPileEffect(action, playerId, cardLost, location, false, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}