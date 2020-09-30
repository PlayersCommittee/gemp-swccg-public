package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
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
        super(Side.LIGHT, 3, 2, 2, 3, 4, "Captain Lando Calrissian", Uniqueness.UNIQUE);
        setLore("Smuggler and gambler.");
        setGameText("Adds 2 to power of anything he pilots (if an [Ind] starship, it is immune to attrition < 5). If opponent just initiated a battle here and Lando is with Kessel Run, may either take any Interrupt of destiny = 4 into hand or lose 2 Force to return Lando to hand.");
        addPersona(Persona.LANDO);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.GAMBLER, Keyword.CAPTAIN);
        setTestingText("Captain Lando Calrissian");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.icon(Icon.INDEPENDENT), Filters.hasPiloting(self)), 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.LANDO__EXCHANGE_CARD_WITH_CARD_IN_RESERVE_DECK;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && GameConditions.isWith(game, self, Filters.Kessel_Run)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            Filter interruptWithDestiny4 = Filters.and(Filters.destinyEqualTo(4), Filters.Interrupt);

            final OptionalGameTextTriggerAction action1 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action1.setText("Exchange card with card in Reserve Deck");
            action1.setActionMsg("Exchange a card in hand with a card in Reserve Deck");
            // Perform result(s)
            action1.appendEffect(
                    new ExchangeCardInHandWithCardInReserveDeckEffect(action1, playerId, Filters.any, interruptWithDestiny4, true));
            actions.add(action1);

            final OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action2.setText("Return to hand");
            action2.setActionMsg("Return " + GameUtils.getCardLink(self) + " to hand");
            // Perform result(s)
            action2.appendCost(
                    new LoseForceEffect(action2, playerId, 2)
            );
            action2.appendEffect(
                    new ReturnCardToHandFromTableEffect(action2, self, Zone.HAND, Zone.LOST_PILE));
            actions.add(action2);

            return Collections.singletonList(action1);
        }
        return null;
    }
}
