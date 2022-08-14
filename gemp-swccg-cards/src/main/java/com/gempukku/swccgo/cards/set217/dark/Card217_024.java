package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Effect
 * Title: Unlimited Power!
 */
public class Card217_024 extends AbstractNormalEffect {
    public Card217_024() {
        super(Side.DARK, 1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Unlimited_Power, Uniqueness.UNIQUE);
        setLore("Eliciting fear from the opponent gives the dark side a powerful advantage.");
        setGameText("Deploy on table. Emperor and Maul are lost. At the start of your turn, if Sidious on Coruscant (or Insidious Prisoner on table), may place two cards from hand on Reserve Deck, reshuffle, and draw two cards from Reserve Deck. [Immune to Alter.]");
        addIcons(Icon.EPISODE_I, Icon.SIDIOUS, Icon.VIRTUAL_SET_17);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isStartOfYourTurn(game, effectResult, playerId)
                && GameConditions.numCardsInHand(game, playerId) >= 2
                && GameConditions.hasReserveDeck(game, playerId)
                && (GameConditions.canSpot(game, self, Filters.and(Filters.Sidious, Filters.on(Title.Coruscant)))
                || GameConditions.canSpot(game, self, Filters.Insidious_Prisoner))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw cards from Reserve Deck");

            action.appendCost(
                    new PutCardsFromHandOnReserveDeckEffect(action, playerId, 2, 2));
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, playerId));
            action.appendEffect(
                    new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 2));

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {

        // Emperor and Maul are lost.
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> toBeLost = Filters.filterActive(game, self, Filters.or(Filters.Emperor, Filters.Maul));
            if (!toBeLost.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Emperor and Maul are lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(toBeLost) + " lost");

                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, toBeLost));
                actions.add(action);
            }
        }
        return actions;
    }
}
