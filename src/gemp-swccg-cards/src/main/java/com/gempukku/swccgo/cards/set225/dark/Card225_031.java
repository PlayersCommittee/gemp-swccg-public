package com.gempukku.swccgo.cards.set225.dark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
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
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ChooseEffectOrderEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

/**
 * Set: Set 25
 * Type: Effect
 * Subtype: Immediate
 * Title: The Client's Bounty
 */
public class Card225_031 extends AbstractImmediateEffect {
    public Card225_031() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "The Client's Bounty", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("One of the most profitable occupations in the galaxy is hunting down and capturing wanted beings. The more notable the quarry, the more profitable the venture.");
        setGameText("Deploy on opponent's just deployed character. Once per turn, if a bounty hunter here, may reveal the top card of each player's Reserve Deck. If this character is about to be captured, retrieve 2 Force (3 if The Client on table) and return this card to your hand. [Immune to Control.]");
        addIcons(Icon.VIRTUAL_SET_25);
        addImmuneToCardTitle(Title.Control);
        addKeywords(Keyword.BOUNTY);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, Filters.and(Filters.opponents(self), Filters.character))) {
            PhysicalCard deployedCard = ((PlayCardResult) effectResult).getPlayedCard();
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameCardId(deployedCard), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, Filters.and(Filters.bounty_hunter, Filters.here(self)))
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.hasReserveDeck(game, opponent)) {
            
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal top card of Reserve Decks");
            action.setActionMsg("Reveal top card of each player's Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));

            // Perform result(s)
            List<StandardEffect> effects = new LinkedList<>();
            effects.add(
                    new RevealTopCardOfReserveDeckEffect(action, playerId, playerId));
            effects.add(
                    new RevealTopCardOfReserveDeckEffect(action, playerId, opponent));

            action.appendEffect(
                    new ChooseEffectOrderEffect(action, effects));

            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        Filter characterFilter = Filters.hasAttached(self);

        // Check condition(s)
        if (TriggerConditions.isAboutToBeCaptured(game, effectResult, characterFilter)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);

            int numForceToRetrieve = GameConditions.canTarget(game, self, Filters.title("The Client")) ? 3 : 2;

            action.setText("Retrieve Force");
            action.setActionMsg("Retrieve " + numForceToRetrieve + " Force, and return " + GameUtils.getCardLink(self) + " to hand");

            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, numForceToRetrieve));
            action.appendEffect(
                    new ReturnCardToHandFromTableEffect(action, self));

            return Collections.singletonList(action);
        }
        return null;
    }
}
