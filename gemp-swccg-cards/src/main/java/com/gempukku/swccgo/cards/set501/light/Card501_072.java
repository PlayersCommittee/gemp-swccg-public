package com.gempukku.swccgo.cards.set501.light;

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
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
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
public class Card501_072 extends AbstractAlien {
    public Card501_072() {
        super(Side.LIGHT, 3, 2, 2, 3, 5, "Captain Lando Calrissian", Uniqueness.UNIQUE);
        setLore("Smuggler and gambler.");
        setGameText("Adds 2 to power of anything he pilots. If a battle was just initiated, may \"smuggle\" (exchange) a card in hand with an Interrupt of destiny = 4 from Reserve deck; reshuffle. Once per game, if a Kessel Run completed, may take Lando into hand from table.");
        addPersona(Persona.LANDO);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.GAMBLER);
        setTestingText("Captain Lando Calrissian");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
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
                    new ExchangeCardInHandWithCardInReserveDeckEffect(action, playerId, Filters.any, interruptWithDestiny4, true));
            return Collections.singletonList(action);
        }
        return null;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.hasCompletedUtinniEffect(game, playerId, Filters.Kessel_Run)
                && GameConditions.isOncePerGame(game, self, GameTextActionId.LANDO__RETURN_TO_HAND)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Return to hand");
            action.setActionMsg("Return " + GameUtils.getCardLink(self) + " to hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ReturnCardToHandFromTableEffect(action, self, Zone.HAND, Zone.LOST_PILE));
            actions.add(action);
        }
        return actions;
    }
}
