package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractRebelResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfCardPileAndTakeCardsIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Rebel
 * Title: Lando, Hero of the Rebellion
 */
public class Card216_035 extends AbstractRebelResistance {
    public Card216_035() {
        super(Side.LIGHT, 3, 3, 2, 3, 5, "Lando, Hero of the Rebellion", Uniqueness.UNIQUE);
        setLore("Leader. Resistance Agent.");
        setGameText("Adds one destiny to total power with Chewie or Jannah (or while piloting). During your turn, may reveal the top three cards of your Reserve Deck, take one starship with a deploy cost < 6 into hand (if possible), and shuffle your Reserve Deck.");
        addPersona(Persona.LANDO);
        addIcons(Icon.VIRTUAL_SET_16, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.RESISTANCE_AGENT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        PilotingCondition pilotingCondition = new PilotingCondition(self);
        WithCondition withJannahOrChewieCondition = new WithCondition(self, Filters.or(Filters.Chewie, Filters.Jannah));
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new AddsDestinyToPowerModifier(self, new OrCondition(pilotingCondition, withJannahOrChewieCondition), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal the top three cards of your Reserve Deck.");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RevealTopCardsOfCardPileAndTakeCardsIntoHandEffect(action, playerId, playerId, Zone.RESERVE_DECK, Filters.and(Filters.starship, Filters.deployCostLessThanOrEqualTo(5.99f)), 3));
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action)
            );
            actions.add(action);
        }

        return actions;
    }
}
