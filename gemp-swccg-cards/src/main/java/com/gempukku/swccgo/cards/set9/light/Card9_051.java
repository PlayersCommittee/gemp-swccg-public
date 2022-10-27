package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Heading For The Medical Frigate
 */
public class Card9_051 extends AbstractUsedOrStartingInterrupt {
    public Card9_051() {
        super(Side.LIGHT, 3, "Heading For The Medical Frigate", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLore("At the Battle of Endor, Rebel starships fought to defend key capital starships, such as the Redemption.");
        setGameText("USED: Add 1 to your battle destiny just drawn. STARTING: Deploy up to three Effects if each of them deploys for free, is always immune to Alter and has 'deploy on table' (or 'deploy on your side of table') in its game text. Place Interrupt in Lost Pile.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add 1 to battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy up to three Effects from Reserve Deck");
        // Allow response(s)
        action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree, Filters.always_immune_to_Alter,
                                        Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table"))), 1, 3, true, false));
                        action.appendEffect(
                                new PutCardFromVoidInLostPileEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}