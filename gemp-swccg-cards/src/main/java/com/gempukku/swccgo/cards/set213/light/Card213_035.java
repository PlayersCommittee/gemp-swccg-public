package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Captain Lando Calrissian
 */
public class Card213_035 extends AbstractAlien {
    public Card213_035() {
        super(Side.LIGHT, 3, 2, 2, 3, 4, "Captain Lando Calrissian", Uniqueness.UNIQUE);
        setLore("Smuggler and gambler.");
        setGameText("[Pilot] 2. If a battle was just initiated here, may exchange a card in hand with an Interrupt of destiny = 4 from Reserve Deck; reshuffle. Once per game, if you have completed a Kessel Run, may return Lando to hand.");
        addPersona(Persona.LANDO);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.GAMBLER, Keyword.CAPTAIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LANDO__RETURN_TO_HAND;

        // Check condition(s)
        if (GameConditions.hasCompletedUtinniEffect(game, self.getOwner(), Filters.Kessel_Run)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Lando)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            PhysicalCard lando = Filters.findFirstActive(game, self, Filters.Lando);
            action.setText("Return to hand");
            action.setActionMsg("Return " + GameUtils.getCardLink(lando) + " to hand");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendEffect(
                    new ReturnCardToHandFromTableEffect(action, lando));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LANDO__EXCHANGE_CARD_WITH_CARD_IN_RESERVE_DECK;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            Filter interruptWithDestiny4 = Filters.and(Filters.destinyEqualTo(4), Filters.Interrupt);

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange card with card in Reserve Deck");
            action.setActionMsg("Exchange a card in hand with a card in Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithCardInReserveDeckEffect(action, playerId, Filters.any, interruptWithDestiny4, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
