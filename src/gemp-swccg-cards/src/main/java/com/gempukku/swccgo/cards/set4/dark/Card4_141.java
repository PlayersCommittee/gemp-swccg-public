package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ChooseEffectEffect;
import com.gempukku.swccgo.logic.effects.LoseForceFromHandEffect;
import com.gempukku.swccgo.logic.effects.LoseForceFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: Fear
 */
public class Card4_141 extends AbstractLostInterrupt {
    public Card4_141() {
        super(Side.DARK, 2, "Fear", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("'I'm not afraid.' 'Oh. You will be.' One of the lessons Luke learned was that fear of the unknown can be stronger than fear of the known.");
        setGameText("Opponent must choose to lose either 2 Force from hand or 1 Force from top of Reserve Deck.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // talked to Gergall 12/11/2011, you don't have to have 2 cards in hand to choose the lose 2 from hand option or 1 card in Reserve to choose the lose from Reserve option

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Make opponent lose Force");
        // Allow response(s)
        action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        List<StandardEffect> possibleEffects = new LinkedList<StandardEffect>();
                        possibleEffects.add(
                                new LoseForceFromHandEffect(action, opponent, 2));
                        possibleEffects.add(
                                new LoseForceFromReserveDeckEffect(action, opponent, 1));
                        // Perform result(s)
                        action.appendEffect(
                                new ChooseEffectEffect(action, opponent, possibleEffects));
                    }
                }
        );
        return Collections.singletonList(action);
    }
}